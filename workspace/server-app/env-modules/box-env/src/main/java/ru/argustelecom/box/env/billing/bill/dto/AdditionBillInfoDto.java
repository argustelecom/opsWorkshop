package ru.argustelecom.box.env.billing.bill.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionBillInfoDto {

	private List<AnalyticDto> analytics;

	@Builder
	public AdditionBillInfoDto(List<AnalyticDto> analytics) {
		this.analytics = analytics;
	}
}