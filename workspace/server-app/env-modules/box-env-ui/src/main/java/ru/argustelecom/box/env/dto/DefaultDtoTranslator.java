package ru.argustelecom.box.env.dto;

import static java.util.stream.Collectors.toList;

import java.util.List;

import ru.argustelecom.system.inf.modelbase.Identifiable;

public interface DefaultDtoTranslator<DTO extends IdentifiableDto, I extends Identifiable> {

	DTO translate(I businessObject);

	default List<DTO> translate(List<I> translate) {
		return translate.stream().map(this::translate).collect(toList());
	}
}