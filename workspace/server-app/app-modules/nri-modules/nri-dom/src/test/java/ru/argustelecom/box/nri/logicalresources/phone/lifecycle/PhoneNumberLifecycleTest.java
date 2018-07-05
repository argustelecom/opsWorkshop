package ru.argustelecom.box.nri.logicalresources.phone.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;
import ru.argustelecom.box.env.lifecycle.impl.factory.LifecycleBuilderImpl;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState.AVAILABLE;
import static ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState.DELETED;
import static ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState.LOCKED;
import static ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState.OCCUPIED;

/**
 * @author d.khekk
 * @since 31.10.2017
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberLifecycleTest {

	@InjectMocks
	private PhoneNumberLifecycle lifecycle;

	@Test
	public void shouldCreateLifecycle() throws Exception {
		LifecycleBuilderImpl<PhoneNumberState, PhoneNumber> builder = new LifecycleBuilderImpl<>();
		builder.begin();
		lifecycle.buildLifecycle(builder);
		Lifecycle<PhoneNumberState, PhoneNumber> lifecycle = builder.build();

		//из всех состояний есть пути
		assertTrue(lifecycle.hasRoutes(AVAILABLE));
		assertTrue(lifecycle.hasRoutes(LOCKED));
		assertTrue(lifecycle.hasRoutes(OCCUPIED));
		//а из DELETED пути никуда нет
		assertFalse(lifecycle.hasRoutes(DELETED));
	}
}