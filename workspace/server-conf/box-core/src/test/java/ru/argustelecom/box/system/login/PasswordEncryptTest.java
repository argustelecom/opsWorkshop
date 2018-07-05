package ru.argustelecom.box.system.login;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.argustelecom.box.inf.login.PasswordEncrypt;

public class PasswordEncryptTest {

	private static final String PASSWORD = "password";

	@Test
	public void shouldGenerateEquivalentEncrypts() {
		PasswordEncrypt encrypt1 = new PasswordEncrypt(false, PASSWORD);
		PasswordEncrypt encrypt2 = new PasswordEncrypt(false, PASSWORD);
		assertEquals(encrypt1, encrypt2);
	}

	@Test
	public void shouldEqualsRawPassword() {
		PasswordEncrypt encrypt = new PasswordEncrypt(false, PASSWORD);
		assertEquals(PASSWORD, encrypt.getDecryptedValue());
	}

}