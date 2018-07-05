package ru.argustelecom.box.env.lifecycle.impl.event;

import java.lang.annotation.Annotation;

import javax.enterprise.util.AnnotationLiteral;

import lombok.ToString;
import ru.argustelecom.box.env.lifecycle.api.event.RoutedTo;

@ToString(of = { "value" })
class RoutedToLiteral extends AnnotationLiteral<RoutedTo> implements RoutedTo {

	private static final long serialVersionUID = -4723519068942568090L;
	
	private String value;

	public RoutedToLiteral(String value) {
		this.value = value;
	}

	@Override
	public String value() {
		return value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return RoutedTo.class;
	}
}