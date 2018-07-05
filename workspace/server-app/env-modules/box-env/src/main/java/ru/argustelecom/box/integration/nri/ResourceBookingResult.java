package ru.argustelecom.box.integration.nri;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by s.kolyada on 19.12.2017.
 */
@Getter
@Setter
public class ResourceBookingResult {

	private Boolean success = true;

	private String message;

	public ResourceBookingResult(String message) {
		this.message = message;
	}

	public ResourceBookingResult(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}
}
