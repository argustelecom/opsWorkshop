package ru.argustelecom.box.inf.service;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Stereotype;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Stereotype
@Inherited
@RequestScoped
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface DtoTranslator {

}
