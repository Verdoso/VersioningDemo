package org.greeneyed.versioning.demo.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.greeneyed.summer.config.JoltViewConfiguration.JoltModelAndView;
import org.greeneyed.versioning.demo.api.PojoAPI;
import org.greeneyed.versioning.demo.model.App;
import org.greeneyed.versioning.demo.model.MyPojo;
import org.greeneyed.versioning.demo.model.RelatedPojo;
import org.greeneyed.versioning.demo.services.PojoService;
import org.greeneyed.versioning.demo.services.PojoService.VERSION;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "jolt")
public class JoltVersioningAPI {
	private final PojoService pojoService;

	@Data
	public static class MyPojoAPI implements PojoAPI {
		private String id;
		private String name;

		private RelatedPojoAPI related;

		public static MyPojoAPI from(MyPojo myPojo) {
			MyPojoAPI myPojoAPI = new MyPojoAPI();
			BeanUtils.copyProperties(myPojo, myPojoAPI);
			myPojoAPI.setRelated(RelatedPojoAPI.from(myPojo.getRelatedPojo()));
			return myPojoAPI;
		}
	}

	@Data
	public static class RelatedPojoAPI {
		private String id;
		private String name;

		public static RelatedPojoAPI from(RelatedPojo relatedPojo) {
			RelatedPojoAPI relatedPojoAPI = new RelatedPojoAPI();
			BeanUtils.copyProperties(relatedPojo, relatedPojoAPI);
			return relatedPojoAPI;
		}
	}

	@RequestMapping(value = "/{version}/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ModelAndView testInterface(@PathVariable(name = "version") VERSION version) {
		return getPojoAPIList(version);
	}

	private ModelAndView getPojoAPIList(VERSION version) {
		return new JoltModelAndView("jolt-" + version, new App("Listing", getVersionedPojos()), HttpStatus.OK);
	}

	private List<PojoAPI> getVersionedPojos() {
		return pojoService.getPojos().stream().map(MyPojoAPI::from).collect(Collectors.toList());
	}
}
