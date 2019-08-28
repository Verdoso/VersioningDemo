package org.greeneyed.versioning.demo.controllers;

//import static org.hamcrest.CoreMatchers.*;
//import static org.hamcrest.Matchers.*;
//import static org.junit.Assert.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.util.Arrays;
import java.util.List;

import org.greeneyed.summer.config.XsltConfiguration;
import org.greeneyed.versioning.demo.VersioningDemoApplication;
import org.greeneyed.versioning.demo.controllers.VersioningDemoAPITest.Config;
import org.greeneyed.versioning.demo.model.App;
import org.greeneyed.versioning.demo.model.MyPojo;
import org.greeneyed.versioning.demo.model.RelatedPojo;
import org.greeneyed.versioning.demo.services.PojoService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import lombok.Data;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { VersioningDemoApplication.class, Config.class })
@TestPropertySource({ "classpath:application-test.properties" })
@ActiveProfiles({ "test" })
@Data
public class VersioningDemoAPITest {

	private static List<MyPojo> TEST_POJOS;

	@Autowired
	private PojoService pojoService;

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mvc;

	@BeforeClass
	public static void setupClass() {
		// Create using PODAM, test DB...
		RelatedPojo related1 = new RelatedPojo("R1", "Related pojo 1");
		TEST_POJOS = Arrays.asList(new MyPojo("anId", "aName", related1), new MyPojo("anotherId", "anotherName", related1));
	}

	@Before
	public void setup() {
		this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Configuration
	@Profile("test")
	protected static class Config {
		@Bean
		public PojoService pojoService() {
			final PojoService pojoService = Mockito.mock(PojoService.class);
			// @formatter:off
			doReturn(TEST_POJOS)
				.when(pojoService)
				.getPojos();			
			// @formatter:on
			return pojoService;
		}
	}

	@Test
	public void testXMLIsFine() throws Exception {
		ResultActions resultActions = this.mvc
				//
				.perform(get("/test?showXMLSource=true"))
				//
				// .andDo(print())
				//
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_XML))
		//
		;
		MvcResult response = resultActions.andReturn();
		// Check the model
		final Object model = response.getModelAndView().getModel().get(XsltConfiguration.XML_SOURCE_TAG);
		assertNotNull("Model object returned is not null", model);
		assertThat("Model object is of the appropriate class", model, instanceOf(App.class));
		// App app = (App) model;
		// Further App checking...

		// Check the XML
		resultActions.andExpect(xpath("/app").exists());
		resultActions.andExpect(xpath("/app/pojos").exists());
		resultActions.andExpect(xpath("/app/pojos/pojo").nodeCount(equalTo(TEST_POJOS.size())));
		for (int i = 0; i < TEST_POJOS.size(); i++) {
			MyPojo testPojo = TEST_POJOS.get(i);
			resultActions.andExpect(xpath("/app/pojos/pojo[" + (i + 1) + "]/@id").string(equalTo(testPojo.getId())));
			resultActions
					.andExpect(xpath("/app/pojos/pojo[" + (i + 1) + "]/@name").string(equalTo(testPojo.getName())));
		}
		// Etc.
	}
}
