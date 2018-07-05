package ru.argustelecom.box.env.personnel;

import javax.inject.Inject;

import lombok.Getter;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.person.PersonDataDto;
import ru.argustelecom.box.env.person.PersonDataFrameModel;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

import java.util.ResourceBundle;

@PresentationModel
public class EmployeeDataFrameModel extends PersonDataFrameModel {

	private static final long serialVersionUID = 6051753154718053597L;

	@Inject
	private EmployeeDataDtoTranslator translator;

	@Inject
	private EmployeeDataAppService employeeDataAs;

	@Getter
	private EmployeeDataDto employeeDataDto;

	public void preRender(Employee employee) {
		employeeDataDto = translator.translate(employee);
	}

	@Override
	public void save() {
		super.save();

		//@formatter:off
		employeeDataAs.editEmployeeData(
			employeeDataDto.getEmployeeId(), 
			employeeDataDto.getAppointment(),
			employeeDataDto.getPersonnelNumber()
		);
		//@formatter:on
	}

	public void fire() {
		employeeDataAs.fireEmployee(employeeDataDto.getEmployeeId());
	}

	@Override
	public PersonDataDto getPersonDataDto() {
		return employeeDataDto;
	}

}