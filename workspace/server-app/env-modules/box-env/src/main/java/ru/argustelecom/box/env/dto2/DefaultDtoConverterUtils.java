package ru.argustelecom.box.env.dto2;

import java.util.List;
import java.util.stream.Collectors;

import ru.argustelecom.system.inf.modelbase.Identifiable;

public class DefaultDtoConverterUtils {

	public static <T extends Identifiable, R extends IdentifiableDto> List<R> translate(
			DefaultDtoTranslator<R, T> dtoTranslator, List<T> translate) {
		return translate.stream().map(dtoTranslator::translate).collect(Collectors.toList());
	}

}