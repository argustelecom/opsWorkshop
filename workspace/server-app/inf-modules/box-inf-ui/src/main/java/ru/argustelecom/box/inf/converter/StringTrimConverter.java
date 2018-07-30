package ru.argustelecom.box.inf.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("stringTrimConverter")
public class StringTrimConverter implements Serializable, Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
		String trimmed = (submittedValue != null) ? submittedValue.trim() : null;
		return (trimmed == null || trimmed.isEmpty()) ? null : trimmed;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
		return (modelValue != null) ? modelValue.toString() : null;
	}

	private static final long serialVersionUID = -5194990367488603016L;
}
