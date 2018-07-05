package ru.argustelecom.box.env.numerationpattern;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "numerationPatternVm")
@PresentationModel
public class NumerationPatternViewModel extends ViewModel {

	private static final long serialVersionUID = 1373237226982167632L;

	@PostConstruct
	@Override
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}
}
