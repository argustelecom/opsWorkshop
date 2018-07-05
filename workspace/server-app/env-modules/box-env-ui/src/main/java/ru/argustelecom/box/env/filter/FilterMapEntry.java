package ru.argustelecom.box.env.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.task.TaskListViewState;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FilterMapEntry {
	String value();

	/**
	 * Необходимость в нескольких трансляторах исходит из того, что в одно поле могут сетится экземпляры разных классов.
	 * Для примера - assignee в {@link TaskListViewState}.
	 */

	@SuppressWarnings("rawtypes")
	Class<? extends DefaultDtoTranslator>[] translator() default DefaultDtoTranslator.class;

	//TODO FIXME когда будет рефакторинг BusinessObjectDto
	boolean isBusinessObjectDto() default false;
}
