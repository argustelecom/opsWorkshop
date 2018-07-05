package ru.argustelecom.box.env.billing.bill.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import ru.argustelecom.box.env.stl.Money;

@Getter
public class ChargesDto {

	private List<ChargesAggDto> chargesAggDtos;
	private Money sumWithTaxTotal;
	private Money sumWithoutTax;
	private Money taxTotal;

	public ChargesDto(List<ChargesAggDto> chargesAggDtos) {
		this.chargesAggDtos = chargesAggDtos;
		this.sumWithTaxTotal = calculateTotal(
				chargesAggDtos.stream().map(ChargesAggDto::getSumWithTax).collect(Collectors.toList()));
		this.sumWithoutTax = calculateTotal(
				chargesAggDtos.stream().map(ChargesAggDto::getSumWithoutTax).collect(Collectors.toList()));
		this.taxTotal = calculateTotal(chargesAggDtos.stream().map(ChargesAggDto::getTax).collect(Collectors.toList()));
	}

	private Money calculateTotal(List<Money> monies) {
		Money sum = new Money(BigDecimal.ZERO);
		for (Money money : monies) {
			sum = sum.add(money);
		}
		return sum;
	}

}
