package org.greeneyed.versioning.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.greeneyed.versioning.demo.api.PojoAPI;
import org.greeneyed.versioning.demo.api.Views;
import org.greeneyed.versioning.demo.api.Views.Common;
import org.greeneyed.versioning.demo.api.Views.V1;
import org.greeneyed.versioning.demo.api.Views.V2;
import org.greeneyed.versioning.demo.model.App;
import org.greeneyed.versioning.demo.model.MyPojo;
import org.greeneyed.versioning.demo.model.RelatedPojo;
import org.greeneyed.versioning.demo.services.PojoService;
import org.greeneyed.versioning.demo.services.PojoService.VERSION;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "mapping_jackson")
public class MappingJacksonValueVersioningAPI {
	
	private static final Map<VERSION,Class<? extends Common>> VIEWS_BY_VERSION;
	static {
		VIEWS_BY_VERSION = new HashMap<>();
		VIEWS_BY_VERSION.put(VERSION.v1, V1.class);
		VIEWS_BY_VERSION.put(VERSION.v2, V2.class);
	}
	

	private final PojoService pojoService;

	@Data
	public static class MyPojoAPI implements PojoAPI {
		@JsonView(Views.Common.class)
		private String id;
		@JsonView(Views.Common.class)
		private String name;

		@JsonView(Views.V2.class)
		private RelatedPojoAPI related;

		@JsonView(Views.V1.class)
		@JsonProperty(value = "related_id")
		public String getRelatedId() {
			return related.getId();
		}

		public static MyPojoAPI from(MyPojo myPojo) {
			MyPojoAPI myPojoAPI = new MyPojoAPI();
			BeanUtils.copyProperties(myPojo, myPojoAPI);
			myPojoAPI.setRelated(RelatedPojoAPI.from(myPojo.getRelatedPojo()));
			return myPojoAPI;
		}
	}

	@Data
	public static class RelatedPojoAPI {
		@JsonView(Views.V2.class)
		private String id;
		@JsonView(Views.V2.class)
		private String name;

		public static RelatedPojoAPI from(RelatedPojo relatedPojo) {
			RelatedPojoAPI relatedPojoAPI = new RelatedPojoAPI();
			BeanUtils.copyProperties(relatedPojo, relatedPojoAPI);
			return relatedPojoAPI;
		}
	}

	@RequestMapping(value = "/{version}/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public MappingJacksonValue testInterface(@PathVariable(name = "version") VERSION version) {
		return getPojoAPIList(version);
	}

	private MappingJacksonValue getPojoAPIList(VERSION version) {
		final MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(new App("Listing", getVersionedPojos()));
		mappingJacksonValue.setSerializationView(VIEWS_BY_VERSION.getOrDefault(version, Common.class));
		return mappingJacksonValue;
	}

	private List<PojoAPI> getVersionedPojos() {
		return pojoService.getPojos().stream().map(MyPojoAPI::from).collect(Collectors.toList());
	}
}
