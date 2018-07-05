package ru.argustelecom.box.env.dto2;

import static java.lang.String.format;

public abstract class ConvertibleDto implements ru.argustelecom.box.env.dto2.IdentifiableDto {

	public abstract Class<? extends ru.argustelecom.box.env.dto2.DefaultDtoTranslator<?, ?>> getTranslatorClass();

	@Override
	public final String toString() {
		return format("%s {%s translatorClass=%s}", getClass().getSimpleName(), getIdentifiableStringValue(),
				getTranslatorClass().getName());
	}

}