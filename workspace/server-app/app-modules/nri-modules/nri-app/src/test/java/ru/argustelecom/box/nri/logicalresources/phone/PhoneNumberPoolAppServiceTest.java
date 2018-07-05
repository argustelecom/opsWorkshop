package ru.argustelecom.box.nri.logicalresources.phone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationInstance;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationRepository;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author d.khekk
 * @since 02.11.2017
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberPoolAppServiceTest {

	@Mock
	private PhoneNumberPoolRepository repository;

	@Mock
	private PhoneNumberPoolDtoTranslator translator;

	@Mock
	private PhoneNumberSpecificationRepository phoneNumberSpecRepository;

	@Mock
	private PhoneNumberAppService phoneNumberService;

	@Mock
	private IdSequenceService idSequence;

	@Mock
	private TypeFactory factory;

	@InjectMocks
	private PhoneNumberPoolAppService service;

	private PhoneNumberPool defaultPool = PhoneNumberPool.builder().id(1L).name("Pool").phoneNumbers(new ArrayList<>()).build();
	private PhoneNumberPoolDto defaultPoolDto = PhoneNumberPoolDto.builder().id(1L).name("Pool").phoneNumbers(new ArrayList<>()).build();
	private PhoneNumberPoolDto poolWithoutId = PhoneNumberPoolDto.builder().build();

	@Before
	public void setUp() throws Exception {
		when(translator.translate(defaultPool)).thenReturn(defaultPoolDto);
		when(translator.translateLazy(defaultPool)).thenReturn(defaultPoolDto);
		when(repository.findOne(eq(1L))).thenReturn(defaultPool);
		when(repository.findOneWithRefresh(eq(1L))).thenReturn(defaultPool);

		doNothing().when(repository).save(any());
	}

	@Test
	public void shouldFindAllLazy() throws Exception {
		when(repository.findAll()).thenReturn(Collections.singletonList(defaultPool));

		List<PhoneNumberPoolDto> poolDtos = service.findAllLazy();

		assertNotNull(poolDtos);
		assertFalse(poolDtos.isEmpty());
		assertTrue(poolDtos.contains(defaultPoolDto));
	}

	@Test
	public void shouldFindOneByItsId() throws Exception {
		PhoneNumberPoolDto poolDto = service.findPoolById(1L);

		assertNotNull(poolDto);
		assertEquals(defaultPoolDto, poolDto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenTryToFindWithoutId() throws Exception {
		Long id = null;
		service.findPoolById(id);
	}

	@Test
	public void shouldRenamePool() throws Exception {
		service.save(defaultPoolDto);

		verify(repository, atLeastOnce()).save(any());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhileTryingToRenamePoolWithoutId() throws Exception {
		service.save(poolWithoutId);
	}

	@Test
	public void shouldCreateNewPool() throws Exception {
		when(repository.create(eq(defaultPoolDto.getName()),any())).thenReturn(defaultPool);

		PhoneNumberPoolDto createdPool = service.createPool(defaultPoolDto);

		assertNotNull(createdPool);
		assertEquals(defaultPoolDto.getName(), createdPool.getName());
	}

	@Test
	public void shouldRemovePool() throws Exception {
		doNothing().when(repository).remove(any());
		service.remove(defaultPoolDto);

		verify(repository, atLeastOnce()).remove(defaultPool);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhileTryingRemovePoolWithoutId() throws Exception {
		service.remove(poolWithoutId);
	}

	@Test(expected = BusinessExceptionWithoutRollback.class)
	public void shouldNotGenerateNumbers() throws BusinessExceptionWithoutRollback {
		service.generatePhoneNumbers(1L,1L,"X","X");
	}

	@Test(expected = BusinessExceptionWithoutRollback.class)
	public void shouldThrowExceptionWhileGenerateNumbersWithXto() throws BusinessExceptionWithoutRollback {
		service.generatePhoneNumbers(1L,1L,"(812)999-99-99","X");
	}

	@Test(expected = BusinessExceptionWithoutRollback.class)
	public void shouldThrowExceptionWhileGenerateNumbersWithFromBiggerTo() throws BusinessExceptionWithoutRollback {
		service.generatePhoneNumbers(1L,1L,"(812)123-45-67","(812)123-45-55");
	}

	@Test(expected = BusinessExceptionWithoutRollback.class)
	public void shouldThrowExceptionWhileGenerateNumbersWithLengthFromBigger15() throws BusinessExceptionWithoutRollback {
		service.generatePhoneNumbers(1L,1L,"123456789012345678","1234567890123456799");
	}

	@Test(expected = BusinessExceptionWithoutRollback.class)
	public void shouldThrowExceptionWhileGenerateNumbersToManyNumbers() throws BusinessExceptionWithoutRollback {
		service.generatePhoneNumbers(1L,1L,"1234567","12345678901234");
	}

	@Test(expected = BusinessExceptionWithoutRollback.class)
	public void shouldThrowExceptionWhileGenerateNumbersAlreadyHas() throws BusinessExceptionWithoutRollback {
		when(phoneNumberService.findAllNotDeletedPhoneDigits()).thenReturn(Arrays.asList("0", "1", "2"));
		service.generatePhoneNumbers(1L,1L,"0","2");
	}

	@Test
	@PrepareForTest(MetadataUnit.class)
	public void shouldGenerateNumbers() throws BusinessExceptionWithoutRollback {
		when(idSequence.nextValue(PhoneNumber.class)).thenReturn(1L);
		when(idSequence.nextValue(PhoneNumberSpecificationInstance.class)).thenReturn(1L);

		when(phoneNumberService.findAllNotDeletedPhoneDigits()).thenReturn(Collections.singletonList("1"));
		when(phoneNumberSpecRepository.findOne(eq(1L))).thenReturn(new PhoneNumberSpecification(1L));

		PowerMockito.mockStatic(MetadataUnit.class);
		when(MetadataUnit.generateId()).thenReturn(1L);
		PhoneNumberSpecificationInstance phoneNumberSpecificationInstance = new PhoneNumberSpecificationInstance(1L);

		when(factory.createInstance(any(), any(), anyLong())).thenReturn(phoneNumberSpecificationInstance);

		service.generatePhoneNumbers(1L,1L,"2","4");

		ArgumentCaptor<PhoneNumberPool> argument = ArgumentCaptor.forClass(PhoneNumberPool.class);
		verify(repository, times(1)).save(argument.capture());

		assertEquals(3,argument.getValue().getPhoneNumbers().size());

	}
}