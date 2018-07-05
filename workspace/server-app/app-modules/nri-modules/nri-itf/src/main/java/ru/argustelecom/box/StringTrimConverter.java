package ru.argustelecom.box;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Конвертер для обрезания пробелов в полях ввода текста
 * Created by s.kolyada on 08.09.2017.
 */
@FacesConverter
public class StringTrimConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
		String trimmed = (submittedValue != null) ? submittedValue.trim() : null;
		return (trimmed == null || trimmed.isEmpty()) ? null : trimmed;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
		return (modelValue != null) ? modelValue.toString() : "";
	}
}
