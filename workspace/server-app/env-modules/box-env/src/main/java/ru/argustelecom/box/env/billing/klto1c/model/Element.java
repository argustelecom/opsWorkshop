package ru.argustelecom.box.env.billing.klto1c.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang.StringUtils;

/**
 * Аннотация для маппинга поля класса на элементы данных из выгрузки.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Element {

	/**
	 * Название ключа элемента в выгрузке.
	 */
	String name();

	String dateFormat() default StringUtils.EMPTY;

	boolean simpleField() default true;

	/**
	 * Обязательность поля для обработки в нашей системе.
	 */
	boolean required() default false;

	/**
	 * Обязательность поля в формате выгрузки.
	 */
	boolean requiredInFormat() default false;

}