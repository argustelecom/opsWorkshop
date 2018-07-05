package ru.argustelecom.box.nri.logicalresources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;

import javax.faces.validator.ValidatorException;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author d.khekk
 * @since 02.11.2017
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberPoolFrameModelTest {

	@Mock
	private PhoneNumberPoolAppService service;

	@InjectMocks
	private PhoneNumberPoolFrameModel model;

	private PhoneNumberPoolDto defaultDto = PhoneNumberPoolDto.builder().id(1L).name("Pool").build();

	@Before
	public void setUp() throws Exception {
		when(service.findAllLazy()).thenReturn(singletonList(defaultDto));
	}

	@Test
	public void shouldPreRender() throws Exception {
		model.preRender(defaultDto);
		PhoneNumberPoolDto pool = model.getPool();

		assertNotNull(pool);
		assertEquals(defaultDto, pool);
	}

	@Test
	public void shouldChangePoolName() throws Exception {
		model.preRender(defaultDto);
		doNothing().when(service).save(defaultDto);

		model.savePool();

		verify(service, atLeastOnce()).save(defaultDto);
	}

	@Test
	public void shouldValidateNullName() throws Exception {
		model.poolNameValidator(null, null, null);
	}

	@Test(expected = ValidatorException.class)
	public void shouldValidateBlankName() throws Exception {
		model.preRender(PhoneNumberPoolDto.builder().id(2L).build());
		model.poolNameValidator(null, null, "   ");
	}

	@Test(expected = ValidatorException.class)
	public void shouldValidateExistName() throws Exception {
		model.preRender(PhoneNumberPoolDto.builder().id(2L).build());
		model.poolNameValidator(null, null, "Pool");
	}

	@Test
	public void shouldPassValidation() throws Exception {
		model.preRender(PhoneNumberPoolDto.builder().id(2L).build());
		model.poolNameValidator(null, null, "Pool #2");
	}
}