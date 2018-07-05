package ru.argustelecom.box.env.dto2;

import ru.argustelecom.system.inf.modelbase.Identifiable;

public interface DefaultDtoTranslator<DTO extends ru.argustelecom.box.env.dto2.IdentifiableDto, I extends Identifiable> {

	DTO translate(I businessObject);

}