package ru.argustelecom.box.env.billing.bill.model;

import static ru.argustelecom.box.env.billing.bill.model.ChargesType.USAGE;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "serviceId", "providerId", "withoutContract" }, callSuper = true)
public class ChargesRawByUsage extends ChargesRaw {

	private Long serviceId;
	private Long providerId;
	private boolean withoutContract;

	@Builder
	public ChargesRawByUsage(Long analyticTypeId, Long optionId, Long serviceId, Long providerId,
			boolean withoutContract, BigDecimal taxRate, Date startDate, Date endDate, BigDecimal sum,
			BigDecimal discountSum, boolean row, AnalyticTypeError error) {
		super(analyticTypeId, optionId, USAGE, taxRate, startDate, endDate, sum, discountSum, row, error);
		this.serviceId = serviceId;
		this.providerId = providerId;
		this.withoutContract = withoutContract;
	}

}
