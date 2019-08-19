package org.greeneyed.versioning.demo.services;

import java.util.Arrays;
import java.util.List;

import org.greeneyed.versioning.demo.model.MyPojo;
import org.greeneyed.versioning.demo.model.RelatedPojo;
import org.springframework.stereotype.Service;

@Service
public class PojoService {
	public static enum VERSION {
		v1, v2
	};

	public List<MyPojo> getPojos() {
		RelatedPojo related1 = new RelatedPojo("R1", "Related pojo 1");
		return Arrays.asList(
				//
				new MyPojo("anId", "aName", related1),
				//
				new MyPojo("anotherId", "anotherName", related1)
		//
		);
	}
}
