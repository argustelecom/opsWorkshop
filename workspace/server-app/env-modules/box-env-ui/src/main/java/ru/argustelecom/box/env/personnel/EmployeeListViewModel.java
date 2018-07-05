package ru.argustelecom.box.env.personnel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "employeeListVM")
@PresentationModel
public class EmployeeListViewModel extends ViewModel {

	private static final long serialVersionUID = -811237790070227860L;

	@Inject
	@Getter
	private EmployeeLazyDataModel lazyDm;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

}