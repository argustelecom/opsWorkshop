package ru.argustelecom.box.inf.exception;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
@Named("modenaErrorVm")
public class ModenaErrorViewModel extends ViewModel {

	private static final String HOME_VIEW_ID = "/views/env/home/HomeView.xhtml";

	@Inject
	private OutcomeConstructor outcomeConstructor;


	public String returnToHomePage() {
		return outcomeConstructor.construct(HOME_VIEW_ID);
	}

	private static final long serialVersionUID = 3416408517361211029L;
}
