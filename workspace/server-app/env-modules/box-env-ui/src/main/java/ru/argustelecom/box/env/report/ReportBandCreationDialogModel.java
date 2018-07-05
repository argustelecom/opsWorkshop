package ru.argustelecom.box.env.report;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import ru.argustelecom.box.env.report.model.ReportBandModel;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "reportBandCreationDm")
@PresentationModel
public class ReportBandCreationDialogModel implements Serializable {

	@Inject
	private ReportBandModelAppService reportBandModelAs;

	@Setter
	private Callback<ReportBandModel> callback;

	@Setter
	private ReportType reportType;

	@Getter
	@Setter
	private ReportBandDto parent;

	@Getter
	private ReportBandCreationDto creationDto = new ReportBandCreationDto();

	public void openCreationDlg() {
		RequestContext.getCurrentInstance().update("report_band_creation_form-report_band_creation_dlg");
		RequestContext.getCurrentInstance().execute("PF('reportBandCreationDlgVar').show()");
	}

	public void create() {
		//@formatter:off
		val band = reportBandModelAs.create(
				reportType.getId(),
				creationDto.getDataLoaderType(),
				parent.getId(),
				creationDto.getKeyword(),
				creationDto.getOrientation()
		);
		//@formatter:on
		callback.execute(band);
		clean();
	}

	public void clean() {
		creationDto = new ReportBandCreationDto();
	}

	private static final long serialVersionUID = 8542473399959680123L;

}