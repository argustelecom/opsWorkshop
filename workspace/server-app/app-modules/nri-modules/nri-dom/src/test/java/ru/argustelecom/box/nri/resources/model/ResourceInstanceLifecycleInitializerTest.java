package ru.argustelecom.box.nri.resources.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecycle;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhase;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 20.11.2017.
 */
@RunWith(PowerMockRunner.class)
public class ResourceInstanceLifecycleInitializerTest {

	@InjectMocks
	private ResourceInstanceLifecycleInitializer testingClass;

	@Test
	public void shouldInit() throws Exception {
		ResourceLifecycle lifecycle = new ResourceLifecycle(1L);
		lifecycle.setInitialPhase(ResourceLifecyclePhase.builder().build());
		ResourceInstance resourceInstance = ResourceInstance
				.builder()
				.specification(ResourceSpecification
						.builder()
						.build())
				.build();

		assertNull(resourceInstance.getCurrentLifecyclePhase());
		testingClass.initDefaultLifecyclePhase(resourceInstance);

		assertNull(resourceInstance.getCurrentLifecyclePhase());

		resourceInstance.getSpecification().setLifecycle(lifecycle);

		testingClass.initDefaultLifecyclePhase(resourceInstance);
		assertNotNull(resourceInstance.getCurrentLifecyclePhase());
	}
}