package ru.argustelecom.box.nri.booking;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.booking.services.IpAddressBookingAppService;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberRepository;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.schema.ResourceSchemaRepository;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.BookingRequirementRepository;
import ru.argustelecom.box.nri.schema.requirements.ip.model.IpAddressBookingRequirement;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.box.nri.schema.requirements.phone.model.PhoneNumberBookingRequirement;
import ru.argustelecom.system.inf.exception.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
public class BookingAppServiceTest {

	@InjectMocks
	private BookingAppService testingClass;

	/**
	 * Репозиторий дступа к нарядам на бронирование
	 */
	@Mock
	private BookingOrderRepository bookingOrderRepository;

	/**
	 * Сервис бронирования ip-адресов
	 */
	@Mock
	private IpAddressBookingAppService ipAddressBookingAppService;

	/**
	 * Транслятор требований к бронированиям
	 */
	@Mock
	private BookingRequirementDtoTranslator bookingRequirementDtoTranslator;

	/**
	 * Репозиторий схем подключения
	 */
	@Mock
	private ResourceSchemaRepository resourceSchemaRepository;

	/**
	 * Транслятор ДТО логических ресурсов
	 */
	@Mock
	private LogicalResourceDtoTranslator logicalResourceDtoTranslator;

	@Mock
	private PhoneNumberRepository pnRepository;

	/**
	 * Репозиторий доступа к общим данным требований
	 */
	@Mock
	private BookingRequirementRepository bookingRequirementRepository;


	@Test
	public void shouldBook(){
		Service serviceInstance = mock(Service.class);
		ResourceRequirement requirement = IpAddressBookingRequirement.builder().build();
		BookingOrder bo = BookingOrder.builder().id(1L).bookedLogicalResource(Sets.newHashSet(new PhoneNumber())).build();
		when(ipAddressBookingAppService.book(eq(serviceInstance),eq(requirement))).thenReturn(bo);
		BookingOrder bo1 = testingClass.bookResource(requirement,serviceInstance);
		assertNotNull(bo1);
		assertEquals(bo1,bo);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotBook(){
		Service serviceInstance = mock(Service.class);
		ResourceRequirement requirement = PhoneNumberBookingRequirement.builder().build();
		testingClass.bookResource(requirement,serviceInstance);
	}

	@Test
	public void shouldRelease(){
		BookingOrder bo = BookingOrder.builder().id(1L).bookedLogicalResource(Sets.newHashSet(new PhoneNumber())).build();
		testingClass.releaseBooking(bo);
		verify(bookingOrderRepository,times(1)).release(eq(bo));
	}

	@Test
	public void shouldLoadBookingRequirementsBySchema(){
		ResourceSchema schema = ResourceSchema.builder().build();
		schema.setBookings(Arrays.asList(PhoneNumberBookingRequirement.builder().build(),IpAddressBookingRequirement.builder().build()));
		when(resourceSchemaRepository.findById(eq(1L))).thenReturn(schema);
		testingClass.loadBookingRequirementsBySchema(1L);
		verify(bookingRequirementDtoTranslator,times(schema.getBookings().size())).translate(any());
	}

	@Test
	public void shouldLoadEmptyBookingRequirementsBySchema(){

		when(resourceSchemaRepository.findById(eq(1L))).thenReturn(ResourceSchema.builder().build());
		List<BookingRequirementDto>  req =  testingClass.loadBookingRequirementsBySchema(1L);
		assertNotNull(req);
		assertEquals(0,req.size());

	}

	@Test
	public void shouldLoadAllBookingsByService(){
		Service serviceInstance = mock(Service.class);
		testingClass.loadAllBookingsByService(serviceInstance);
		verify(bookingOrderRepository,times(1)).loadAllLoadingsByService(eq(serviceInstance));
	}

	@Test
	public void shouldLoadAllBookedResourceByOrder(){
		BookingOrder bo = BookingOrder.builder().id(1L).bookedLogicalResource(Sets.newHashSet(new PhoneNumber(),new IPAddress())).build();
		when(bookingOrderRepository.findOne(eq(bo.getId()))).thenReturn(bo);
		testingClass.loadAllBookedResourceByOrder(bo);
		verify(logicalResourceDtoTranslator,times(bo.getBookedLogicalResource().size())).translate(any());
	}
	@Test(expected = BusinessException.class)
	public void shouldThrowExceptionNullService(){
		testingClass.bookPhoneNumbers(null,null,null);
	}
	@Test(expected = BusinessException.class)
	public void shouldThrowExceptionNullReq(){
		testingClass.bookPhoneNumbers(mock(Service.class),null,null);
	}
	@Test(expected = BusinessException.class)
	public void shouldThrowExceptionNullPhonesToBook(){
		testingClass.bookPhoneNumbers(mock(Service.class),PhoneNumberBookingRequirementDto.builder().build(),null);
	}

	@Test
	public void shouldBookPhoneNumbers(){

		List<PhoneNumber> phones = new ArrayList<>();
		phones.add(new PhoneNumber());
		phones.add(new PhoneNumber());
		when(pnRepository.findMany(Arrays.asList(1L,2L))).thenReturn(phones);
		PhoneNumberBookingRequirement req = PhoneNumberBookingRequirement.builder().build();
		when(bookingRequirementRepository.findOne(1L)).thenReturn(req);
		Service service = mock(Service.class);
		testingClass.bookPhoneNumbers(service,PhoneNumberBookingRequirementDto.builder().id(1L).build(),
				Arrays.asList(PhoneNumberDto.builder().id(1L).build(),PhoneNumberDto.builder().id(2L).build()));
		verify(bookingOrderRepository,times(1)).createBookingOrder(eq(new HashSet<>(phones)),eq(service),eq(req));

	}
}
