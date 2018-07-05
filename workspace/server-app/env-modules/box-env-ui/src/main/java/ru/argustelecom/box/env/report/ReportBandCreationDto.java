package ru.argustelecom.box.env.report;

import com.haulmont.yarg.structure.BandOrientation;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.model.DataLoaderType;

@Getter
@Setter
public class ReportBandCreationDto {

	private String keyword;
	private DataLoaderType dataLoaderType;
	private BandOrientation orientation;

}