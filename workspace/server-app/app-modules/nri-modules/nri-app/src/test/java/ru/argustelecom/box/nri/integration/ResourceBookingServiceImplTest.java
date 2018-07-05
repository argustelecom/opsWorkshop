package ru.argustelecom.box.nri.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.nri.booking.BookingAppService;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaAppService;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.box.nri.schema.requirements.ip.model.IpAddressBookingRequirement;
import ru.argustelecom.box.nri.schema.requirements.phone.model.PhoneNumberBookingRequirement;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.system.inf.exception.BusinessException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Created by b.bazarov on 09.02.2018.
 */
@RunWith(PowerMockRunner.class)
public class ResourceBookingServiceImplTest {


	@Mock
	private ResourceSchemaAppService resourceSchemaAppService;

	@Mock
	private BookingAppService bookingAppService;


	@InjectMocks
	private ResourceBookingServiceImpl resourceBookingCheckService;

	@Test(expected = BusinessException.class)
	public void shouldNotCheck() {
		resourceBookingCheckService.check(null);
	}

	@Test
	public void shouldReturnTrue() {

		List<ResourceRequirement> requirementList = new ArrayList<>();
		requirementList.add(IpAddressBookingRequirement.builder().id(1L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(3L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(4L).build());
		requirementList.add(PhoneNumberBookingRequirement.builder().id(5L).build());

		Service serviceInstance = mock(Service.class);
		List<ResourceSchema> schemas = new ArrayList<>();
		ResourceSchema schema1 = ResourceSchema.builder().build();
		ResourceSchema schema2 = ResourceSchema.builder().build();
		schemas.add(schema1);
		schemas.add(schema2);
		schemas.get(1).setBookings(requirementList);

		List<BookingOrder> bookingOrders = new ArrayList<>();
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema2).id(1L).build()).build());
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema2).id(3L).build()).build());
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema2).id(4L).build()).build());
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(PhoneNumberBookingRequirement.builder().schema(schema2).id(5L).build()).build());

		when(resourceSchemaAppService.findAllByServiceSpec(any())).thenReturn(schemas);
		when(bookingAppService.loadAllBookingsByService(any())).thenReturn(bookingOrders);


		assertTrue(resourceBookingCheckService.check(serviceInstance));
	}

	@Test
	public void shouldReturnFalseBySize() {

		List<ResourceRequirement> requirementList = new ArrayList<>();
		requirementList.add(IpAddressBookingRequirement.builder().id(1L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(3L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(4L).build());
		requirementList.add(PhoneNumberBookingRequirement.builder().id(5L).build());

		Service serviceInstance = mock(Service.class);
		List<ResourceSchema> schemas = new ArrayList<>();
		ResourceSchema schema1 = ResourceSchema.builder().build();
		ResourceSchema schema2 = ResourceSchema.builder().build();
		schemas.add(schema1);
		schemas.add(schema2);
		schemas.get(1).setBookings(requirementList);

		List<BookingOrder> bookingOrders = new ArrayList<>();
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema2).id(1L).build()).build());
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema2).id(3L).build()).build());
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema2).id(4L).build()).build());


		when(resourceSchemaAppService.findAllByServiceSpec(any())).thenReturn(schemas);
		when(bookingAppService.loadAllBookingsByService(any())).thenReturn(bookingOrders);


		assertFalse(resourceBookingCheckService.check(serviceInstance));
	}

	@Test
	public void shouldReturnFalseByRequirementMismatch() {

		List<ResourceRequirement> requirementList = new ArrayList<>();
		requirementList.add(IpAddressBookingRequirement.builder().id(1L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(3L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(4L).build());
		requirementList.add(PhoneNumberBookingRequirement.builder().id(6L).build());

		Service serviceInstance = mock(Service.class);
		List<ResourceSchema> schemas = new ArrayList<>();
		ResourceSchema schema1 = ResourceSchema.builder().build();
		ResourceSchema schema2 = ResourceSchema.builder().build();
		schemas.add(schema1);
		schemas.add(schema2);
		schemas.get(1).setBookings(requirementList);

		List<BookingOrder> bookingOrders = new ArrayList<>();
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema2).id(1L).build()).build());
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema2).id(3L).build()).build());
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema2).id(4L).build()).build());
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(PhoneNumberBookingRequirement.builder().schema(schema2).id(5L).build()).build());

		when(resourceSchemaAppService.findAllByServiceSpec(any())).thenReturn(schemas);
		when(bookingAppService.loadAllBookingsByService(any())).thenReturn(bookingOrders);


		assertFalse(resourceBookingCheckService.check(serviceInstance));
	}
	@Test
	public void shouldReturnTrueForNullReq(){
		List<ResourceRequirement> requirementList = new ArrayList<>();
		requirementList.add(IpAddressBookingRequirement.builder().id(1L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(3L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(4L).build());
		requirementList.add(PhoneNumberBookingRequirement.builder().id(5L).build());

		Service serviceInstance = mock(Service.class);
		List<ResourceSchema> schemas = new ArrayList<>();
		ResourceSchema schema1 = ResourceSchema.builder().build();
		ResourceSchema schema2 = ResourceSchema.builder().build();
		schemas.add(schema1);
		schemas.add(schema2);
		schemas.get(1).setBookings(requirementList);

		List<BookingOrder> bookingOrders = new ArrayList<>();

		when(resourceSchemaAppService.findAllByServiceSpec(any())).thenReturn(schemas);
		when(bookingAppService.loadAllBookingsByService(any())).thenReturn(bookingOrders);


		assertTrue(resourceBookingCheckService.check(serviceInstance));
	}
	@Test
	public void shouldReturnFalseForNullReq(){
		List<ResourceRequirement> requirementList = new ArrayList<>();
		requirementList.add(IpAddressBookingRequirement.builder().id(1L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(3L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(4L).build());
		requirementList.add(PhoneNumberBookingRequirement.builder().id(5L).build());
		List<ResourceRequirement> requirementList2 = new ArrayList<>(requirementList);

		Service serviceInstance = mock(Service.class);
		List<ResourceSchema> schemas = new ArrayList<>();
		ResourceSchema schema1 = ResourceSchema.builder().build();
		ResourceSchema schema2 = ResourceSchema.builder().build();
		schemas.add(schema1);
		schemas.add(schema2);
		schemas.get(1).setBookings(requirementList);
		schemas.get(0).setBookings(requirementList2);

		List<BookingOrder> bookingOrders = new ArrayList<>();

		when(resourceSchemaAppService.findAllByServiceSpec(any())).thenReturn(schemas);
		when(bookingAppService.loadAllBookingsByService(any())).thenReturn(bookingOrders);


		assertFalse(resourceBookingCheckService.check(serviceInstance));
	}
	@Test
	public void shouldReturnFalseByNullCurSchema() {

		List<ResourceRequirement> requirementList = new ArrayList<>();
		requirementList.add(IpAddressBookingRequirement.builder().id(1L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(3L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(4L).build());
		requirementList.add(PhoneNumberBookingRequirement.builder().id(6L).build());

		Service serviceInstance = mock(Service.class);
		List<ResourceSchema> schemas = new ArrayList<>();
		ResourceSchema schema1 = ResourceSchema.builder().build();
		ResourceSchema schema2 = ResourceSchema.builder().build();
		schemas.add(schema1);
		schemas.add(schema2);
		schemas.get(1).setBookings(requirementList);

		List<BookingOrder> bookingOrders = new ArrayList<>();
		bookingOrders.add(BookingOrder.builder().id(123L).bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().id(1L).build()).build());

		when(resourceSchemaAppService.findAllByServiceSpec(any())).thenReturn(schemas);
		when(bookingAppService.loadAllBookingsByService(any())).thenReturn(bookingOrders);


		assertFalse(resourceBookingCheckService.check(serviceInstance));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionSchemasMismatch() {

		List<ResourceRequirement> requirementList = new ArrayList<>();
		requirementList.add(IpAddressBookingRequirement.builder().id(1L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(3L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(4L).build());
		requirementList.add(PhoneNumberBookingRequirement.builder().id(6L).build());

		Service serviceInstance = mock(Service.class);
		List<ResourceSchema> schemas = new ArrayList<>();
		ResourceSchema schema1 = ResourceSchema.builder().build();
		ResourceSchema schema2 = ResourceSchema.builder().build();
		schemas.add(schema1);
		schemas.get(0).setBookings(requirementList);

		List<BookingOrder> bookingOrders = new ArrayList<>();

		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema2).id(1L).build()).build());

		when(resourceSchemaAppService.findAllByServiceSpec(any())).thenReturn(schemas);
		when(bookingAppService.loadAllBookingsByService(any())).thenReturn(bookingOrders);


		resourceBookingCheckService.check(serviceInstance);
	}
	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionTooMachSchemas() {

		List<ResourceRequirement> requirementList = new ArrayList<>();
		requirementList.add(IpAddressBookingRequirement.builder().id(1L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(3L).build());
		requirementList.add(IpAddressBookingRequirement.builder().id(4L).build());
		requirementList.add(PhoneNumberBookingRequirement.builder().id(6L).build());

		Service serviceInstance = mock(Service.class);
		List<ResourceSchema> schemas = new ArrayList<>();
		ResourceSchema schema1 = ResourceSchema.builder().build();
		ResourceSchema schema2 = ResourceSchema.builder().build();
		schemas.add(schema1);
		schemas.get(0).setBookings(requirementList);

		List<BookingOrder> bookingOrders = new ArrayList<>();

		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema2).id(1L).build()).build());
		bookingOrders.add(BookingOrder.builder().bookedLogicalResource(Collections.singleton(IPAddress.builder().name("192.168.100.22").build())).requirement(IpAddressBookingRequirement.builder().schema(schema1).id(1L).build()).build());

		when(resourceSchemaAppService.findAllByServiceSpec(any())).thenReturn(schemas);
		when(bookingAppService.loadAllBookingsByService(any())).thenReturn(bookingOrders);


		resourceBookingCheckService.check(serviceInstance);
	}

	@Test
	public void shouldReturnTrueNullSchemas(){
		Service serviceInstance = mock(Service.class);


		List<BookingOrder> bookingOrders = new ArrayList<>();

		when(resourceSchemaAppService.findAllByServiceSpec(any())).thenReturn(null);
		when(bookingAppService.loadAllBookingsByService(any())).thenReturn(bookingOrders);


		assertTrue(resourceBookingCheckService.check(serviceInstance));
	}

}