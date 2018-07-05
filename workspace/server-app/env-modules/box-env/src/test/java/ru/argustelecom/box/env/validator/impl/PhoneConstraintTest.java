package ru.argustelecom.box.env.validator.impl;

import org.junit.Test;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.stl.PhoneNumberType;
import ru.argustelecom.box.env.validator.Phone;

public class PhoneConstraintTest extends AbstractConstraintTest {

	private static final String US_FIXED_INTERNATIONAL = "+14082334545";

	private static final String RU_FIXED_LOCALE = "88123333660";
	private static final String RU_FIXED_INTERNATIONAL = "+78123333660";

	private static final String RU_MOBILE_INTERNATIONAL = "+79112334545";
	private static final String RU_MOBILE_INTERNATIONAL_F = "+7(911)233-45-45";

	@Test
	public void shouldValidateInternationalPhoneFormat() {
		InternationalPhoneNumberBean phone = new InternationalPhoneNumberBean();

		// Российский номер с country calling code и форматированием
		// Должен успешно пройти валидацию
		phone.setPhone(RU_MOBILE_INTERNATIONAL_F);
		assertValid(phone);

		// Американский номер с country calling code без форматирования
		// Должен успешно пройти валидацию
		phone.setPhone(US_FIXED_INTERNATIONAL);
		assertValid(phone);

		// Российский локальный/национальный номер без country calling code и без форматирования
		// Должен зафейлить валидацию
		phone.setPhone(RU_FIXED_LOCALE);
		assertInvalid(phone);
	}

	@Test
	public void shouldValidateLocalPhoneFormat() {
		RuPhoneNumberBean ru = new RuPhoneNumberBean();
		UsPhoneNumberBean us = new UsPhoneNumberBean();

		// Российский мобильный номер с country calling code в валидаторе, настроенном на российский регион
		// Должен успешно пройти валидацию
		ru.setPhone(RU_MOBILE_INTERNATIONAL);
		assertValid(ru);

		// Американский номер фиксированной связи с country calling code в валидаторе, настроенном на американский
		// регион
		// Должен успешно пройти валидацию
		us.setPhone(US_FIXED_INTERNATIONAL);
		assertValid(us);

		// Российский номер фиксированной связи с country calling code в валидаторе, настроенном на американский регион
		// Американский номер фиксированной связи с country calling code в валидаторе, настроенном на российский регион
		// Должен зафейлить валидацию
		ru.setPhone(US_FIXED_INTERNATIONAL);
		us.setPhone(RU_FIXED_INTERNATIONAL);
		assertInvalid(us);
		assertInvalid(ru);

		// Российский номер фиксированной связи без country calling code в валидаторе, настроенном на российский регион
		// Должен успешно пройти валидацию
		ru.setPhone(RU_FIXED_LOCALE);
		assertValid(ru);
	}

	@Test
	public void shouldValidateMobilePhoneOnly() {
		MobilePhoneNumberBean phone = new MobilePhoneNumberBean();

		// Российский мобильный номер с country calling code в валидаторе, настроенном только на мобильные номера
		// Должен успешно пройти валидацию
		phone.setPhone(RU_MOBILE_INTERNATIONAL);
		assertValid(phone);

		// Российский номер фиксированной связи с country calling code в валидаторе, настроенном на мобильные номера
		// Должен зафейлить валидацию
		phone.setPhone(RU_FIXED_INTERNATIONAL);
		assertInvalid(phone);
	}

	@Getter
	@Setter
	static abstract class PhoneNumberBean {
		private String phone;
	}

	static class InternationalPhoneNumberBean extends PhoneNumberBean {
		@Override
		@Phone
		public String getPhone() {
			return super.getPhone();
		}
	}

	static class RuPhoneNumberBean extends PhoneNumberBean {
		@Override
		@Phone(regionCode = "RU")
		public String getPhone() {
			return super.getPhone();
		}
	}

	static class UsPhoneNumberBean extends PhoneNumberBean {
		@Override
		@Phone(regionCode = "US")
		public String getPhone() {
			return super.getPhone();
		}
	}

	static class MobilePhoneNumberBean extends PhoneNumberBean {
		@Override
		@Phone(oneOf = { PhoneNumberType.MOBILE })
		public String getPhone() {
			return super.getPhone();
		}
	}
}
