package ru.argustelecom.box.env.validator.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ru.argustelecom.box.inf.validator.EmailValidator;

import ru.argustelecom.box.env.validator.Email;

/**
 * Валидатор адреса электронной почты
 */
public class EmailConstraintValidator implements ConstraintValidator<Email, CharSequence> {

	private boolean allowLocal;
	private boolean canBeNull;

	@Override
	public void initialize(Email constraintAnnotation) {
		allowLocal = constraintAnnotation.allowLocal();
		canBeNull = constraintAnnotation.canBeNull();
	}

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		return EmailValidator.validate(value, context, allowLocal, canBeNull);
	}

}
