package ru.argustelecom.box.env.report;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.haulmont.yarg.structure.BandOrientation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.model.DataLoaderType;
import ru.argustelecom.box.env.report.model.ReportBandModel;

/**
 * DTO для работы с {@linkplain ReportBandModel полосами} типа отчёта, в справочнике типов отчётов.
 */
@Getter
@Builder
@AllArgsConstructor
public class ReportBandDto {

	/**
	 * Идентификатор полосы.
	 */
	private Long id;

	/**
	 * Является ли текущий band корневым для всех остальных.
	 */
	private boolean root;

	/**
	 * Способ получения данных (Groovy, Sql).
	 */
	@Setter
	private DataLoaderType dataLoaderType;

	/**
	 * Направление полосы (вертикальное/горизонтальное).
	 */
	@Setter
	private BandOrientation orientation;

	/**
	 * Название полосы. Может состоять только из латинских букв и символа "_".
	 */
	@Setter
	private String keyword;

	/**
	 * Вложенные полосы.
	 */
	private List<ReportBandDto> children;

	/**
	 * Запрос для получения данных полосы.
	 */
	@Setter
	private String query;

	public String getQuery() {
		return dataLoaderType.equals(DataLoaderType.GROOVY) && StringUtils.isEmpty(query)
				? ReportBandModel.EMPTY_GROOVY_SCRIPT
				: query;
	}
}