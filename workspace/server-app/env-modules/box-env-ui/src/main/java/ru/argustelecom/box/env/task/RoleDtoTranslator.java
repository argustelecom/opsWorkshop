package ru.argustelecom.box.env.task;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class RoleDtoTranslator implements DefaultDtoTranslator<RoleDto, Role> {
	public RoleDto translate(Role role) {
		return new RoleDto(role.getId(), role.getObjectName());
	}
}