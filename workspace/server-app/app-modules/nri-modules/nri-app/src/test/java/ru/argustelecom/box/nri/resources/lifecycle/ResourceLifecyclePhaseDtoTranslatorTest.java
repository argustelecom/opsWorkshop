package ru.argustelecom.box.nri.resources.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhase;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 17.11.2017.
 */
@RunWith(PowerMockRunner.class)
public class ResourceLifecyclePhaseDtoTranslatorTest {

	@InjectMocks
	private ResourceLifecyclePhaseDtoTranslator testingClass;

	@Test
	public void shouldTranslateNull() throws Exception {
		assertNull(testingClass.translate(null));
	}

	@Test
	public void shouldTranslate() throws Exception {
		ResourceLifecyclePhase phase = ResourceLifecyclePhase.builder().build();

		assertNotNull(testingClass.translate(phase));
	}
}