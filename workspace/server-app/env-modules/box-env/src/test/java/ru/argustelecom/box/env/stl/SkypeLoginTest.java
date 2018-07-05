package ru.argustelecom.box.env.stl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SkypeLoginTest {

	@Test
	public void shouldCreateSkypeLoginByValidStringRepresentation() {
		SkypeLogin skype = SkypeLogin.create("boxteam");

		assertNotNull(skype);
		assertEquals("boxteam", skype.value());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreationSkypeLoginByInvalidStringRepresentation() {
		SkypeLogin.create("box!");
	}

}
