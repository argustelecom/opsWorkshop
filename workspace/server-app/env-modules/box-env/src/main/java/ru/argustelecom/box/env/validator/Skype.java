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
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;

import ru.argustelecom.box.env.stl.SkypeLogin;

@Documented
@Retention(RUNTIME)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Constraint(validatedBy = {})
@Pattern(regexp = SkypeLogin.REGEXP)
@ReportAsSingleViolation
public @interface Skype {

	String message() default "{ru.argustelecom.box.env.validator.Skype.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
