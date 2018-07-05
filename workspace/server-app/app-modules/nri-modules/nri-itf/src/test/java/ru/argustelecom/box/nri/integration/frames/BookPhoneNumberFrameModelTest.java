package ru.argustelecom.box.nri.integration.frames;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.model.DualListModel;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.nri.booking.BookingAppService;
import ru.argustelecom.box.nri.booking.PhoneNumberBookingRequirementDto;
import ru.argustelecom.box.nri.booking.PhoneNumberBookingRequirementDtoTranslator;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.integration.viewmodel.BookingResourceDataHolder;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationInstance;
import ru.argustelecom.box.nri.schema.requirements.model.RequirementType;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.box.nri.schema.requirements.phone.model.PhoneNumberBookingRequirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class BookPhoneNumberFrameModelTest {

	@Mock
	private PhoneNumberBookingRequirementDtoTranslator translator;

	@Mock
	private PhoneNumberAppService phoneNumberAppService;

	@Mock
	private BookingAppService bookingAppService;

	@InjectMocks
	private BookPhoneNumberFrameModel testingClass;

	private Set<LogicalResource> lr;

	@Before
	public void setUp() {
		lr = new HashSet<>();
		PhoneNumber ph = new PhoneNumber();
		ph.setPool(PhoneNumberPool.builder().build());
		ph.setSpecInstance(mock(PhoneNumberSpecificationInstance.class));
		ph.setName("5551234");
		lr.add(ph);


	}
	@Test
	public void shouldPreRender(){
		Service service = mock(Service.class);
		PhoneNumberBookingRequirement req = PhoneNumberBookingRequirement.builder().build();
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(req)
				.build();

		PhoneNumberBookingRequirementDto reqDto = PhoneNumberBookingRequirementDto.builder().build();
		when(translator.translate(req)).thenReturn(reqDto);
		testingClass.preRender(service,booking);
		assertEquals(booking,testingClass.getCurrentBooking());
		assertEquals(reqDto,testingClass.getCurrentRequirement());



	}
	@Test
	public void shouldPreRenderReset(){
		Service service = mock(Service.class);
		PhoneNumberBookingRequirement req = PhoneNumberBookingRequirement.builder().build();
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(req)
				.build();

		PhoneNumberBookingRequirementDto reqDto = PhoneNumberBookingRequirementDto.builder().build();
		when(translator.translate(req)).thenReturn(reqDto);
		testingClass.preRender(service,booking);
		assertEquals(booking,testingClass.getCurrentBooking());
		assertEquals(reqDto,testingClass.getCurrentRequirement());

		DualListModel<PhoneNumberDto> dl = new DualListModel<>();
		testingClass.setAvailableResources(dl);
		testingClass.getAvailableResources().getTarget().add(PhoneNumberDto.builder().build());
		testingClass.preRender(service,BookingResourceDataHolder.builder().build());
		assertNull(testingClass.getCurrentBooking());
		assertNull(testingClass.getCurrentRequirement());
		assertEquals(0,testingClass.getAvailableResources().getTarget().size());

		testingClass.preRender(service,booking);

		testingClass.preRender(service,null);
		assertNull(testingClass.getCurrentBooking());
		assertNull(testingClass.getCurrentRequirement());


		testingClass.preRender(service,booking);

		testingClass.preRender(service,BookingResourceDataHolder.builder().requirement(new ResourceRequirement(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT)).build());
		assertNull(testingClass.getCurrentBooking());
		assertNull(testingClass.getCurrentRequirement());


	}

	@Test
	public void shouldNotFind(){

		Service service = mock(Service.class);
		PhoneNumberBookingRequirement req = PhoneNumberBookingRequirement.builder().build();
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(req)
				.build();

		PhoneNumberBookingRequirementDto reqDto = PhoneNumberBookingRequirementDto.builder().build();
		when(translator.translate(req)).thenReturn(reqDto);
		testingClass.preRender(service,booking);

		when(phoneNumberAppService.findPhoneNumbersLike(any())).thenReturn(null);
		DualListModel<PhoneNumberDto> dl = new DualListModel<>();
		testingClass.setAvailableResources(dl);
		testingClass.getAvailableResources().getSource().add(PhoneNumberDto.builder().build());

		testingClass.find();

		assertEquals(0,testingClass.getAvailableResources().getSource().size());
	}

	@Test
	public void shouldFind(){

		Service service = mock(Service.class);
		PhoneNumberBookingRequirement req = PhoneNumberBookingRequirement.builder().build();
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().
				bookingOrder(BookingOrder.builder().id(1L).bookedLogicalResource(lr).build()).requirement(req)
				.build();

		PhoneNumberBookingRequirementDto reqDto = PhoneNumberBookingRequirementDto.builder().build();
		when(translator.translate(req)).thenReturn(reqDto);
		testingClass.preRender(service,booking);


		when(phoneNumberAppService.findPhoneNumbersLike(any())).thenReturn(Arrays.asList(PhoneNumberDto.builder().build(),PhoneNumberDto.builder().build()));
		DualListModel<PhoneNumberDto> dl = new DualListModel<>();
		testingClass.setAvailableResources(dl);
		testingClass.getAvailableResources().getSource().add(PhoneNumberDto.builder().build());

		testingClass.find();

		assertEquals(2,testingClass.getAvailableResources().getSource().size());
	}

	@Test
	public void shouldBook(){
		Service service = mock(Service.class);
		PhoneNumberBookingRequirement req = PhoneNumberBookingRequirement.builder().build();
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().requirement(req)
				.build();

		PhoneNumberBookingRequirementDto reqDto = PhoneNumberBookingRequirementDto.builder().build();
		when(translator.translate(req)).thenReturn(reqDto);
		testingClass.preRender(service,booking);
		testingClass.getAvailableResources().getTarget().add(PhoneNumberDto.builder().build());
		when( bookingAppService.bookPhoneNumbers(any(),any(),any())).thenReturn(BookingOrder.builder().bookedLogicalResource(Sets.newHashSet(new PhoneNumber())).build());
		testingClass.book();

		assertNotNull(testingClass.getCurrentBooking().getBookingOrder());
	}

	@Test
	public void shouldNotBook(){
		Service service = mock(Service.class);
		PhoneNumberBookingRequirement req = PhoneNumberBookingRequirement.builder().build();
		BookingResourceDataHolder booking = BookingResourceDataHolder.builder().requirement(req)
				.build();

		PhoneNumberBookingRequirementDto reqDto = PhoneNumberBookingRequirementDto.builder().build();
		when(translator.translate(req)).thenReturn(reqDto);
		testingClass.preRender(service,booking);

		testingClass.book();

		verify(bookingAppService,times(0)).bookPhoneNumbers(any(),any(),any());
	}


}