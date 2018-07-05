package ru.argustelecom.box.env.validator.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.validator.Email;

public class EmailConstraintTest extends AbstractConstraintTest {

	private EmailTestBean emailBean;
	private LocalEmailTestBean localEmailBean;

	@Override
	@Before
	public void setup() {
		super.setup();
		this.emailBean = new EmailTestBean();
		this.localEmailBean = new LocalEmailTestBean();
	}

	@Override
	@After
	public void cleanup() {
		super.cleanup();
		this.emailBean = null;
		this.localEmailBean = null;
	}

	@Test
	public void shouldValidateStandardEmail() {
		emailBean.setEmail("box@argustelecom.ru");
		assertValid("Успешная валидация стандартного email", emailBean);
	}

	@Test
	public void shouldValidateEmailWithAllowedNonAlphabeticCharacters() {
		emailBean.setEmail("box.team@argustelecom.ru");
		assertValid("Успешная валидация email с . до @", emailBean);

		emailBean.setEmail("box_team@argustelecom.ru");
		assertValid("Успешная валидация email с _ до @", emailBean);

		emailBean.setEmail("box-team@argustelecom.ru");
		assertValid("Успешная валидация email с - до @", emailBean);

		emailBean.setEmail("box!#$%&'*+/=?^`{|}~team@argustelecom.ru");
		assertValid("Успешная валидация email с ! # $ % & ' * + / = ? ^ ` { | } ~ до @", emailBean);

		emailBean.setEmail("box1_team18@argustelecom.ru");
		assertValid("Успешная валидация email с цифрами до @", emailBean);

		emailBean.setEmail("box@argustelecom1.ru");
		assertValid("Успешная валидация email с цифрами в доменной части", emailBean);

		emailBean.setEmail("box@argus.tele-com.ru");
		assertValid("Успешная валидация email с . и - в доменной части", emailBean);
	}

	@Test
	public void shouldValidateEmailWithIPDomain() {
		emailBean.setEmail("box.team@[10.10.213.64]");
		assertValid("Успешная валидация email IP доменом", emailBean);
	}

	@Test
	public void shouldValidateLocalEmail() {
		localEmailBean.setEmail("box.team@argus");
		assertValid("Успешная валидация email с локальным доменом при allowLocal = true", localEmailBean);

		emailBean.setEmail("box.team@argus");
		assertInvalid("Проваленная валидация email с локальным доменом при при allowLocal = false", emailBean);
	}

	@Test
	public void shouldFailEmailValidationWhenInputInvalidEmail() {
		emailBean.setEmail("box");
		assertInvalid("Некорректный еmail не имеет доменной части", emailBean);

		emailBean.setEmail(".box@argustelecom.ru");
		assertInvalid("Некорректный еmail начинается с .", emailBean);

		emailBean.setEmail("box.@argustelecom.ru");
		assertInvalid("Некорректный еmail заканчивается на .", emailBean);

		emailBean.setEmail("box..team@argustelecom.ru");
		assertInvalid("Некорректный еmail содержит несколько . до @", emailBean);

		emailBean.setEmail("box@.argustelecom.ru");
		assertInvalid("Доменная часть начинается с .", emailBean);
	}

	@Getter
	@Setter
	private static class EmailTestBean {
		@Email(allowLocal = false)
		private String email;
	}

	@Getter
	@Setter
	private static class LocalEmailTestBean {
		@Email(allowLocal = true)
		private String email;
	}

}
