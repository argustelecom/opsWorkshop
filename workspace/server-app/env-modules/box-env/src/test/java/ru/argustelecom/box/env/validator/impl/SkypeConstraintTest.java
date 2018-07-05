package ru.argustelecom.box.env.validator.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Strings;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.validator.Skype;

public class SkypeConstraintTest extends AbstractConstraintTest {

	private SkypeTestBean skypeBean;

	@Override
	@Before
	public void setup() {
		super.setup();
		this.skypeBean = new SkypeTestBean();
	}

	@Override
	@After
	public void cleanup() {
		super.cleanup();
		this.skypeBean = null;
	}

	@Test
	public void shouldValidateCorrectSkypeLogin() {
		skypeBean.setSkype("boxskype");
		assertValid("Корректный логин скайпа из одного латинского слова", skypeBean);

		skypeBean.setSkype("boxskype99");
		assertValid("Корректный логин скайпа из одного латинского слова с цифрами", skypeBean);

		skypeBean.setSkype("box.skype-99");
		assertValid("Корректный логин скайпа из одного латинского слова с цифрами, точкой и дефисом", skypeBean);

		skypeBean.setSkype(Strings.repeat("a", 6));
		assertValid("Корректный логин скайпа содержит не менее 6 символов", skypeBean);

		skypeBean.setSkype(Strings.repeat("a", 32));
		assertValid("Корректный логин скайпа содержит не более 32 символа", skypeBean);
	}

	@Test
	public void shouldFailValidationWhenSkypeLoginIsIncorrect() {
		skypeBean.setSkype("1boxskype");
		assertInvalid("Некорректный логин скайпа начинается не с буквы", skypeBean);

		skypeBean.setSkype("box");
		assertInvalid("Некорректный логин скайпа содержит меньше 6 символов", skypeBean);

		skypeBean.setSkype(Strings.repeat("a", 33));
		assertInvalid("Некорректный логин скайпа содержит больше 32 символов", skypeBean);

		skypeBean.setSkype("box'n'bux$!");
		assertInvalid("Некорректный логин скайпа содержит символы, отличные от разрешенных", skypeBean);
	}

	@Getter
	@Setter
	private static class SkypeTestBean {
		@Skype
		private String skype;
	}

}
