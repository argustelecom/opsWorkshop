package ru.argustelecom.box.env.security;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class RoleListDtoTranslator implements DefaultDtoTranslator<RoleListDto, Role> {
	@Override
	public RoleListDto translate(Role role) {
		return new RoleListDto(role.getId(), role.getObjectName(), role.getDescription(), role.getIsSys());
	}
}
