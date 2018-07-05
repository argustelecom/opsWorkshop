package ru.argustelecom.box.env.task;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionDto {

	private String client;
	private String number;
	private String product;
	private List<String> locations;

	//@formatter:off
	@Builder
	public SubscriptionDto(String client,
						   String number,
						   String product,
						   List<String> locations) {
		this.client = client;
		this.number = number;
		this.product = product;
		this.locations = locations;
	}
	//@formatter:on
}