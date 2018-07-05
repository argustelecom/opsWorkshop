package ru.argustelecom.box.nri.logicalresources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.faces.validator.ValidatorException;

import static org.junit.Assert.*;
@RunWith(PowerMockRunner.class)
public class PhoneNumberMaskValidatorTest {

	@InjectMocks
	private PhoneNumberMaskValidator testingClass;
	@Test(expected = ValidatorException.class)
	public void nonValid() throws Exception {
		testingClass.validate(null,null, "1234567890123456");
	}

	@Test
	public void validMask() throws Exception {
		testingClass.validate(null,null, "(812)999-99-99");
	}
	@Test(expected = ValidatorException.class)
	public void nonValidCompilationError() throws Exception {
		testingClass.validate(null,null, "+++???***");
	}


}