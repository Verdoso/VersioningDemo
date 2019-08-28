package org.greeneyed.versioning.demo.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.greeneyed.versioning.demo.VersioningDemoApplication;
import org.greeneyed.versioning.demo.controllers.VersioningAPITest.Config;
import org.greeneyed.versioning.demo.model.MyPojo;
import org.greeneyed.versioning.demo.model.RelatedPojo;
import org.greeneyed.versioning.demo.services.PojoService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RunWith(Parameterized.class)
@WebAppConfiguration
@ContextConfiguration(classes = { VersioningDemoApplication.class, Config.class })
@ActiveProfiles({ "test" })
@Data
@Slf4j
public class VersioningAPITest {

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	private static List<MyPojo> TEST_POJOS;

	@Autowired
	private PojoService pojoService;

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mvc;

	@Parameter(0)
	public String controller;

	@Parameter(1)
	public String version;

	@Parameters(name="Path /{0}/{1}/test")
	public static String[][] urlMatrix() {
		String[] controllers = new String[] {
				//
				ClassicVersioningAPI.API_PATH,
				//
				ViewsVersioningAPI.API_PATH,
				//
				MappingJacksonValueFilterVersioningAPI.API_PATH,
				//
				MappingJacksonValueViewsVersioningAPI.API_PATH,
				//
				JoltVersioningAPI.API_PATH
				//
		};
		String[] versions = new String[] { "v1", "v2" };

		String[][] matrix = new String[controllers.length * versions.length][2];
		for (int i = 0; i < controllers.length; i++) {
			for (int j = 0; j < versions.length; j++) {
				matrix[(i * versions.length) + j][0] = controllers[i];
				matrix[(i * versions.length) + j][1] = versions[j];
			}
		}
		return matrix;
	}

	@BeforeClass
	public static void setupClass() {
		// Create using PODAM, test DB...
		RelatedPojo related1 = new RelatedPojo("R1", "Related pojo 1");
		TEST_POJOS = Arrays.asList(new MyPojo("anId", "aName", related1),
				new MyPojo("anotherId", "anotherName", related1));
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
			//
			doReturn(TEST_POJOS).when(pojoService).getPojos();
			//
			return pojoService;
		}
	}

	@Test
	public void testJsonIsFine() throws Exception {
		log.info("Testing controller {}, version {}", controller, version);
		ResultActions resultActions = this.mvc
				//
				.perform(get("/" + controller + "/" + version + "/test"))
				//
				// .andDo(print())
				//
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		//
		;
		// Check the JSON

		resultActions.andExpect(jsonPath("$.id", is("Listing")));
		resultActions.andExpect(jsonPath("$.pojoAPIs").isArray());
		resultActions.andExpect(jsonPath("$.pojoAPIs", hasSize(TEST_POJOS.size())));
		for (int i = 0; i < TEST_POJOS.size(); i++) {
			MyPojo testPojo = TEST_POJOS.get(i);
			resultActions.andExpect(jsonPath("$.pojoAPIs[" + i + "].id", is(testPojo.getId())));
			resultActions.andExpect(jsonPath("$.pojoAPIs[" + i + "].name", is(testPojo.getName())));
			if ("v1".equals(version)) {
				resultActions
						.andExpect(jsonPath("$.pojoAPIs[" + i + "].related_id", is(testPojo.getRelatedPojo().getId())));
				resultActions.andExpect(jsonPath("$.pojoAPIs[" + i + "].related").doesNotExist());
			} else if ("v2".equals(version)) {
				resultActions.andExpect(jsonPath("$.pojoAPIs[" + i + "].related_id").doesNotExist());
				resultActions.andExpect(jsonPath("$.pojoAPIs[" + i + "].related").exists());
				resultActions
						.andExpect(jsonPath("$.pojoAPIs[" + i + "].related.id", is(testPojo.getRelatedPojo().getId())));
				resultActions.andExpect(
						jsonPath("$.pojoAPIs[" + i + "].related.name", is(testPojo.getRelatedPojo().getName())));
			}
		}
		// Etc.
	}
}
