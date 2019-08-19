package org.greeneyed.versioning.demo.api.v2;

import org.greeneyed.versioning.demo.api.PojoAPI;
import org.greeneyed.versioning.demo.model.MyPojo;
import org.springframework.beans.BeanUtils;

import lombok.Data;

@Data
public class MyPojoAPI_v2 implements PojoAPI {
	private String id;
	private String name;
	private RelatedPojoAPI_v2 related;

	public static MyPojoAPI_v2 from(MyPojo myPojo) {
		MyPojoAPI_v2 myPojoAPI_v2 = new MyPojoAPI_v2();
		BeanUtils.copyProperties(myPojo, myPojoAPI_v2);
		myPojoAPI_v2.setRelated(RelatedPojoAPI_v2.from(myPojo.getRelatedPojo()));
		return myPojoAPI_v2;
	}
}