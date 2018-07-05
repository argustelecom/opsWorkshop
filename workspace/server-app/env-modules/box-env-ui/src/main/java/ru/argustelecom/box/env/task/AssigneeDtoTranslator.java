package ru.argustelecom.box.env.task;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class AssigneeDtoTranslator implements DefaultDtoTranslator<AssigneeDto, Employee> {
	public AssigneeDto translate(Employee employee) {
		return new AssigneeDto(employee.getId(), employee.getObjectName());
	}
}