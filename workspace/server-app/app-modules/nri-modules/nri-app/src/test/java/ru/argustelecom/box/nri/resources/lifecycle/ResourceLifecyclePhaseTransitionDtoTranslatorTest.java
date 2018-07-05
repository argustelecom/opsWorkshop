package ru.argustelecom.box.nri.resources.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhaseTransition;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by s.kolyada on 17.11.2017.
 */
@RunWith(PowerMockRunner.class)
public class ResourceLifecyclePhaseTransitionDtoTranslatorTest {

	@InjectMocks
	private ResourceLifecyclePhaseTransitionDtoTranslator testingClass;

	@Test
	public void shouldTranslateNull() throws Exception {
		assertNull(testingClass.translate(null));
	}

	@Test
	public void shouldTranslate() throws Exception {
		ResourceLifecyclePhaseTransition transition = ResourceLifecyclePhaseTransition.builder().build();

		assertNotNull(testingClass.translate(transition));
	}
}