package ru.argustelecom.box.env.billing.bill.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.stl.Money;

@Getter
@Setter
public class ChargesAggDto {
	private String subjectName;
	private Money sumWithTax;
	private Money sumWithoutTax;
	private Money tax;

	@Builder
	public ChargesAggDto(String subjectName, Money sumWithTax, Money sumWithoutTax, Money tax) {
		this.subjectName = subjectName;
		this.sumWithTax = sumWithTax;
		this.sumWithoutTax = sumWithoutTax;
		this.tax = tax;
	}

}