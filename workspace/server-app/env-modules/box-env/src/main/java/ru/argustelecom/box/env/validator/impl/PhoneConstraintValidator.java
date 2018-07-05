package ru.argustelecom.box.env.validator.impl;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Arrays.asList;

import java.text.Collator;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ru.argustelecom.box.env.stl.PhoneNumberType;
import ru.argustelecom.box.env.validator.Phone;

public class PhoneConstraintValidator implements ConstraintValidator<Phone, CharSequence> {

	private String regionCode;
	private Set<PhoneNumberType> oneOf;

	@Override
	public void initialize(Phone constraintAnnotation) {
		regionCode = constraintAnnotation.regionCode();
		oneOf = new HashSet<>(asList(constraintAnnotation.oneOf()));
	}

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		boolean result = true;

		if (value != null && value.length() > 0) {
			com.google.i18n.phonenumbers.Phonenumber.PhoneNumber number = parse(value, regionCode);
			if (number == null) {
				result = false;
			} else {
				if (!isNullOrEmpty(regionCode)) {
					String realRegionCode = util().getRegionCodeForNumber(number);
					result = result && Collator.getInstance().equals(realRegionCode, regionCode);
				}
				
				if (!oneOf.isEmpty()) {
					PhoneNumberType realType = PhoneNumberType.identify(util().getNumberType(number));
					result = result && oneOf.contains(realType);
				}
			}
		}

		return result;
	}

	private com.google.i18n.phonenumbers.PhoneNumberUtil util() {
		return com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();
	}

	private com.google.i18n.phonenumbers.Phonenumber.PhoneNumber parse(CharSequence value, String regionCode) {
		com.google.i18n.phonenumbers.Phonenumber.PhoneNumber result = null;
		try {
			result = util().parse(value.toString(), emptyToNull(regionCode));
		} catch (com.google.i18n.phonenumbers.NumberParseException e) {
		}

		return result;
	}
}
