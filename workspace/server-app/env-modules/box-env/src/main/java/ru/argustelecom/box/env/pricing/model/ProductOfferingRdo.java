package ru.argustelecom.box.env.pricing.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import ru.argustelecom.box.env.product.model.ProductRdo;
import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.format.ReportDataFormat;

@Getter
@Setter
public class ProductOfferingRdo extends ReportData {

	private ProductRdo product;

	@ReportDataFormat(value = "#,###.00")
	private BigDecimal value;

	@ReportDataFormat(value = "#,###.00")
	private BigDecimal valueWithoutTax;

	@ReportDataFormat(value = "0.00")
	private BigDecimal taxValue;

	private String currency;

	@Builder
	public ProductOfferingRdo(Long id, ProductRdo product, BigDecimal value, BigDecimal valueWithoutTax,
			BigDecimal taxValue, String currency) {
		super(id);
		this.product = product;
		this.value = value;
		this.valueWithoutTax = valueWithoutTax;
		this.taxValue = taxValue;
		this.currency = currency;
	}

}