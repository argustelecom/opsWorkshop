package ru.argustelecom.box.env.lifecycle.impl.event;

import java.lang.annotation.Annotation;

import javax.enterprise.util.AnnotationLiteral;

import lombok.ToString;
import ru.argustelecom.box.env.lifecycle.api.event.RoutedFrom;

@ToString(of = { "value" })
class RoutedFromLiteral extends AnnotationLiteral<RoutedFrom> implements RoutedFrom {

	private static final long serialVersionUID = 779450916007716591L;

	private String value;

	public RoutedFromLiteral(String value) {
		this.value = value;
	}

	@Override
	public String value() {
		return value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return RoutedFrom.class;
	}
}