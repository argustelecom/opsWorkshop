package ru.argustelecom.box.env.report;

import ru.argustelecom.box.env.report.model.ReportBandModel;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ReportBandDtoTranslator {

	public ReportBandDto translate(ReportBandModel reportBandModel) {
		//@formatter:off
		return ReportBandDto.builder()
					.id(reportBandModel.getId())
					.root(reportBandModel.getParent() == null)
					.dataLoaderType(reportBandModel.getDataLoaderType())
					.orientation(reportBandModel.getOrientation())
					.keyword(reportBandModel.getKeyword())
					.query(reportBandModel.getQuery())
				.build();
		//@formatter:on
	}
}