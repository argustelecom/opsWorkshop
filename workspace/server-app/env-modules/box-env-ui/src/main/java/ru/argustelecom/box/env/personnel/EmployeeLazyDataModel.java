package ru.argustelecom.box.env.personnel;

import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployee;
import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper;
import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper.EMAIL;
import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper.FIRST_NAME;
import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper.ID;
import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper.LAST_NAME;
import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper.PERSONNEL_NUMBER;
import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper.SECOND_NAME;
import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper.USER_NAME;
import static ru.argustelecom.box.env.personnel.EmployeeLazyDataModel.EmployeeSort;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.JPQLConvertibleDtoFilterModel;
import ru.argustelecom.box.env.JPQLConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class EmployeeLazyDataModel extends
		JPQLConvertibleDtoLazyDataModel<LoginEmployee, EmployeeListDto, LoginEmployeeQueryWrapper, EmployeeSort> {

	@Inject
	private EmployeeFilterModel employeeFilterModel;

	@Inject
	private EmployeeListDtoTranslator employeeListDtoTranslator;

	@PostConstruct
	private void postConstruct() {
		initPaths();
	}

	private void initPaths() {
		addPath(EmployeeSort.id, ID);
		addPath(EmployeeSort.userName, USER_NAME);
		addPath(EmployeeSort.number, PERSONNEL_NUMBER);
		addPath(EmployeeSort.lastName, LAST_NAME);
		addPath(EmployeeSort.firstName, FIRST_NAME);
		addPath(EmployeeSort.secondName, SECOND_NAME);
		addPath(EmployeeSort.email, EMAIL);
	}

	@Override
	protected Class<EmployeeSort> getSortableEnum() {
		return EmployeeSort.class;
	}

	@Override
	protected DefaultDtoTranslator<EmployeeListDto, LoginEmployee> getDtoTranslator() {
		return employeeListDtoTranslator;
	}

	@Override
	protected JPQLConvertibleDtoFilterModel<LoginEmployee, LoginEmployeeQueryWrapper> getFilterModel() {
		return employeeFilterModel;
	}

	public enum EmployeeSort {
		id, userName, number, lastName, firstName, secondName, email
	}

	private static final long serialVersionUID = -5342654997800161925L;
}