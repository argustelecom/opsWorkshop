package ru.argustelecom.ops.inf.queue.api.worker;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Inherited
@Documented
@Stereotype
@Transactional
@RequestScoped
@Target({ TYPE })
@Retention(RUNTIME)
public @interface QueueHandlerBean {

}
