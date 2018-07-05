package ru.argustelecom.box.env.report.impl.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import com.haulmont.yarg.structure.ReportOutputType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.api.ReportTemplateFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportOutputFormatMappings {

	public static final String DEFAULT_REPORT_NAME = "report";
	public static final Map<ReportTemplateFormat, Set<ReportOutputFormat>> OUTPUT_FORMATS_MAPPING;
	public static final Map<ReportOutputFormat, ReportOutputType> OUTPUT_MAPPING;

	static {
		Map<ReportTemplateFormat, Set<ReportOutputFormat>> typesMapping = new EnumMap<>(ReportTemplateFormat.class);
		typesMapping.put(ReportTemplateFormat.DOCX, asSet(ReportOutputFormat.DOCX, ReportOutputFormat.PDF));
		typesMapping.put(ReportTemplateFormat.XLSX, asSet(ReportOutputFormat.XLSX, ReportOutputFormat.PDF));
		typesMapping.put(ReportTemplateFormat.HTML,
				asSet(ReportOutputFormat.HTML, ReportOutputFormat.PDF, ReportOutputFormat.CSV));
		OUTPUT_FORMATS_MAPPING = Collections.unmodifiableMap(typesMapping);

		Map<ReportOutputFormat, ReportOutputType> outputMapping = new EnumMap<>(ReportOutputFormat.class);
		outputMapping.put(ReportOutputFormat.DOCX, ReportOutputType.docx);
		outputMapping.put(ReportOutputFormat.XLSX, ReportOutputType.xlsx);
		outputMapping.put(ReportOutputFormat.HTML, ReportOutputType.html);
		outputMapping.put(ReportOutputFormat.PDF, ReportOutputType.pdf);
		outputMapping.put(ReportOutputFormat.CSV, ReportOutputType.csv);
		OUTPUT_MAPPING = Collections.unmodifiableMap(outputMapping);
	}

	public static Collection<ReportOutputFormat> getReportTypesBy(ReportTemplateFormat templateFormat) {
		checkArgument(templateFormat != null);
		return ReportOutputFormatMappings.OUTPUT_FORMATS_MAPPING.get(templateFormat);
	}

	public static Collection<ReportOutputFormat> getReportTypesBy(String templateMimeType) {
		//@formatter:off
		return ReportOutputFormatMappings.OUTPUT_FORMATS_MAPPING.keySet().stream()
				.filter(templateFormat -> templateFormat.mimeType().equals(templateMimeType))
				.findFirst().map(ReportOutputFormatMappings.OUTPUT_FORMATS_MAPPING::get).orElse(Collections.emptySet());
		//@formatter:on
	}

	public static void validate(ReportTemplateFormat templateFormat, ReportOutputFormat reportOutputFormat) {
		Set<ReportOutputFormat> supportedTypes = OUTPUT_FORMATS_MAPPING.get(templateFormat);
		checkState(supportedTypes.contains(reportOutputFormat));
	}

	@SafeVarargs
	@SuppressWarnings("varargs")
	private static <T> Set<T> asSet(T... values) {
		return unmodifiableSet(newHashSet(values));
	}
}