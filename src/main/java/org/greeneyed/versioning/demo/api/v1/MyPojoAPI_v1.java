package org.greeneyed.versioning.demo.api.v1;

import org.greeneyed.versioning.demo.api.PojoAPI;
import org.greeneyed.versioning.demo.model.MyPojo;
import org.springframework.beans.BeanUtils;

import lombok.Data;

@Data
public class MyPojoAPI_v1 implements PojoAPI {
	private String id;
	private String name;
	private String related_id;

	public static MyPojoAPI_v1 from(MyPojo myPojo) {
		MyPojoAPI_v1 myPojoAPI_v1 = new MyPojoAPI_v1();
		BeanUtils.copyProperties(myPojo, myPojoAPI_v1);
		myPojoAPI_v1.setRelated_id(myPojo.getRelatedPojo().getId());
		return myPojoAPI_v1;
	}
}