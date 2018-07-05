package ru.argustelecom.box.env.stl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Locale;

import org.junit.Test;

import ru.argustelecom.system.inf.exception.SystemException;

public class PhoneNumberTest {

	@Test
	public void shouldCreateByInternationalFormattedInputAndIdentifyMainRuFixedlineNumberParams() {
		PhoneNumber number = PhoneNumber.create("+78123333660");
		checkRuFixedlineNumber(number);
	}

	@Test
	public void shouldCreateByNationalFormattedInputWithRegionCodeAndIdentifyMainRuFixedlineNumberParams() {
		PhoneNumber number = PhoneNumber.create("88123333660", "RU");
		checkRuFixedlineNumber(number);
	}

	@Test
	public void shouldCreateByNationalFormattedInputWithCountryCodeAndIdentifyMainRuFixedlineNumberParams() {
		PhoneNumber number = PhoneNumber.create("88123333660", 7);
		checkRuFixedlineNumber(number);
	}

	@Test
	public void shouldCreateByNationalFormattedInputWithLocaleAndIdentifyMainRuFixedlineNumberParams() {
		PhoneNumber number = PhoneNumber.create("88123333660", new Locale("ru", "RU"));
		checkRuFixedlineNumber(number);
	}

	private void checkRuFixedlineNumber(PhoneNumber number) {
		assertNotNull(number);

		assertEquals("RU", number.regionCode());
		assertEquals(7, number.countryCode());

		assertTrue(number.isFixedLine());

		assertFalse(number.isMobile());
		assertFalse(number.isPremiumRate());
		assertFalse(number.isTollFree());
		assertFalse(number.isVoIP());
	}

	@Test(expected = SystemException.class)
	public void shouldFailCreationByNationalFormattedInputWithoutRegionCode() {
		PhoneNumber.create("88123333660");
	}

	@Test
	public void shouldDetectMobilePhoneNumber() {
		PhoneNumber number = PhoneNumber.create("+79213333660");
		assertTrue(number.isMobile());
		assertEquals(PhoneNumberType.MOBILE, number.numberType());
	}

	@Test
	public void shouldDetectTollfreePhoneNumber() {
		PhoneNumber number = PhoneNumber.create("+78003333660");
		assertTrue(number.isTollFree());
		assertEquals(PhoneNumberType.TOLL_FREE, number.numberType());
	}

	@Test
	public void shouldDetectPremiumratePhoneNumber() {
		PhoneNumber number = PhoneNumber.create("+78093333660");
		assertTrue(number.isPremiumRate());
		assertEquals(PhoneNumberType.PREMIUM_RATE, number.numberType());
	}

	@Test
	public void shouldFormatPhoneNumber() {
		PhoneNumber number = PhoneNumber.create("+7(812)3333-66-0");
		assertEquals("8 (812) 333-36-60", number.formatNational());
		assertEquals("+7 812 333-36-60", number.formatInternational());
		assertEquals("+78123333660", number.formatE164());
		assertEquals("tel:+7-812-333-36-60", number.formatRCF3966());
	}

	@Test
	public void shouldStoreValueInE164Format() {
		PhoneNumber number = PhoneNumber.create("+7(809)3333-66-0");
		assertEquals(number.formatE164(), number.value());
	}

	@Test
	public void shouldExtractPhoneNumbersFromCustomText() {
		Iterable<PhoneNumberEntry> entries = PhoneNumber.extractAll(
				"Василий! Позвони мне по номеру 88002000600 или +7(921)3333-66-0 до вечера! +333-00 рубля отдам", "RU");

		Iterator<PhoneNumberEntry> it = entries.iterator();

		assertTrue(it.hasNext());
		assertEquals("+78002000600", it.next().number().value());

		assertTrue(it.hasNext());
		assertEquals("+79213333660", it.next().number().value());

		assertFalse(it.hasNext());
	}
}
