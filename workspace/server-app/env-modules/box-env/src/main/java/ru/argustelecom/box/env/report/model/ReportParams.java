package ru.argustelecom.box.env.report.model;

import ru.argustelecom.box.env.type.model.TypeInstance;

/**
 * Необходим для задания параметров при создании отчета
 */
public class ReportParams extends TypeInstance<ReportType> {
	public ReportParams(Long id) {
		super(id);
	}

	private static final long serialVersionUID = -1687946489252832359L;
}
