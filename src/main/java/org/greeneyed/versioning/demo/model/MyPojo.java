package org.greeneyed.versioning.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyPojo {
	private String id;
	private String name;

	private RelatedPojo relatedPojo;
}
