package ru.argustelecom.box.env.personnel;

import javax.inject.Inject;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.PartyRepository.LoginEmployee;
import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class EmployeeListDtoTranslator implements DefaultDtoTranslator<EmployeeListDto, LoginEmployee> {

	@Inject
	private LoginListDtoTranslator loginListDtoTranslator;

	@Override
	public EmployeeListDto translate(LoginEmployee loginEmployee) {
		//@formatter:off
		return EmployeeListDto.builder()
				.id(loginEmployee.getId())
				.firstName(((Person) loginEmployee.getEmployee().getParty()).getName().firstName())
				.secondName(((Person) loginEmployee.getEmployee().getParty()).getName().secondName())
				.lastName(((Person) loginEmployee.getEmployee().getParty()).getName().lastName())
				.personnelNumber(loginEmployee.getEmployee().getPersonnelNumber())
				.fired(loginEmployee.getEmployee().getFired())
				.loginListDto(loginListDtoTranslator.translate(loginEmployee.getLogin()))
				.build();
		//@formatter:on
	}
}
