package ru.argustelecom.box.env.product.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.CommodityRdo;
import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.env.report.api.data.format.ReportBandDef;

@Getter
@Setter
public class ProductRdo extends ReportData {

	private String name;

	@ReportBandDef(name = "ProductEntries")
	private ReportDataList<CommodityRdo> commodities;

	@Builder
	public ProductRdo(Long id, String name, ReportDataList<CommodityRdo> commodities) {
		super(id);
		this.name = name;
		this.commodities = commodities;
	}

}