package ru.argustelecom.box.env.security;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.Getter;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class RoleListViewModel extends ViewModel {

	@Inject
	@Getter
	private RoleLazyDataModel lazyDm;

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	private static final long serialVersionUID = -6074444561927622873L;
}
