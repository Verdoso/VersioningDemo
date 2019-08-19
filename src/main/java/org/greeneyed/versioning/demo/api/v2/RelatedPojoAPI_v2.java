package org.greeneyed.versioning.demo.api.v2;

import org.greeneyed.versioning.demo.model.RelatedPojo;
import org.springframework.beans.BeanUtils;

import lombok.Data;

@Data
public class RelatedPojoAPI_v2 {
	private String id;
	private String name;

	public static RelatedPojoAPI_v2 from(RelatedPojo relatedPojo) {
		RelatedPojoAPI_v2 relatedPojoAPI_v2 = new RelatedPojoAPI_v2();
		BeanUtils.copyProperties(relatedPojo, relatedPojoAPI_v2);
		return relatedPojoAPI_v2;
	}
}