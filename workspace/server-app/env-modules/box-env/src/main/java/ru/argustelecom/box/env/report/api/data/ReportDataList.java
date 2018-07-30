package ru.argustelecom.box.env.report.api.data;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.reflect.TypeToken;

public final class ReportDataList<T extends ReportData> extends ArrayList<T> {

	private static final long serialVersionUID = 7163706329493162542L;

	@JsonIgnore
	private transient TypeToken<T> typeToken = new TypeToken<T>(getClass()) {
		private static final long serialVersionUID = -345134552721860626L;
	};

	public Class<? super T> getDataClass() {
		return typeToken.getRawType();
	}
}
