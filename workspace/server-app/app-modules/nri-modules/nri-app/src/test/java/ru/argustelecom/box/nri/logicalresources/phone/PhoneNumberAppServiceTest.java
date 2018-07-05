package ru.argustelecom.box.nri.logicalresources.phone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.lifecycle.api.history.model.LifecycleHistoryItem;
import ru.argustelecom.box.env.lifecycle.impl.LifecycleHistoryRepository;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberLifecycle;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationInstance;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author a.wisniewski
 * @since 02.11.2017
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({MetadataUnit.class})
public class PhoneNumberAppServiceTest {

	@Mock
	private PhoneNumberRepository repository;

	@Mock
	private IdSequenceService idSequenceService;

	@Mock
	private PhoneNumberDtoTranslator translator;

	@Mock
	private PhoneNumberPoolRepository poolRepository;

	@Mock
	private LifecycleHistoryRepository historyRepository;

	@Mock
	private LifecycleRoutingService routingService;

	@Mock
	private TypeFactory factory;

	private PhoneNumberDto defaultPhoneNumberDto = PhoneNumberDto.builder().id(1L).state(PhoneNumberState.AVAILABLE).build();
	private PhoneNumber defaultPhoneNumber = new PhoneNumber(1L);

	private PhoneNumberPoolDto defaultPhoneNumberPoolDto = PhoneNumberPoolDto.builder().id(1L).build();
	private PhoneNumberPool defaultPhoneNumberPool = PhoneNumberPool.builder().id(1L).build();

	@InjectMocks
	private PhoneNumberAppService service;

	@Before
	public void setUp() throws Exception {
		when(translator.translate(defaultPhoneNumber)).thenReturn(defaultPhoneNumberDto);
		when(repository.findOne(anyLong())).thenReturn(defaultPhoneNumber);
		when(repository.findOneWithRefresh(anyLong())).thenReturn(defaultPhoneNumber);

		defaultPhoneNumberPoolDto.setPhoneNumbers(new ArrayList<>());
		defaultPhoneNumberPoolDto.getPhoneNumbers().add(defaultPhoneNumberDto);
		defaultPhoneNumberPool.setPhoneNumbers(new ArrayList<>());
		defaultPhoneNumberPool.getPhoneNumbers().add(defaultPhoneNumber);
		defaultPhoneNumber.setPool(defaultPhoneNumberPool);
		defaultPhoneNumber.setName("123");
	}

	@Test
	public void shouldRemovePhoneNumber() throws Exception {
		when(historyRepository.getHistory(defaultPhoneNumber)).thenReturn(emptyList());
		service.remove(defaultPhoneNumberDto);
		verify(repository, atLeastOnce()).remove(defaultPhoneNumber);
	}

	@Test
	public void shouldPerformRouting() throws Exception {
		when(historyRepository.getHistory(defaultPhoneNumber)).thenReturn(singletonList(LifecycleHistoryItem.builder().build()));
		defaultPhoneNumber.setState(PhoneNumberState.AVAILABLE);
		service.remove(defaultPhoneNumberDto);
		verify(repository, never()).remove(defaultPhoneNumber);
		verify(routingService, atLeastOnce()).performRouting(defaultPhoneNumber, PhoneNumberLifecycle.Routes.DELETE);
	}

	@Test(expected = BusinessExceptionWithoutRollback.class)
	public void shouldThrowExceptionWhenDeletingWasForbidden() throws Exception {
		when(historyRepository.getHistory(defaultPhoneNumber)).thenReturn(singletonList(LifecycleHistoryItem.builder().build()));
		defaultPhoneNumber.setState(PhoneNumberState.OCCUPIED);

		service.remove(defaultPhoneNumberDto);
	}

	@Test(expected = BusinessExceptionWithoutRollback.class)
	public void shouldThrowExceptionWhenRemoving() throws Exception {
		when(historyRepository.getHistory(defaultPhoneNumber)).thenReturn(singletonList(LifecycleHistoryItem.builder().build()));
		defaultPhoneNumber.setState(PhoneNumberState.OCCUPIED);

		service.remove(defaultPhoneNumberDto);
	}

	@Test
	public void shouldNotChangePool() {
		when(poolRepository.findOne(any())).thenReturn(null);
		when(repository.findOne(any())).thenReturn(defaultPhoneNumber);
		service.changePool(PhoneNumberDto.builder().build(), PhoneNumberPoolDto.builder().build());
		verify(poolRepository, times(0)).save(any());
	}

	@Test
	public void shouldNotChangePool1() {
		when(poolRepository.findOne(any())).thenReturn(defaultPhoneNumberPool);
		when(repository.findOne(any())).thenReturn(null);
		service.changePool(PhoneNumberDto.builder().build(), PhoneNumberPoolDto.builder().build());
		verify(poolRepository, times(0)).save(any());
	}

	@Test
	public void shouldChangePool() {
		when(poolRepository.findOne(eq(1L))).thenReturn(PhoneNumberPool.builder().build());
		when(repository.findOne(eq(1L))).thenReturn(defaultPhoneNumber);
		service.changePool(PhoneNumberDto.builder().id(1L).build(), PhoneNumberPoolDto.builder().id(1L).build());

		ArgumentCaptor<PhoneNumberPool> argument = ArgumentCaptor.forClass(PhoneNumberPool.class);
		verify(poolRepository, times(1)).save(argument.capture());
		assertEquals(1, argument.getValue().getPhoneNumbers().size());
		assertEquals(defaultPhoneNumber, argument.getValue().getPhoneNumbers().get(0));
		assertEquals(argument.getValue(), argument.getValue().getPhoneNumbers().get(0).getPool());
		assertEquals(0, defaultPhoneNumberPool.getPhoneNumbers().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCreatePhoneNumber() {
		PhoneNumberDto number = null;
		PhoneNumberSpecification spec = null;
		service.createPhoneNumber(number, spec);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCreatePhoneNumberNullPool() {
		service.createPhoneNumber(PhoneNumberDto.builder().build(), new PhoneNumberSpecification(1L));
	}

	@Test
	@PrepareForTest(MetadataUnit.class)
	public void shouldCreatePhoneNumber() {
		when(poolRepository.findOne(eq(1L))).thenReturn(PhoneNumberPool.builder().build());
		when(idSequenceService.nextValue(any())).thenReturn(1L);
		when(translator.translate(any())).thenReturn(defaultPhoneNumberDto);

		PowerMockito.mockStatic(MetadataUnit.class);
		BDDMockito.given(MetadataUnit.generateId()).willReturn(1L);

		PowerMockito.mockStatic(MetadataUnit.class);
		when(MetadataUnit.generateId()).thenReturn(1L);

		PhoneNumberSpecificationInstance phoneNumberSpecificationInstance = new PhoneNumberSpecificationInstance(1L);
		when(factory.createInstance(any(), any(), anyLong())).thenReturn(phoneNumberSpecificationInstance);

		service.createPhoneNumber(PhoneNumberDto.builder().id(1L).pool(PhoneNumberPoolDto.builder().id(1L).build()).build(), new PhoneNumberSpecification(1L));
		ArgumentCaptor<PhoneNumberPool> argument = ArgumentCaptor.forClass(PhoneNumberPool.class);
		verify(poolRepository, times(1)).save(argument.capture());
		assertNotNull(argument.getValue());
		assertNotNull(argument.getValue().getPhoneNumbers());
		assertEquals(1, argument.getValue().getPhoneNumbers().size());
		//проставили спецификацию
		assertNotNull(argument.getValue().getPhoneNumbers().get(0).getSpecInstance());
		//У спецификации проставили телефон
		assertEquals(argument.getValue().getPhoneNumbers().get(0), argument.getValue().getPhoneNumbers().get(0).getSpecInstance().getPhoneNumber());
	}

	@Test
	public void removeFromResource() {
		List<Long> phoneNumberIds = emptyList();
		service.removeFromResource(phoneNumberIds);
		verify(repository, times(1)).removeFromResource(phoneNumberIds);
	}

	@Test
	public void shouldFindAllNotDeletedPhoneNumbers() throws Exception {
		when(repository.findAllNotDeletedPhoneDigits()).thenReturn(singletonList("123"));
		List<String> names = service.findAllNotDeletedPhoneDigits();
		assertNotNull(names);
		assertFalse(names.isEmpty());
	}

	@Test
	public void shouldRemoveSeveralNumbers() {
		when(historyRepository.getHistory(any())).thenReturn(new ArrayList<>());
		List<PhoneNumber> phoneNumbers = new ArrayList<>();
		phoneNumbers.add(defaultPhoneNumber);
		service.removeSeveralNumbers(phoneNumbers);

		verify(repository, atLeastOnce()).removeSeveralNumbers(any());
		verify(routingService, never()).performRouting(any(), eq(PhoneNumberLifecycle.Routes.DELETE));
	}

	@Test
	public void shoudMoveToArchive() {
		when(historyRepository.getHistory(any())).thenReturn(singletonList(LifecycleHistoryItem.builder().build()));
		List<PhoneNumber> phoneNumbers = new ArrayList<>();
		phoneNumbers.add(defaultPhoneNumber);
		service.removeSeveralNumbers(phoneNumbers);

		verify(repository, never()).removeSeveralNumbers(any());
		verify(routingService, atLeastOnce()).performRouting(any(), eq(PhoneNumberLifecycle.Routes.DELETE));
	}

	@Test
	public void shouldDoNothingWhileRemovingSeveralNumbers() {
		service.removeSeveralNumbers(new ArrayList<>());

		verify(repository, never()).removeSeveralNumbers(any());
		verify(routingService, never()).performRouting(any(), eq(PhoneNumberLifecycle.Routes.DELETE));
	}
}