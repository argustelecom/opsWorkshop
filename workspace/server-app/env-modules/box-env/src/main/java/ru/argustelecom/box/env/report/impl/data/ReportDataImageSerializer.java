package ru.argustelecom.box.env.report.impl.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import ru.argustelecom.box.env.report.api.data.ReportDataImage;

public class ReportDataImageSerializer extends JsonSerializer<ReportDataImage> {

	@Override
	public void serialize(ReportDataImage value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		if (value != null) {
			jgen.writeString(value.toRFC2397Uri());
		} else {
			jgen.writeNull();
		}
	}

}
