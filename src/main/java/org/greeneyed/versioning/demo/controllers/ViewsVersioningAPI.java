package org.greeneyed.versioning.demo.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.greeneyed.versioning.demo.api.MyPojoAPI;
import org.greeneyed.versioning.demo.api.PojoAPI;
import org.greeneyed.versioning.demo.api.Views;
import org.greeneyed.versioning.demo.model.App;
import org.greeneyed.versioning.demo.services.PojoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = ViewsVersioningAPI.API_PATH)
public class ViewsVersioningAPI {

	public static final String API_PATH = "views";
	private final PojoService pojoService;

	@RequestMapping(value = "/v1/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@JsonView(Views.V1.class)
	public App testInterfaceV1() {
		return getPojoAPIList();
	}

	@RequestMapping(value = "/v2/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@JsonView(Views.V2.class)
	public App testInterfaceV2() {
		return getPojoAPIList();
	}

	private App getPojoAPIList() {
		return new App("Listing", getVersionedPojos());
	}

	private List<PojoAPI> getVersionedPojos() {
		return pojoService.getPojos().stream().map(MyPojoAPI::from).collect(Collectors.toList());
	}
}
