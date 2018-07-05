package ru.argustelecom.box.nri.logicalresources.phone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDtoTranslator;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by b.bazarov on 31.10.2017.
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberDtoTranslatorTest {

	@InjectMocks
	private PhoneNumberDtoTranslator testingClass;

	@Mock
	private ResourceInstanceDtoTranslator resTranslator;

	@Test
	public void shouldValidateInput() throws Exception {
		assertNull(testingClass.translate(null));
	}

	@Test
	public void shouldTranslate() throws Exception {
		PhoneNumber phoneNumber = new PhoneNumber(1L);

		PhoneNumberDto dto = testingClass.translate(phoneNumber);

		assertNotNull(dto);
		assertEquals(new Long(1),dto.getId());

		Mockito.verify(resTranslator, Mockito.times(1)).translateLazy(Mockito.anyObject());
	}

}