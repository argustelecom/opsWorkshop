package ru.argustelecom.box.env.validator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.After;
import org.junit.Before;

public class AbstractConstraintTest {

	private Validator validator;

	@Before
	public void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();
	}

	@After
	public void cleanup() {
		this.validator = null;
	}

	protected <T> void assertValid(T bean) {
		Set<ConstraintViolation<T>> violations = validator.validate(bean);
		assertTrue(violations.isEmpty());
	}

	protected <T> void assertValid(String message, T bean) {
		Set<ConstraintViolation<T>> violations = validator.validate(bean);
		assertTrue(message, violations.isEmpty());
	}

	protected <T> void assertInvalid(T bean) {
		Set<ConstraintViolation<T>> violations = validator.validate(bean);
		assertEquals(1, violations.size());
	}

	protected <T> void assertInvalid(String message, T bean) {
		Set<ConstraintViolation<T>> violations = validator.validate(bean);
		assertEquals(message, 1, violations.size());
	}

}
