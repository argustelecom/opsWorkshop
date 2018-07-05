package ru.argustelecom.box.nri.logicalresources;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.type.CurrentType;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by s.kolyada on 01.11.2017.
 */
@RunWith(PowerMockRunner.class)
public class LogicalResourceSpecAttributesFrameModelTest {




	@Mock
	private CurrentType cs;

	@InjectMocks
	private LogicalResourceSpecAttributesFrameModel testingClass;


	@Test
	public void preRender() throws Exception {
		CustomerType specification = mock(CustomerType.class);
		when(cs.getValue()).thenReturn(specification);
		when(cs.changed(any())).thenReturn(true);

		testingClass.preRender();

		testingClass.getResourceType();

		assertNull(testingClass.getPhoneNumberSpec());
	}

	@Test
	public void preRenderNotChangedSpec() throws Exception {
		CustomerType specification =  mock(CustomerType.class);;
		when(cs.getValue()).thenReturn(specification);
		when(cs.changed(any())).thenReturn(false);

		testingClass.preRender();
		assertNull(testingClass.getPhoneNumberSpec());
	}

	@Test
	public void preRenderNullSpec() throws Exception {
		when(cs.getValue()).thenReturn(null);
		when(cs.changed(any())).thenReturn(true);

		testingClass.preRender();
		assertNull(testingClass.getPhoneNumberSpec());
	}

	@Test
	public void getCurrentSpecIconValue() throws Exception {
		PhoneNumberSpecification specification = new PhoneNumberSpecification(1L);
		when(cs.getValue()).thenReturn(specification);
		when(cs.changed(any())).thenReturn(true);

		testingClass.preRender();
		assertEquals(LogicalResNodeType.PHONE_NUMBER, testingClass.getResourceType());
		assertTrue(StringUtils.isNotBlank(testingClass.getCurrentSpecIconValue()));
	}
}