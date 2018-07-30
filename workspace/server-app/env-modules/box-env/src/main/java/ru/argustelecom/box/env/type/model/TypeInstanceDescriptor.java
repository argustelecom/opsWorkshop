package ru.argustelecom.box.env.type.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Описывает таблицы с индексами и экземплярами типа. <br/>
 * Также см. {@link SupportUniqueProperty} и {@link ru.argustelecom.box.env.type.BaseTypePropertyUniqueHandler}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TypeInstanceDescriptor {
	IndexTable indexTable();

	InstanceTable instanceTable();
}
