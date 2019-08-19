package org.greeneyed.versioning.demo.api;

import org.greeneyed.versioning.demo.model.RelatedPojo;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class RelatedPojoAPI {
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