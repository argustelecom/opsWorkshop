package ru.argustelecom.box.env.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

@FacesConverter("textArrayConverter")
public class TextArrayConverter implements Converter {

	private static final String SPLIT_PATTERN = "[\r\n]+";

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		return asList((value).split(SPLIT_PATTERN));
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		checkState(value instanceof List);
		return ((List<String>) value).stream().filter(Objects::nonNull).map(Object::toString)
				.collect(joining(lineSeparator()));
	}
}
