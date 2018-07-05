package ru.argustelecom.box.env.report;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.type.CurrentType;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "reportTypeEditorVm")
@PresentationModel
public class ReportTypeEditorViewModel extends ViewModel {

	private static final long serialVersionUID = 6270561726210943167L;

	@Inject
	private CurrentType currentType;

	@Inject
	private ReportTypeDtoTranslator reportTypeDtoTr;

	@Getter
	private ReportTypeDto reportTypeDto;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		if (currentType != null && currentType.getValue() instanceof ReportType) {
			reportTypeDto = reportTypeDtoTr.translate((ReportType) currentType.getValue());
		}
	}

}
