package ru.argustelecom.box.nri.logicalresources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;

import javax.faces.validator.ValidatorException;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author d.khekk
 * @since 02.11.2017
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberPoolCreationDialogModelTest {

	@Mock
	private PhoneNumberPoolAppService service;

	@InjectMocks
	private PhoneNumberPoolCreationDialogModel model;

	private PhoneNumberPoolDto defaultPool = PhoneNumberPoolDto.builder().name("Pool").build();

	@Before
	public void setUp() throws Exception {
		when(service.findAllLazy()).thenReturn(singletonList(defaultPool));
	}

	@Test
	public void shouldCreateNewPoolWhenDialogOpens() throws Exception {
		model.onCreationDialogOpen();
		PhoneNumberPoolDto newPool = model.getNewPool();

		assertNotNull(newPool);
		assertNull(newPool.getId());
		assertNull(newPool.getName());
	}

	@Test
	public void shouldDoNothingWhenNameIsNull() throws Exception {
		model.nameValidator(null, null, null);
	}

	@Test(expected = ValidatorException.class)
	public void shouldValidateBlankName() throws Exception {
		model.nameValidator(null, null, "  ");
	}

	@Test(expected = ValidatorException.class)
	public void shouldValidateExistName() throws Exception {
		model.nameValidator(null, null, "Pool");
	}

	@Test
	public void shouldPassValidation() throws Exception {
		model.nameValidator(null, null, "new name");
	}

	@Test
	public void shouldExecuteCallback() throws Exception {
		Callback<PhoneNumberPoolDto> newNameCallback = pool -> pool.setName("New Name");
		model.onCreationDialogOpen();
		model.setOnCreateButtonPressed(newNameCallback);

		assertEquals(newNameCallback, model.getOnCreateButtonPressed());

		model.create();

		assertEquals("New Name", model.getNewPool().getName());
	}
}