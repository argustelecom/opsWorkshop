package ru.argustelecom.box.env.billing.bill;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "billCardVm")
@PresentationModel
public class BillCardViewModel extends ViewModel {

	public static final String VIEW_ID = "/views/env/billing/bill/BillCardView.xhtml";

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	private static final long serialVersionUID = 9199222646716244363L;
}
