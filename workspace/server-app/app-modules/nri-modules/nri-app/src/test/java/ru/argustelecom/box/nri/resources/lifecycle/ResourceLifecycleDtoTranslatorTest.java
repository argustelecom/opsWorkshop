package ru.argustelecom.box.nri.resources.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by s.kolyada on 17.11.2017.
 */
@RunWith(PowerMockRunner.class)
public class ResourceLifecycleDtoTranslatorTest {

	@Mock
	private ResourceLifecyclePhaseDtoTranslator phaseTranslator;

	@InjectMocks
	private ResourceLifecycleDtoTranslator testingClass;

	@Test
	public void shouldTranslateNull() throws Exception {
		assertNull(testingClass.translate(null));
	}

	@Test
	public void shouldTranslate() throws Exception {
		ResourceLifecycle lifecycle = new ResourceLifecycle(1L);
		lifecycle.setName("nn");

		ResourceLifecycleDto dto = testingClass.translate(lifecycle);
		assertNotNull(dto);
		assertEquals("nn", dto.getName());
	}
}