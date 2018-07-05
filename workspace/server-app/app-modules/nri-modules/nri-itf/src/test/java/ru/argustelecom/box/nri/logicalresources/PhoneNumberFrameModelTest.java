package ru.argustelecom.box.nri.logicalresources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.ContextMocker;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author d.khekk
 * @since 20.11.2017
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberFrameModelTest {

	@Mock
	private PhoneNumberAppService service;

	@InjectMocks
	private PhoneNumberFrameModel model;

	@Before
	public void setUp() throws Exception {
		ContextMocker.mockFacesContext();
		model.setOnDeleteButtonPressed(System.out::println);
	}

	@Test
	public void shouldPreRender() throws Exception {
		model.preRender(PhoneNumberPoolDto.builder().phoneNumbers(new ArrayList<>()).build());
		assertNotNull(model.getPhoneNumbers());
	}

	@Test
	public void shouldRemoveSelectedNumbers() throws Exception {
		doNothing().when(service).remove(any(Long.class));
		List<PhoneNumberDto> selectedPhones = new ArrayList<>();
		selectedPhones.addAll(Arrays.asList(PhoneNumberDto.builder().build(), PhoneNumberDto.builder().build(), PhoneNumberDto.builder().build()));
		model.setSelectedPhones(selectedPhones);

		model.removeSelectedNumbers();

		assertTrue(model.getSelectedPhones().isEmpty());
	}

	@Test
	public void shouldNotRemoveNumbers() throws Exception {
		doThrow(new BusinessExceptionWithoutRollback()).when(service).remove(any(PhoneNumberDto.class));
		List<PhoneNumberDto> selectedPhones = new ArrayList<>();
		selectedPhones.addAll(Arrays.asList(PhoneNumberDto.builder().build(), PhoneNumberDto.builder().build(), PhoneNumberDto.builder().build()));
		model.setSelectedPhones(selectedPhones);

		model.removeSelectedNumbers();

		assertFalse(model.getSelectedPhones().isEmpty());
	}

	@Test
	public void shouldNotDoungAnything() throws Exception {
		model.setSelectedPhones(new ArrayList<>());
		model.removeSelectedNumbers();
		verify(service, never()).remove(any(Long.class));
	}
}