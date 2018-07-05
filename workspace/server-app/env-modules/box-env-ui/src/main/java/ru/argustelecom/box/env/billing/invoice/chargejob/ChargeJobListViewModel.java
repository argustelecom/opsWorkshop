package ru.argustelecom.box.env.billing.invoice.chargejob;

import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
@Named(value = "chargeJobListVm")
public class ChargeJobListViewModel extends ViewModel {

	private static final long serialVersionUID = -1024638645971417319L;

	@Inject
	@Getter
	private ChargeJobLazyDataModel lazyDm;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

}