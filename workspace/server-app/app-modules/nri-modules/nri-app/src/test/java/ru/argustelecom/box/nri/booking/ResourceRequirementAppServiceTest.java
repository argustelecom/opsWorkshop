package ru.argustelecom.box.nri.booking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDto;
import ru.argustelecom.box.nri.schema.requirements.ip.IpAddressBookingRequirementRepository;
import ru.argustelecom.box.nri.schema.requirements.ip.model.IpAddressBookingRequirement;
import ru.argustelecom.box.nri.schema.requirements.phone.model.PhoneNumberBookingRequirement;
import ru.argustelecom.box.nri.schema.requirements.phone.PhoneNumberBookingRequirementRepository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 02.02.2018.
 */
@RunWith(PowerMockRunner.class)
public class ResourceRequirementAppServiceTest {

	@InjectMocks
	private BookingRequirementAppService testingClass;

	@Mock
	private IpAddressBookingRequirementRepository repository;

	@Mock
	private IpAddressBookingRequirementDtoTranslator translator;

	@Mock
	private PhoneNumberBookingRequirementRepository pnRepository;

	@Mock
	private PhoneNumberBookingRequirementDtoTranslator pnTranslator;

	@Test
	public void shouldSaveIpRequirement() throws Exception {
		IpAddressBookingRequirementDto requirement = IpAddressBookingRequirementDto.builder()
				.schema(ResourceSchemaDto.builder().build())
				.build();

		when(repository.create(anyString(), anyBoolean(), any(), anyBoolean(), anyBoolean(), any(), any(), anyLong()))
				.thenReturn(IpAddressBookingRequirement.builder().build());

		when(translator.translate(anyObject())).thenReturn(requirement);

		IpAddressBookingRequirementDto res = testingClass.saveRequirement(requirement);

		assertNotNull(res);
		verify(repository, times(1)).create(anyString(), anyBoolean(), any(),
				anyBoolean(), anyBoolean(), any(), any(), anyLong());

		verify(translator, times(1)).translate(anyObject());
	}

	@Test
	public void shouldReturnNullOnFailSaveIpReq() throws Exception {
		IpAddressBookingRequirementDto requirement = IpAddressBookingRequirementDto.builder()
				.schema(ResourceSchemaDto.builder().build())
				.build();

		when(repository.create(anyString(), anyBoolean(), any(), anyBoolean(), anyBoolean(), any(), any(), anyLong()))
				.thenReturn(null);

		IpAddressBookingRequirementDto res = testingClass.saveRequirement(requirement);

		assertNull(res);
		verify(repository, times(1)).create(anyString(), anyBoolean(), any(),
				anyBoolean(), anyBoolean(), any(), any(), anyLong());

		verify(translator, times(0)).translate(anyObject());
	}

	@Test
	public void shouldSavePnRequirement() throws Exception {
		PhoneNumberBookingRequirementDto requirement = PhoneNumberBookingRequirementDto.builder()
				.schema(ResourceSchemaDto.builder().build())
				.build();

		when(pnRepository.create(anyString(), anyLong()))
				.thenReturn(PhoneNumberBookingRequirement.builder().build());

		when(pnTranslator.translate(anyObject())).thenReturn(requirement);

		PhoneNumberBookingRequirementDto res = testingClass.saveRequirement(requirement);

		assertNotNull(res);
		verify(pnRepository, times(1)).create(anyString(), anyLong());

		verify(pnTranslator, times(1)).translate(anyObject());
	}

	@Test
	public void shouldReturnNullOnFailSavePnReq() throws Exception {
		PhoneNumberBookingRequirementDto requirement = PhoneNumberBookingRequirementDto.builder()
				.schema(ResourceSchemaDto.builder().build())
				.build();

		when(pnRepository.create(anyString(), anyLong()))
				.thenReturn(null);

		PhoneNumberBookingRequirementDto res = testingClass.saveRequirement(requirement);

		assertNull(res);
		verify(pnRepository, times(1)).create(anyString(), anyLong());

		verify(pnTranslator, times(0)).translate(anyObject());
	}
}