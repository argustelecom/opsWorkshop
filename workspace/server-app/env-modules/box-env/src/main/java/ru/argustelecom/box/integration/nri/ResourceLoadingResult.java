package ru.argustelecom.box.integration.nri;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceLoadingResult {

	private Boolean success = true;

	private String message;

	public ResourceLoadingResult(String message) {
		this.message = message;
	}

	public ResourceLoadingResult(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}
}