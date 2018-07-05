package ru.argustelecom.box.nri.resources.spec.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by s.kolyada on 18.09.2017.
 */
public class ParameterSpecificationTest {

	@Test
	public void shouldValidate() throws Exception {
		ParameterSpecification specification = new ParameterSpecification();
		specification.setDataType(ParameterDataType.INTEGER);
		specification.setRequired(false);

		assertTrue(specification.validate("123"));
		assertTrue(specification.validate(""));
		assertTrue(specification.validate(" "));
		assertFalse(specification.validate("ss"));

		specification.setRequired(true);
		assertTrue(specification.validate("123"));
		assertFalse(specification.validate(""));
		assertFalse(specification.validate(" "));

		specification.setRegex("\\D+");
		assertTrue(specification.validate("ss"));
		assertFalse(specification.validate("123"));
	}
}