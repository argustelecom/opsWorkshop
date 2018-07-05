package ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;
import ru.argustelecom.box.env.lifecycle.impl.factory.LifecycleBuilderImpl;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState.AVAILABLE;
import static ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState.DELETED;
import static ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState.OCCUPIED;

/**
 * @author d.khekk
 * @since 11.12.2017
 */
@RunWith(PowerMockRunner.class)
public class IPAddressLifecycleTest {

	@InjectMocks
	private IPAddressLifecycle lifecycle;

	@Test
	public void shouldCreateLifecycle() {
		LifecycleBuilderImpl<IPAddressState, IPAddress> builder = new LifecycleBuilderImpl<>();
		builder.begin();
		lifecycle.buildLifecycle(builder);
		Lifecycle<IPAddressState, IPAddress> lifecycle = builder.build();

		//из всех состояний есть пути
		assertTrue(lifecycle.hasRoutes(AVAILABLE));
		assertTrue(lifecycle.hasRoutes(OCCUPIED));
		//а из DELETED пути никуда нет
		assertFalse(lifecycle.hasRoutes(DELETED));
	}
}