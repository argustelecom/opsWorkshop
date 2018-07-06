package ru.argustelecom.box.env.report.api.data.format;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReportImageFormat {

	int width() default 100;

	int height() default 100;
}