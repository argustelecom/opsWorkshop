package ru.argustelecom.box.nri.resources.spec.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by s.kolyada on 18.09.2017.
 */
public class ParameterDataTypeTest {

	@Test
	public void shouldValidateString() throws Exception {
		assertTrue(ParameterDataType.STRING.validate(" 1122 2323"));
		assertTrue(ParameterDataType.STRING.validate(" йцуйцу йцуйцу "));
		assertFalse(ParameterDataType.STRING.validate(" "));
		assertFalse(ParameterDataType.STRING.validate(""));
	}

	@Test
	public void shouldValidateInt() throws Exception {
		assertTrue(ParameterDataType.INTEGER.validate("123"));
		assertTrue(ParameterDataType.INTEGER.validate("-123"));
		assertFalse(ParameterDataType.INTEGER.validate("123.1"));
		assertFalse(ParameterDataType.INTEGER.validate(" "));
		assertFalse(ParameterDataType.INTEGER.validate(""));
	}

	@Test
	public void shouldValidateFloat() throws Exception {
		assertTrue(ParameterDataType.FLOAT.validate("123"));
		assertTrue(ParameterDataType.FLOAT.validate("-123"));
		assertTrue(ParameterDataType.FLOAT.validate("123.1"));
		assertFalse(ParameterDataType.FLOAT.validate("123s.1"));
		assertFalse(ParameterDataType.FLOAT.validate(" "));
		assertFalse(ParameterDataType.FLOAT.validate(""));
	}

	@Test
	public void shouldValidateBoolean() throws Exception {
		assertTrue(ParameterDataType.BOOLEAN.validate("true"));
		assertTrue(ParameterDataType.BOOLEAN.validate("True"));
		assertTrue(ParameterDataType.BOOLEAN.validate("TRUE"));
		assertTrue(ParameterDataType.BOOLEAN.validate("false"));
		assertFalse(ParameterDataType.BOOLEAN.validate("1"));
	}
}