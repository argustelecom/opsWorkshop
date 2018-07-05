package ru.argustelecom.box.env.report.impl.data;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ru.argustelecom.box.env.report.api.data.ReportData;
import ru.argustelecom.box.env.report.api.data.ReportDataImage;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

public final class ReportDataMapper {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	static {
		OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		SimpleModule reportDataModule = new SimpleModule("ReportData", new Version(1, 0, 0, "RC", null, null));
		reportDataModule.addSerializer(ReportDataImage.class, new ReportDataImageSerializer());

		OBJECT_MAPPER.registerModule(reportDataModule);
	}

	private ReportDataMapper() {
	}

	public static <T extends ReportData> String map(T data) {
		return marshallToJson(data);
	}

	public static <T extends ReportData> String map(ReportDataList<T> data) {
		return marshallToJson(data);
	}

	private static String marshallToJson(Object data) {
		checkArgument(data != null);
		try {

			return OBJECT_MAPPER.writeValueAsString(data);

		} catch (IOException cause) {
			throw LocaleUtils.exception(SystemException.class, cause, "Unable to marshall ReportData of class {0}",
					data.getClass().getSimpleName());
		}
	}
}
