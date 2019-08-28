package org.greeneyed.versioning.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.greeneyed.versioning.demo.api.PojoAPI;
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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = MappingJacksonValueFilterVersioningAPI.API_PATH)
public class MappingJacksonValueFilterVersioningAPI {

	static final String API_PATH = "mapping_jackson_filter";
	private final PojoService pojoService;

	@Data
	@EqualsAndHashCode(callSuper = true)
	private static final class VersionPropertyFilter extends SimpleBeanPropertyFilter {
		private final String versionToCheck;		
		@Override
		public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider,
				PropertyWriter writer) throws Exception {
			if (include(writer)) {
				if (writer.getMetadata() == null || writer.getMetadata().getDescription() == null
						|| writer.getMetadata().getDescription().contains(versionToCheck)) {
					writer.serializeAsField(pojo, jgen, provider);
				}
			} else if (!jgen.canOmitFields()) { // since 2.3
				writer.serializeAsOmittedField(pojo, jgen, provider);
			}
		}
	};
	
	private static final PropertyFilter Filter_v1 = new VersionPropertyFilter("v1.0");
	private static final PropertyFilter Filter_v2 = new VersionPropertyFilter("v2.0");

	private static final Map<VERSION, PropertyFilter> VIEWS_BY_VERSION;
	static {
		VIEWS_BY_VERSION = new HashMap<>();
		VIEWS_BY_VERSION.put(VERSION.v1, Filter_v1);
		VIEWS_BY_VERSION.put(VERSION.v2, Filter_v2);
	}

	@Data
	@JsonFilter("VersionFilter")
	public static class MyPojoAPI implements PojoAPI {
		@JsonPropertyDescription("v1.0,v2.0")
		private String id;
		@JsonPropertyDescription("v1.0,v2.0")
		private String name;

		@JsonPropertyDescription("v2.0")
		private RelatedPojoAPI related;

		@JsonPropertyDescription("v1.0")
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
		private String id;
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
		final MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(
				new App("Listing", getVersionedPojos()));
		mappingJacksonValue
				.setFilters(new SimpleFilterProvider().addFilter("VersionFilter", VIEWS_BY_VERSION.get(version)));
		return mappingJacksonValue;
	}

	private List<PojoAPI> getVersionedPojos() {
		return pojoService.getPojos().stream().map(MyPojoAPI::from).collect(Collectors.toList());
	}
}
