package ru.argustelecom.box.env.personnel;

import java.sql.SQLException;

import javax.inject.Inject;

import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.person.avatar.PersonAvatarService;
import ru.argustelecom.box.env.person.avatar.model.PersonAvatar;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class EmployeeDataDtoTranslator {

	@Inject
	private PersonAvatarService personAvatarService;

	public EmployeeDataDto translate(Employee employee) {
		Person person = (Person) employee.getParty();

		//@formatter:off
		EmployeeDataDto employeeDataDto = EmployeeDataDto.builder()
			
			 // Собственные данные сотрудника (Employee) 
			.employeeId(employee.getId())
			.employeeName(employee.getObjectName())
			.appointment(employee.getAppointment())
			.personnelNumber(employee.getPersonnelNumber())
			.fired(employee.getFired())
			
			 // Данные персоны (Person)
			.personId(person.getId())
			.prefix(person.getName().prefix())
			.firstName(person.getName().firstName())
			.secondName(person.getName().secondName())
			.lastName(person.getName().lastName())
			.suffix(person.getName().suffix())
			.note(person.getNote())
			
		.build(); 
		//@formatter:on

		PersonAvatar personAvatar = personAvatarService.findAvatar(person);

		try {
			if (personAvatar != null) {
				employeeDataDto.setImageInputStream(personAvatar.getImage().getBinaryStream());
				employeeDataDto.setImageFormatName(personAvatar.getFormatName());
			}
		} catch (SQLException ignore) {
		}

		return employeeDataDto;
	}

}