package ru.argustelecom.box.env.dto;

import static java.lang.String.format;

public abstract class ConvertibleDto implements IdentifiableDto {

	public abstract Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass();

	@Override
	public final String toString() {
		return format("%s {%s translatorClass=%s}", getClass().getSimpleName(), getIdentifiableStringValue(),
				getTranslatorClass().getName());
	}

}