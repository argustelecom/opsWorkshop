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
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolRepository;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationRepository;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author d.khekk
 * @since 27.11.2017
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumbersViewModelTest {

	@Mock
	private PhoneNumberPoolRepository poolRepository;

	@Mock
	private PhoneNumberList lazyNumbers;

	@Mock
	private PhoneNumberSpecificationRepository specRepository;

	@Mock
	private PhoneNumberAppService phoneService;

	@Mock
	private UnitOfWork unitOfWork;

	@InjectMocks
	private PhoneNumbersViewModel model;

	private PhoneNumberPool defaultPool = PhoneNumberPool.builder().id(1L).name("Pool").build();
	private PhoneNumberDtoTmp defaultNumberTmp = PhoneNumberDtoTmp.builder().id(1L).name("Number").build();
	private PhoneNumberDto defaultNumber = PhoneNumberDto.builder().id(1L).name("Number").build();

	private PhoneNumberSpecification defaultSpec = new PhoneNumberSpecification(1L);
	private List<PhoneNumberDtoTmp> defaultSelectedNumbers = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		ContextMocker.mockFacesContext();
		when(poolRepository.findAll()).thenReturn(Collections.singletonList(defaultPool));
		when(specRepository.getAllSpecs()).thenReturn(Collections.singletonList(defaultSpec));
		doNothing().when(lazyNumbers).reloadData();
		doNothing().when(unitOfWork).makePermaLong();
		defaultSelectedNumbers.add(defaultNumberTmp);
	}

	@Test
	public void shouldInitController() throws Exception {
		model.postConstruct();

		assertNotNull(model.getPools());
		assertNotNull(model.getSpecifications());
		assertFalse(model.getPools().isEmpty());
		assertFalse(model.getSpecifications().isEmpty());
	}

	@Test
	public void shouldNotRemoveNumbers() throws Exception {
		model.setSelectedPhones(new ArrayList<>());
		model.remove();

		verify(phoneService, never()).remove(any(Long.class));
	}

	@Test
	public void shouldRemoveNumbers() throws Exception {
		doNothing().when(phoneService).remove(any(Long.class));
		when(phoneService.findPhoneNumberById(eq(1L))).thenReturn(defaultNumber);

		model.setSelectedPhones(defaultSelectedNumbers);
		model.remove();

		assertTrue(model.getSelectedPhones().isEmpty());
		verify(phoneService, atLeastOnce()).remove(eq(1L));
	}

	@Test
	public void shouldNotRemoveCauseCheckIsFailed() throws Exception {
		doThrow(new BusinessExceptionWithoutRollback()).when(phoneService).remove(any(Long.class));
		when(phoneService.findPhoneNumberById(eq(1L))).thenReturn(defaultNumber);
		model.setSelectedPhones(defaultSelectedNumbers);
		model.remove();


		assertFalse(model.getSelectedPhones().isEmpty());
		verify(phoneService, atLeastOnce()).remove(eq(1L));
	}

	@Test
	public void shouldGetStates() throws Exception {
		assertNotNull(model.getStates());
	}

	@Test
	public void shouldClearSelected() throws Exception {
		model.setSelectedPhones(defaultSelectedNumbers);
		model.clearSelected();

		assertTrue(model.getSelectedPhones().isEmpty());
	}
}