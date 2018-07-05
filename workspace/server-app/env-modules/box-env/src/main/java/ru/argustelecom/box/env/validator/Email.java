package ru.argustelecom.box.env.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import ru.argustelecom.box.env.validator.impl.EmailConstraintValidator;

@Documented
@Retention(RUNTIME)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Constraint(validatedBy = EmailConstraintValidator.class)
public @interface Email {

	String message() default "{ru.argustelecom.box.env.validator.Email.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	boolean allowLocal() default true;

	boolean canBeNull() default false;

}
