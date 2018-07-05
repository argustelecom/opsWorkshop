package ru.argustelecom.box.nri.integration.frames;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.ContextMocker;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.booking.services.IpAddressBookingAppService;
import ru.argustelecom.box.nri.integration.frames.nls.BookingIPResourceFrameModelMessagesBundle;
import ru.argustelecom.box.nri.integration.viewmodel.BookingResourceDataHolder;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.schema.requirements.model.RequirementType;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
public class BookingIPResourceFrameModelTest {

	@InjectMocks
	private BookingIPResourceFrameModel testingClass;

	@Mock
	private IpAddressBookingAppService bookingService;

	private Set<LogicalResource> lr;

	@Before
	public void setUp() {
		lr = new HashSet<>();
		lr.add(IPAddress.builder().id(1L).name("192.168.1.11").build());
		lr.add(IPAddress.builder().id(2L).name("192.168.1.12").build());
	}

	@Test
	public void shouldPreRender() {
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(new ResourceRequirement(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT))
				.build();
		Service service = mock(Service.class);
		testingClass.preRender(service, booking);
		assertEquals(booking.getRequirement(), testingClass.getCurrentBookingRequirement());
	}

	@Test
	public void shouldReset() {

		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(new ResourceRequirement(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT))
				.build();
		Service service = mock(Service.class);
		testingClass.preRender(service, booking);
		assertEquals(booking.getRequirement(), testingClass.getCurrentBookingRequirement());
		testingClass.preRender(null, booking);
		assertNull(testingClass.getCurrentBookingRequirement());

		testingClass.preRender(service, booking);
		assertEquals(booking.getRequirement(), testingClass.getCurrentBookingRequirement());
		testingClass.preRender(service, null);
		assertNull(testingClass.getCurrentBookingRequirement());

		testingClass.preRender(service, booking);
		assertEquals(booking.getRequirement(), testingClass.getCurrentBookingRequirement());
		testingClass.preRender(service, BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).build());
		assertNull(testingClass.getCurrentBookingRequirement());
	}

	class ConsumerClass {	public void createNewBooking(ResourceRequirement newBooking) {

	}
	}

	@Test
	public void shouldBook() {
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(new ResourceRequirement(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT))
				.build();
		Service service = mock(Service.class);
		testingClass.preRender(service, booking);
		ConsumerClass b = mock(ConsumerClass.class);
		testingClass.setOnCreateBookingButtonPressed(b::createNewBooking);
		when(bookingService.book(eq(service), eq(booking.getRequirement()))).thenReturn(BookingOrder.builder().bookedLogicalResource(lr).build());
		testingClass.book();
		verify(b,times(1)).createNewBooking(eq(booking.getRequirement()));

	}

	@Test
	public void shouldNotBook() {
		FacesContext context = ContextMocker.mockFacesContext();
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(new ResourceRequirement(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT))
				.build();
		Service service = mock(Service.class);
		testingClass.preRender(service, booking);
		ConsumerClass b = mock(ConsumerClass.class);
		testingClass.setOnCreateBookingButtonPressed(b::createNewBooking);
		when(bookingService.book(eq(service), eq(booking.getRequirement()))).thenReturn(null);
		testingClass.book();
		verify(b,times(1)).createNewBooking(any());

		BaseMatcher<FacesMessage> matcher = new BaseMatcher<FacesMessage>() {
			@Override
			public void describeTo(Description description) {
				// пустота
			}

			@Override
			public boolean matches(Object item) {
				FacesMessage actual = (FacesMessage) item;
				return actual.getDetail().equals(LocaleUtils.getMessages(BookingIPResourceFrameModelMessagesBundle.class).couldNotBookResourceByRule()+booking.getRequirement().getName());
			}
		};
		verify(context).addMessage(eq(null), argThat(matcher));

	}
}