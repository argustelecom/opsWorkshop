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

import ru.argustelecom.box.env.stl.PhoneNumberType;
import ru.argustelecom.box.env.validator.impl.PhoneConstraintValidator;

@Documented
@Retention(RUNTIME)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Constraint(validatedBy = PhoneConstraintValidator.class)
public @interface Phone {
	String message() default "{ru.argustelecom.box.env.validator.Phone.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String regionCode() default "";

	PhoneNumberType[] oneOf() default {};
}
