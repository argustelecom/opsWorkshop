package ru.argustelecom.box.env.type.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Явно описывает схему и таблицу, хранящую экземпляры TypeInstance, а также колонки, где хранятся значения свойств и
 * идентификаторов
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface InstanceTable {
	String schema();

	String table();

	String idColumn() default "id";

	String propsColumn() default "properties";
}
