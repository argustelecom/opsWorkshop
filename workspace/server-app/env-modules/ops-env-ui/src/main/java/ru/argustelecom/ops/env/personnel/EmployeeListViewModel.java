package ru.argustelecom.ops.env.personnel;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "employeeListVM")
@PresentationModel
public class EmployeeListViewModel extends ViewModel {

	private static final long serialVersionUID = -811237790070227860L;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

}