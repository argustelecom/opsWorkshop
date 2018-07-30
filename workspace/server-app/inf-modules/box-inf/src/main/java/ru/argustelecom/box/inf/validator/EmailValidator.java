package ru.argustelecom.box.inf.validator;

import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * Валидатор адреса электронной почты согласно <a href="http://www.faqs.org/rfcs/rfc2822.html">RFC 2822</a>
 * <p>Email делится знаком "@" на две части: local и domain.
 * Domain часть состоит из атомарных частей, согласно <a href="https://tools.ietf.org/html/rfc2822#section-3.4.1">3.4.1. Addr-spec specification</a>
 * <p>Domain часть может быть провалидирована с разрешением local domain и без разрешения local domain
 * <p>Подробнее см. {@link org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator}
 * <a href="https://habrahabr.ru/post/274985/"></a>
 * <p>Расположен в box-inf, т.к. должен быть доступен из любого места.
 */
public class EmailValidator {
	private static final org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator emailValidator = new org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator();
	/**
	 * Если allowLocal = false - запрещаем локальный домен (остальное по аналогии с Email Validator от Hibernate)
	 */
	private static final String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
	private static final String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
	private static final String strictDOMAIN = ATOM + "+(\\." + ATOM + "+)+";

	/**
	 * Регулярное выражение для domain части e-mail адреса (всё, что следует после '@')
	 * Domain часть состоит из IP или из атомарных частей (см. <a href="https://www.w3.org/Protocols/rfc822/#z66">RFC822 - 3.3. LEXICAL TOKENS</a>)
	 */
	private static final Pattern strictDomainPattern = java.util.regex.Pattern.compile(
			strictDOMAIN + "|" + IP_DOMAIN, CASE_INSENSITIVE
	);

	public static boolean validate(CharSequence value, ConstraintValidatorContext context, boolean allowLocal) {
		if (value == null || value.length() == 0) {
			return false;
		}
		boolean isValid = emailValidator.isValid(value.toString(), context);
		if (isValid && !allowLocal) {
			String[] emailParts = value.toString().split("@", 3);
			isValid = strictDomainPattern.matcher(emailParts[1]).matches();
		}
		return isValid;
	}

	public static boolean validate(CharSequence value, ConstraintValidatorContext context, boolean allowLocal, boolean canBeNull) {
		return canBeNull && value == null || validate(value, context, allowLocal);
	}

	public static boolean validate(CharSequence value, boolean allowLocal) {
		return validate(value, null, allowLocal);
	}

	public static boolean validate(CharSequence value) {
		return validate(value, false);
	}

}