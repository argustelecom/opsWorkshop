package ru.argustelecom.box.env.billing.bill.dto;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.stl.Money;

@Getter
@Setter
public class AnalyticDto {

	private Long analyticTypeId;
	private String name;
	private Money sum;

	public AnalyticDto(Long analyticTypeId, String name, Money sum) {
		this.analyticTypeId = analyticTypeId;
		this.name = name;
		this.sum = sum;
	}

}