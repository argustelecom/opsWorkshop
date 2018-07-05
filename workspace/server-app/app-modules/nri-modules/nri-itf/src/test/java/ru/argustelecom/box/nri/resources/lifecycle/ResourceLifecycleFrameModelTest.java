package ru.argustelecom.box.nri.resources.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 16.11.2017.
 */
@RunWith(PowerMockRunner.class)
public class ResourceLifecycleFrameModelTest {

	@Mock
	private ResourceLifecycleAppService lifecycleAppService;

	@InjectMocks
	private ResourceLifecycleFrameModel testingClass;

	@Test
	public void shouldPreRender() throws Exception {
		ResourceInstanceDto resourceInstanceDto = ResourceInstanceDto.builder().build();
		ResourceLifecyclePhaseDto phaseDto = ResourceLifecyclePhaseDto.builder().build();

		when(lifecycleAppService.getPhaseOfResource(eq(resourceInstanceDto))).thenReturn(phaseDto);

		testingClass.preRender(resourceInstanceDto);

		assertEquals(phaseDto, testingClass.getCurrentPhase());
		verify(lifecycleAppService, times(1)).getPhaseOfResource(eq(resourceInstanceDto));
	}

	@Test
	public void shouldGetRoutes() throws Exception {
		ResourceInstanceDto resourceInstanceDto = ResourceInstanceDto.builder().build();

		ResourceLifecyclePhaseTransitionDto transitionDto = ResourceLifecyclePhaseTransitionDto.builder().build();
		when(lifecycleAppService.loadTransitions(eq(resourceInstanceDto))).thenReturn(Arrays.asList(transitionDto));



		List<ResourceLifecyclePhaseTransitionDto> res = testingClass.getRoutes(resourceInstanceDto);

		assertNotNull(res);
		assertFalse(res.isEmpty());

		verify(lifecycleAppService, times(1)).loadTransitions(any());
	}

	@Test
	public void shouldGetRoutesForEmptyPhase() throws Exception {
		ResourceInstanceDto resourceInstanceDto = ResourceInstanceDto.builder().build();

		when(lifecycleAppService.loadTransitions(eq(resourceInstanceDto))).thenReturn(Collections.emptyList());
		when(lifecycleAppService.loadAllPossiblePhasesForResource(eq(resourceInstanceDto)))
				.thenReturn(Arrays.asList(ResourceLifecyclePhaseDto.builder().build()));

		List<ResourceLifecyclePhaseTransitionDto> res = testingClass.getRoutes(resourceInstanceDto);

		assertNotNull(res);
		assertFalse(res.isEmpty());

		verify(lifecycleAppService, times(1)).loadTransitions(any());
		verify(lifecycleAppService, times(1)).loadAllPossiblePhasesForResource(any());
	}

	@Test
	public void shouldGetHasLifecycle() throws Exception {
		ResourceInstanceDto resourceInstanceDto = ResourceInstanceDto.builder().build();
		testingClass.preRender(resourceInstanceDto);

		when(lifecycleAppService.hasLifecycle(eq(resourceInstanceDto))).thenReturn(true);

		assertTrue(testingClass.getHasLifecycle());
		verify(lifecycleAppService, times(1)).hasLifecycle(eq(resourceInstanceDto));
	}
}