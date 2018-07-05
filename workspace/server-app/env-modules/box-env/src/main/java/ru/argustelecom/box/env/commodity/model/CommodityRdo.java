package ru.argustelecom.box.env.commodity.model;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.api.data.ReportData;

@Getter
@Setter
public class CommodityRdo extends ReportData {

	private String name;
	private String categoryName;
	private Map<String, String> properties;

	@Builder
	public CommodityRdo(Long id, String name, String categoryName, Map<String, String> properties) {
		super(id);
		this.name = name;
		this.categoryName = categoryName;
		this.properties = properties;
	}

}