package ru.argustelecom.box.env.contractor;

import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
@Named(value = "supplierListVm")
public class SupplierListViewModel extends ViewModel {

	private static final long serialVersionUID = -8491798733734428741L;

	@Inject
	@Getter
	private SupplierLazyDataModel lazyDm;


	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

}