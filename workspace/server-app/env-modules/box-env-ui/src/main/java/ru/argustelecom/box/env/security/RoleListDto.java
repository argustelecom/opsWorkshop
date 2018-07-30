package ru.argustelecom.box.env.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleListDto extends ConvertibleDto {
	private Long id;
	private String name;
	private String desc;
	private boolean sys;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return RoleListDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Role.class;
	}
}
