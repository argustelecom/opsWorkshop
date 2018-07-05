package ru.argustelecom.box.env.type;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.type.model.TypePropertyAccessor;

@Getter
@AllArgsConstructor
public class TypePropertyGroupPanelDto {

	private Long id;
	private String name;
	private Integer ordinalNumber;
	private List<TypePropertyAccessor<?>> accessors;

	public TypePropertyGroupPanelDto(List<TypePropertyAccessor<?>> accessors) {
		this.accessors = accessors;
	}

}