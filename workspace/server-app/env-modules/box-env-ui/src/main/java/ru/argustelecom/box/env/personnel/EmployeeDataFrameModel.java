package ru.argustelecom.box.env.personnel;

import javax.inject.Inject;

import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.person.Person;
import ru.argustelecom.box.env.person.PersonDataFrameModel;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class EmployeeDataFrameModel extends PersonDataFrameModel {

	private static final long serialVersionUID = 6051753154718053597L;

	@Inject
	private EmployeeDataAppService employeeDataAs;
	private Employee employee;

	public void preRender(Employee employee) {
		this.employee = employee;
	}

	@Override
	public void save() {
		super.save();

		//@formatter:off
		employeeDataAs.editEmployeeData(
			employee.getId(),
			employee.getAppointment(),
			employee.getPersonnelNumber()
		);
		//@formatter:on
	}

	@Override
	public Person getPerson() {
		return null;
	}

	public void fire() {
		employeeDataAs.fireEmployee(employee.getId());
	}

}