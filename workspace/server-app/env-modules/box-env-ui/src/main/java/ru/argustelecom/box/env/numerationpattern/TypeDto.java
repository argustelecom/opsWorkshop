package ru.argustelecom.box.env.numerationpattern;

import lombok.Getter;
import ru.argustelecom.box.env.dto.IdentifiableDto;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
public class TypeDto implements IdentifiableDto {
	private Long id;
	private String name;
	private Class<? extends Identifiable> typeClass;

	public TypeDto(Long id, String name, Class<? extends Identifiable> typeClass) {
		this.id = id;
		this.name = name;
		this.typeClass = typeClass;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return typeClass;
	}

}