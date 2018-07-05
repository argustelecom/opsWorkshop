package ru.argustelecom.box.env.task;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskInfoDto {

	private String customer;
	private String personalAccountNumber;
	private String product;
	private List<String> addresses;

	@Builder
	public TaskInfoDto(String customer, String personalAccountNumber, String product, List<String> addresses) {
		this.customer = customer;
		this.personalAccountNumber = personalAccountNumber;
		this.product = product;
		this.addresses = addresses;
	}

}
