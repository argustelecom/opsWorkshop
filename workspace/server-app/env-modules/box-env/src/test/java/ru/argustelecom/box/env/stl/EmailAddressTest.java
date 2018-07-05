package ru.argustelecom.box.env.stl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class EmailAddressTest {

	@Test
	public void shouldCreateEmailAddressByValidStringRepresentation() {
		EmailAddress email = EmailAddress.create("box.team@argustelecom.ru");

		assertNotNull(email);
		assertEquals("box.team@argustelecom.ru", email.value());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreationEmailAddressByInvalidStringRepresentation() {
		EmailAddress.create("this.is.not.email");
	}

}
