package ru.argustelecom.box.env.report.api.data;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//@formatter:off
@JsonAutoDetect(
	fieldVisibility    = ANY,
	getterVisibility   = NONE, 
	setterVisibility   = NONE, 
	creatorVisibility  = ANY,
	isGetterVisibility = NONE
)//@formatter:on
@Getter
@Setter
@ToString
public abstract class ReportData {

	private Long id;

	public ReportData(Long id) {
		Preconditions.checkNotNull(id);
		this.id = id;
	}

	public static Date translate(Date sqlDate) {
		return sqlDate != null ? new Date(sqlDate.getTime()) : null;
	}

}