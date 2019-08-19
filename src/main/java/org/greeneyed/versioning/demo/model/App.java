package org.greeneyed.versioning.demo.model;

import java.util.List;

import org.greeneyed.versioning.demo.api.PojoAPI;
import org.greeneyed.versioning.demo.api.Views;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class App {
	@JsonView(Views.Common.class)
	private String id;
	@JsonView(Views.Common.class)
	private List<PojoAPI> pojoAPIs;
}
