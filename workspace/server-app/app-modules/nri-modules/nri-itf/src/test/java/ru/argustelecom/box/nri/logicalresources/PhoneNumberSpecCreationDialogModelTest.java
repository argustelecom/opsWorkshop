package ru.argustelecom.box.nri.logicalresources;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationRepository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by s.kolyada on 01.11.2017.
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberSpecCreationDialogModelTest {

	@Mock
	private PhoneNumberSpecificationRepository phoneNumberSpecificationRepository;

	@InjectMocks
	private PhoneNumberSpecCreationDialogModel testingClass;

	@Test
	public void cleanParams() throws Exception {
		testingClass.setNewDescription("123");
		testingClass.setNewName("123");

		testingClass.cleanParams();

		assertTrue(StringUtils.isBlank(testingClass.getNewName()));
		assertTrue(StringUtils.isBlank(testingClass.getNewDescription()));
	}

	@Test
	public void create() throws Exception {
		testingClass.setNewDescription("123");
		testingClass.setNewName("456");
		testingClass.setNewMask("999");
		testingClass.setNewBlockedInterval(3);

		PhoneNumberSpecification spec = new PhoneNumberSpecification(1L);
		spec.setName("456");
		spec.setMask("999");
		spec.setDescription("123");
		spec.setBlockedInterval(3);

		when(phoneNumberSpecificationRepository.createPhoneNumberSpec(eq(testingClass.getNewName()),
				eq(testingClass.getNewDescription()), eq(testingClass.getNewMask()), eq(testingClass.getNewBlockedInterval())))
				.thenReturn(spec);

		PhoneNumberSpecification specification = testingClass.create();

		assertNotNull(specification);
		assertTrue(specification.getName().equals("456"));
		assertTrue(specification.getDescription().equals("123"));

		verify(phoneNumberSpecificationRepository, times(1))
				.createPhoneNumberSpec(eq("456"), eq("123"), eq("999"), eq(3));
	}
}