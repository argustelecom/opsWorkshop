package ru.argustelecom.box.inf.service;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

@Stereotype
@Inherited
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@ApplicationScoped
@Transactional(TxType.REQUIRED)
public @interface DomainService {

}
