package ru.argustelecom.box.nri.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author d.khekk
 * @since 10.10.2017
 */
@RunWith(PowerMockRunner.class)
public class ServiceSpecificationsViewModelTest {

	@Mock
	private ServiceSpecificationRepository service;

	@InjectMocks
	private ServiceSpecificationsViewModel model;

	@Test
	public void shouldReturnSpecifications() throws Exception {
		when(service.findAll()).thenReturn(Collections.singletonList(ServiceSpec.builder().build()));

		assertNull(model.getAllSpecifications());

		List<ServiceSpec> specifications1 = model.getSpecifications();

		assertNotNull(model.getAllSpecifications());
		assertFalse(specifications1.isEmpty());

		List<ServiceSpec> specifications2 = model.getSpecifications();

		assertEquals(specifications1, specifications2);
	}
}