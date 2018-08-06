package ru.argustelecom.ops.inf.validator.type;

import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;

import static java.util.Optional.ofNullable;

public interface HasAttribute {
	default Optional<Object> getAttribute(String attribute, UIComponent component) {
		return ofNullable(component.getAttributes().get(attribute));
	}

	default Optional<Object> getAttribute(String attribute, FaceletContext context) {
		return ofNullable(context.getAttribute(attribute));
	}
}
