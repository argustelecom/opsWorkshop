package ru.argustelecom.box.env.dto;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.util.List;
import java.util.stream.Collectors;

import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.modelbase.NamedObject;

@DtoTranslator
public class BusinessObjectDtoTranslator {

	@SuppressWarnings("unchecked")
	public <T extends Identifiable & NamedObject> BusinessObjectDto<T> translate(T bo) {
		return new BusinessObjectDto<>(initializeAndUnproxy(bo));
	}

	public <T extends Identifiable & NamedObject> List<BusinessObjectDto<T>> translate(List<T> boList) {
		return boList.stream().map(this::translate).collect(Collectors.toList());
	}

}