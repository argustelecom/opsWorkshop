package ru.argustelecom.box.env.lifecycle.impl.event;

import java.lang.annotation.Annotation;

import javax.enterprise.util.AnnotationLiteral;

import lombok.ToString;
import ru.argustelecom.box.env.lifecycle.api.event.RoutingCompleted;

@ToString(of = { "oldState", "newState" })
class RoutingCompletedLiteral extends AnnotationLiteral<RoutingCompleted> implements RoutingCompleted {

	private static final long serialVersionUID = 9122593128658439577L;

	private String oldState;
	private String newState;

	public RoutingCompletedLiteral(String oldState, String newState) {
		this.oldState = oldState;
		this.newState = newState;
	}

	@Override
	public String oldState() {
		return oldState;
	}

	@Override
	public String newState() {
		return newState;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return RoutingCompleted.class;
	}
}