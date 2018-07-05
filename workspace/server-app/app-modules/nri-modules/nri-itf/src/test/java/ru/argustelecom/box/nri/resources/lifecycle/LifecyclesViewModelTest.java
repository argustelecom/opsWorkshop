package ru.argustelecom.box.nri.resources.lifecycle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecycle;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 16.11.2017.
 */
@RunWith(PowerMockRunner.class)
public class LifecyclesViewModelTest {

	@Mock
	private ResourceLifecycleRepository lifecycleRepository;

	@Mock
	private ResourceLifecycleDtoTranslator lifecycleDtoTranslator;

	@Mock
	private UnitOfWork unitOfWork;

	@InjectMocks
	private LifecyclesViewModel testingClass;

	List<ResourceLifecycle> lifecycles;

	@Before
	public void setUp() throws Exception {
		lifecycles = new ArrayList<>();

		ResourceLifecycle lifecycle = new ResourceLifecycle(1L);

		lifecycles.add(lifecycle);

		when(lifecycleRepository.findAll()).thenReturn(lifecycles);
		when(lifecycleDtoTranslator.translate(eq(lifecycle))).thenReturn(ResourceLifecycleDto.builder().id(lifecycle.getId()).build());
	}

	@Test
	public void postConstructCall() throws Exception {

		testingClass.postConstruct();

		assertNotNull(testingClass.getLifecycles());
		assertNotNull(testingClass.getLifecycles().getChildren().get(0).getData());

		verify(unitOfWork, times(1)).makePermaLong();
		verify(lifecycleRepository, times(1)).findAll();
	}

	@Test
	public void shouldGetSelectedLifecycle() throws Exception {
		testingClass.postConstruct();

		ResourceLifecycleDto lifecycleDto = testingClass.getSelectedLifecycle();
		assertNull(lifecycleDto);

		testingClass.setSelectedNode(testingClass.getLifecycles().getChildren().get(0));

		lifecycleDto = testingClass.getSelectedLifecycle();

		assertNotNull(lifecycleDto);
		assertTrue(lifecycleDto.getId().equals(1L));
	}
}