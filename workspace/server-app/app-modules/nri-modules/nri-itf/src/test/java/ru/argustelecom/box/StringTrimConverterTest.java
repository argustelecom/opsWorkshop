package ru.argustelecom.box;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by s.kolyada on 09.09.2017.
 */
public class StringTrimConverterTest {

	private StringTrimConverter converter = new StringTrimConverter();

	@Test
	public void shouldConvertToString() throws Exception {
		String res = converter.getAsString(null, null, "123");
		assertNotNull(res);
		assertEquals("123", res);
	}

	@Test
	public void shouldConvertNullToString() throws Exception {
		String res = converter.getAsString(null, null, null);
		assertNotNull(res);
		assertEquals("",res);
	}

	@Test
	public void shouldConvertToObject() throws Exception {
		Object res = converter.getAsObject(null, null, "123");
		assertNotNull(res);
		assertEquals("123", res);
	}

	@Test
	public void shouldConvertEmptuStrToObject() throws Exception {
		Object res = converter.getAsObject(null, null, "");
		assertNull(res);
	}

	@Test
	public void shouldConvertNullToObject() throws Exception {
		Object res = converter.getAsObject(null, null, null);
		assertNull(res);
	}
}