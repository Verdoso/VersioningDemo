package org.greeneyed.versioning.demo.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.greeneyed.versioning.demo.api.PojoAPI;
import org.greeneyed.versioning.demo.api.v1.MyPojoAPI_v1;
import org.greeneyed.versioning.demo.api.v2.MyPojoAPI_v2;
import org.greeneyed.versioning.demo.model.App;
import org.greeneyed.versioning.demo.services.PojoService;
import org.greeneyed.versioning.demo.services.PojoService.VERSION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "classic")
public class ClassicVersioningAPI {

	private final PojoService pojoService;

	@RequestMapping(value = "/{version}/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public App testInterface(@PathVariable(name = "version") VERSION version) {
		App app = new App("Listing", getVersionedPojos(version));
		return app;
	}

	private List<PojoAPI> getVersionedPojos(VERSION version) {
		return pojoService.getPojos().stream().map(myPojo -> {
			switch (version) {
			case v1:
				return MyPojoAPI_v1.from(myPojo);
			case v2:
				return MyPojoAPI_v2.from(myPojo);
			default:
				return null;
			}
		}).collect(Collectors.toList());
	}
}
