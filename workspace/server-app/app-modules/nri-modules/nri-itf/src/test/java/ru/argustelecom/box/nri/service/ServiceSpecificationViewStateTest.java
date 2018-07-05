package ru.argustelecom.box.nri.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
@RunWith(MockitoJUnitRunner.class)
public class ServiceSpecificationViewStateTest {

	private ServiceSpecificationViewState state = new ServiceSpecificationViewState();
	@Test
	public void shouldSet(){
		state.setSpecification(ServiceSpec.builder().id(1L).build());
		assertNotNull(state.getSpecification());
		assertNotNull(state.getSpecification().getId());
		assertEquals(new Long(1L),state.getSpecification().getId());
	}
}