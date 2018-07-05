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
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.booking.BookingAppService;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.integration.frames.nls.BookedResourceFrameModelMessagesBundle;
import ru.argustelecom.box.nri.integration.viewmodel.BookingResourceDataHolder;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.schema.requirements.model.RequirementType;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class BookedResourceFrameModelTest {


	@Mock
	private BookingAppService bookingAppService;

	@InjectMocks
	private BookedResourceFrameModel testingClass;

	private Set<LogicalResource> lr;
	private Set<LogicalResourceDto> lrdto;

	@Before
	public void setUp(){
		lr = new HashSet<>();
		lr.add(IPAddress.builder().id(1L).name("192.168.1.11").build());
		lr.add(IPAddress.builder().id(2L).name("192.168.1.12").build());

		lrdto = new HashSet<>();
		lrdto.add(LogicalResourceDto.builder().id(1L).build());
		lrdto.add(LogicalResourceDto.builder().id(2L).build());

	}
	@Test
	public void shouldPreRender(){

		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(new ResourceRequirement(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT))
				.build();
		when(bookingAppService.loadAllBookedResourceByOrder(eq(booking.getBookingOrder()))).thenReturn(lrdto);

		testingClass.preRender(booking);

		assertEquals(testingClass.getCurrentBooking(),booking);
		assertNotNull(testingClass.getResources());
		assertEquals(2,testingClass.getResources().size());
	}

	@Test
	public void shouldReset(){
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(new ResourceRequirement(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT))
				.build();
		when(bookingAppService.loadAllBookedResourceByOrder(eq(booking.getBookingOrder()))).thenReturn(lrdto);

		testingClass.preRender(booking);

		assertEquals(testingClass.getCurrentBooking(),booking);
		assertNotNull(testingClass.getResources());
		assertEquals(2,testingClass.getResources().size());
		testingClass.preRender(null);

		assertNull(testingClass.getCurrentBooking());
		assertNotNull(testingClass.getResources());
		assertEquals(0,testingClass.getResources().size());

	}
	@Test
	public void shouldResetNullOrder(){
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(new ResourceRequirement(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT))
				.build();
		when(bookingAppService.loadAllBookedResourceByOrder(eq(booking.getBookingOrder()))).thenReturn(lrdto);

		testingClass.preRender(booking);

		assertEquals(testingClass.getCurrentBooking(),booking);
		assertNotNull(testingClass.getResources());
		assertEquals(2,testingClass.getResources().size());
		testingClass.preRender(BookingResourceDataHolder.builder().
				bookingOrder(null).requirement(new ResourceRequirement(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT))
				.build());

		assertNull(testingClass.getCurrentBooking());
		assertNotNull(testingClass.getResources());
		assertEquals(0,testingClass.getResources().size());

	}

	@Test
	public void shouldReleaseBooking(){
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(new ResourceRequirement(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT))
				.build();
		when(bookingAppService.loadAllBookedResourceByOrder(eq(booking.getBookingOrder()))).thenReturn(lrdto);

		testingClass.preRender(booking);

		assertEquals(testingClass.getCurrentBooking(),booking);
		assertNotNull(testingClass.getResources());
		assertEquals(2,testingClass.getResources().size());

		when(bookingAppService.releaseBooking(eq(booking.getBookingOrder()))).thenReturn(true);

		testingClass.releaseBooking();
		assertNull(testingClass.getCurrentBooking().getBookingOrder());
	}

	@Test
	public void shouldNotReleaseBooking(){
		FacesContext context = ContextMocker.mockFacesContext();
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(new ResourceRequirement(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT))
				.build();
		when(bookingAppService.loadAllBookedResourceByOrder(eq(booking.getBookingOrder()))).thenReturn(lrdto);

		testingClass.preRender(booking);

		assertEquals(testingClass.getCurrentBooking(),booking);
		assertNotNull(testingClass.getResources());
		assertEquals(2,testingClass.getResources().size());

		when(bookingAppService.releaseBooking(eq(booking.getBookingOrder()))).thenReturn(false);

		testingClass.releaseBooking();
		BaseMatcher<FacesMessage> matcher = new BaseMatcher<FacesMessage>() {
			@Override
			public void describeTo(Description description) {
				// пустота
			}

			@Override
			public boolean matches(Object item) {
				FacesMessage actual = (FacesMessage) item;
				return actual.getDetail().equals(LocaleUtils.getMessages(BookedResourceFrameModelMessagesBundle.class).couldNotCancelBooking());
			}
		};
		verify(context).addMessage(eq(null), argThat(matcher));
	}


}