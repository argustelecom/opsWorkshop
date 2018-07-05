package ru.argustelecom.box.env.report.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.env.report.api.ReportTemplateFormat.getReportTemplateFormatBy;
import static ru.argustelecom.box.env.report.impl.ReportMetadata.getQueryName;
import static ru.argustelecom.box.env.report.impl.utils.ReportOutputFormatMappings.validate;
import static ru.argustelecom.box.env.type.model.Ordinal.comparator;

import java.io.Serializable;
import java.util.function.Consumer;

import javax.inject.Inject;

import com.haulmont.yarg.structure.Report;
import com.haulmont.yarg.structure.ReportBand;
import com.haulmont.yarg.structure.ReportTemplate;
import com.haulmont.yarg.structure.impl.BandBuilder;
import com.haulmont.yarg.structure.impl.ReportBuilder;
import com.haulmont.yarg.structure.impl.ReportParameterImpl;
import com.haulmont.yarg.structure.impl.ReportTemplateImpl;

import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.impl.utils.ReportOutputFormatMappings;
import ru.argustelecom.box.env.report.model.ReportBandModel;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.env.report.model.ReportParams;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.exception.SystemException;

@ApplicationService
public class DatabaseReportBuilder implements Serializable {

	@Inject
	private TypeFactory typeFactory;

	public Report createReport(ReportType reportType, ReportParams reportParams, ReportModelTemplate template,
			ReportOutputFormat outputFormat) {
		checkArgument(reportType.getTemplates().contains(template));
		validate(getReportTemplateFormatBy(template.getMimeType()), outputFormat);

		ReportBuilder builder = new ReportBuilder().template(createTemplate(template, outputFormat));
		typeFactory.createAccessors(reportParams)
				.forEach(accessor -> builder.parameter(new ReportParameterImpl(accessor.getProperty().getKeyword(),
						accessor.getProperty().getKeyword(), accessor.getProperty().isRequired(),
						accessor.getValue().getClass(), accessor.getAsString())));

		iterateOverChildren(reportType.getRootBand(), bandModel -> builder.band(createReportBand(bandModel)));

		return builder.build();
	}

	private ReportBand createReportBand(ReportBandModel bandModel) {
		BandBuilder builder = new BandBuilder();
		builder.name(bandModel.getObjectName());
		builder.query(getQueryName(bandModel.getObjectName()), bandModel.getQuery(),
				bandModel.getDataLoaderType().getName());
		builder.orientation(bandModel.getOrientation());
		iterateOverChildren(bandModel, reportBandModel -> builder.child(createReportBand(reportBandModel)));
		return builder.build();
	}

	private void iterateOverChildren(ReportBandModel bandModel, Consumer<ReportBandModel> consumer) {
		bandModel.getChildren().stream().sorted(comparator()).forEach(consumer);
	}

	private ReportTemplate createTemplate(ReportModelTemplate template, ReportOutputFormat reportOutputFormat) {
		try {
			//@formatter:off
			return new ReportTemplateImpl(
					ReportTemplate.DEFAULT_TEMPLATE_CODE,
					template.getFileName(),
					template.getFileName(),
					template.getBinaryStream(),
					ReportOutputFormatMappings.OUTPUT_MAPPING.get(reportOutputFormat)
			);
			//@formatter:on
		} catch (Exception e) {
			throw new SystemException(e);
		}
	}

	private static final long serialVersionUID = 6910589096052032419L;
}
