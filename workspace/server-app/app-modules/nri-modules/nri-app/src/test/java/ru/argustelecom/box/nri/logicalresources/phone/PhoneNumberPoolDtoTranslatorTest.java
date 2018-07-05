package ru.argustelecom.box.nri.logicalresources.phone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by s.kolyada on 31.10.2017.
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberPoolDtoTranslatorTest {

	@Mock
	private PhoneNumberDtoTranslator phoneNumberDtoTranslator;

	@InjectMocks
	private PhoneNumberPoolDtoTranslator translator;

	private PhoneNumberPool pool = PhoneNumberPool.builder().id(1L).name("name").build();

	@Before
	public void setUp() throws Exception {
		pool.setPhoneNumbers(Collections.singletonList(new PhoneNumber()));
	}

	@Test
	public void shouldValidateInput() throws Exception {
		assertNull(translator.translate(null));
	}

	@Test
	public void shouldTranslate() throws Exception {
		PhoneNumberPoolDto dto = translator.translate(pool);

		assertNotNull(dto);
		assertEquals(new Long(1), dto.getId());
		assertFalse(dto.getPhoneNumbers().isEmpty());
	}

	@Test
	public void shouldTranslateLazy() throws Exception {
		PhoneNumberPoolDto dto = translator.translateLazy(pool);

		assertNotNull(dto);
		assertEquals(new Long(1), dto.getId());
		assertTrue(dto.getPhoneNumbers().isEmpty());
	}

	@Test
	public void shouldReturnNullWhenTranslatingNull() throws Exception {
		PhoneNumberPoolDto dtoOne = translator.translate(null);
		PhoneNumberPoolDto dtoTwo = translator.translateLazy(null);

		assertNull(dtoOne);
		assertNull(dtoTwo);
	}
}