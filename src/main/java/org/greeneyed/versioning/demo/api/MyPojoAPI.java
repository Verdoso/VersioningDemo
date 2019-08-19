package org.greeneyed.versioning.demo.api;

import org.greeneyed.versioning.demo.model.MyPojo;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class MyPojoAPI implements PojoAPI {
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