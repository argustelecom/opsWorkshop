package ru.argustelecom.box.nri.resources.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.resources.ResourceInstanceRepository;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDtoTranslator;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecycle;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhase;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhaseRepository;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhaseTransition;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhaseTransitionRepository;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationRepository;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 20.11.2017.
 */
@RunWith(PowerMockRunner.class)
public class ResourceLifecycleAppServiceTest {

	@Mock
	private ResourceLifecyclePhaseRepository phaseRepository;

	@Mock
	private ResourceLifecycleRepository lifecycleRepository;

	@Mock
	private ResourceLifecyclePhaseDtoTranslator phaseDtoTranslator;

	@Mock
	private ResourceLifecyclePhaseTransitionRepository transitionRepository;

	@Mock
	private ResourceLifecyclePhaseTransitionDtoTranslator transitionDtoTranslator;

	@Mock
	private ResourceSpecificationDtoTranslator resourceSpecificationDtoTranslator;

	@Mock
	private ResourceInstanceRepository resourceRepository;

	@Mock
	private ResourceSpecificationRepository resourceSpecificationRepository;

	@Mock
	private ResourceInstanceDtoTranslator resourceInstanceDtoTranslator;

	@InjectMocks
	private ResourceLifecycleAppService testingClass;

	@Test
	public void shouldCheckPhaseHasAtLeastOneIncomingTransition() throws Exception {
		assertFalse(testingClass.phaseHasAtLeastOneIncomingTransition(null));

		ResourceLifecyclePhaseDto phaseDto = ResourceLifecyclePhaseDto.builder().build();

		assertFalse(testingClass.phaseHasAtLeastOneIncomingTransition(phaseDto));

		phaseDto = ResourceLifecyclePhaseDto.builder().id(1L).build();

		Set<ResourceLifecyclePhaseTransition> inTrans = new HashSet<>();
		ResourceLifecyclePhaseTransition transition = ResourceLifecyclePhaseTransition.builder().build();
		inTrans.add(transition);
		ResourceLifecyclePhase phase = ResourceLifecyclePhase.builder().incomingPhases(inTrans).build();

		when(phaseRepository.findById(eq(1L))).thenReturn(phase);

		assertTrue(testingClass.phaseHasAtLeastOneIncomingTransition(phaseDto));
	}

	@Test
	public void shouldLoadAllLifecyclePhases() throws Exception {
		ResourceLifecycleDto lifecycleDto = ResourceLifecycleDto.builder().build();

		when(lifecycleRepository.findById(anyLong())).thenReturn(new ResourceLifecycle(1L));

		List<ResourceLifecyclePhase> phases = new ArrayList<>();
		ResourceLifecyclePhase phase = ResourceLifecyclePhase.builder().build();
		phases.add(phase);
		when(lifecycleRepository.findAllPhases(any())).thenReturn(phases);

		when(phaseDtoTranslator.translate(any())).thenReturn(ResourceLifecyclePhaseDto.builder().build());

		List<ResourceLifecyclePhaseDto> res = testingClass.loadAllLifecyclePhases(lifecycleDto);

		assertNotNull(res);
		assertFalse(res.isEmpty());

	}

	@Test
	public void shouldAddNewTransition() throws Exception {
		ResourceLifecyclePhaseDto from = ResourceLifecyclePhaseDto.builder().id(1L).build();
		ResourceLifecyclePhase f = ResourceLifecyclePhase.builder().id(1L).build();

		ResourceLifecyclePhaseDto to = ResourceLifecyclePhaseDto.builder().id(2L).build();
		ResourceLifecyclePhase t = ResourceLifecyclePhase.builder().id(2L).build();

		when(phaseRepository.findById(eq(1L))).thenReturn(f);
		when(phaseRepository.findById(eq(2L))).thenReturn(t);
		when(transitionRepository.createTransition(eq(f), eq(t), eq("name"))).thenReturn(ResourceLifecyclePhaseTransition.builder().build());
		when(transitionDtoTranslator.translate(any())).thenReturn(ResourceLifecyclePhaseTransitionDto.builder().build());

		ResourceLifecyclePhaseTransitionDto res = testingClass.addNewTransition(from, to, "name");

		assertNotNull(res);
		assertTrue(from.getOutcomingPhases().contains(res));
		verify(phaseRepository, times(2)).findById(anyLong());
		verify(transitionRepository, times(1)).createTransition(eq(f), eq(t), eq("name"));
	}

	@Test
	public void shouldCreatePhase() throws Exception {
		ResourceLifecycleDto lifecycleDto = ResourceLifecycleDto.builder().build();

		when(lifecycleRepository.findById(anyLong())).thenReturn(new ResourceLifecycle(1L));
		when(phaseRepository.createPhase(any(), eq("name"))).thenReturn(ResourceLifecyclePhase.builder().build());
		when(phaseDtoTranslator.translate(any())).thenReturn(ResourceLifecyclePhaseDto.builder().phaseName("name").build());

		ResourceLifecyclePhaseDto res = testingClass.createPhase(lifecycleDto, "name");

		assertNotNull(res);

		verify(phaseRepository, times(1)).createPhase(any(), anyString());
	}

	@Test
	public void shouldSaveCoordinates() throws Exception {
		ResourceLifecyclePhaseDto from = ResourceLifecyclePhaseDto.builder().id(1L).build();

		when(phaseRepository.updateCoordinates(anyLong(), anyString(), anyString())).thenReturn(ResourceLifecyclePhase.builder().build());
		when(phaseDtoTranslator.translate(any())).thenReturn(ResourceLifecyclePhaseDto.builder().build());

		ResourceLifecyclePhaseDto res = testingClass.saveCoordinates(from, "1", "2");

		assertNotNull(res);
		verify(phaseRepository, times(1)).updateCoordinates(eq(1L), eq("1"), eq("2"));
	}

	@Test
	public void shouldUpdatePhse() throws Exception {
		ResourceLifecyclePhaseDto from = ResourceLifecyclePhaseDto.builder().id(1L).build();
		ResourceLifecyclePhase f = ResourceLifecyclePhase.builder().id(1L).build();

		when(phaseRepository.findById(eq(1L))).thenReturn(f);
		when(phaseRepository.savePhase(eq(f))).thenReturn(f);
		when(phaseDtoTranslator.translate(eq(f))).thenReturn(from);

		ResourceLifecyclePhaseDto res = testingClass.updatePhase(from);

		assertNotNull(res);
		verify(phaseRepository, times(1)).savePhase(eq(f));
	}

	@Test
	public void shouldRemoveTransition() throws Exception {
		ResourceLifecyclePhaseTransitionDto transitionDto = ResourceLifecyclePhaseTransitionDto.builder().id(1L).build();

		testingClass.removeTransition(transitionDto);

		verify(transitionRepository, times(1)).removeTransition(eq(1L));
	}

	@Test
	public void shouldRenameTransition() throws Exception {
		ResourceLifecyclePhaseTransitionDto transitionDto = ResourceLifecyclePhaseTransitionDto.builder().id(1L).build();

		when(transitionRepository.rename(eq(1L), eq("newName"))).thenReturn(ResourceLifecyclePhaseTransition.builder().build());
		when(transitionDtoTranslator.translate(any())).thenReturn(transitionDto);

		ResourceLifecyclePhaseTransitionDto res = testingClass.renameTransition(transitionDto, "newName");

		assertNotNull(res);
		verify(transitionRepository, times(1)).rename(eq(1L), eq("newName"));
	}

	@Test
	public void shouldFindResourceSpecificationsWithLifecycle() throws Exception {
		ResourceLifecycleDto lifecycleDto = ResourceLifecycleDto.builder().id(1L).build();

		when(lifecycleRepository.findById(eq(1L))).thenReturn(new ResourceLifecycle(1L));

		List<ResourceSpecificationDto> res = testingClass.findResourceSpecificationsWithLifecycle(lifecycleDto);
		assertNotNull(res);
		assertTrue(res.isEmpty());

		when(lifecycleRepository.findResourceSpecificationsWithLifecycle(any())).thenReturn(Arrays.asList(ResourceSpecification.builder().build()));
		when(resourceSpecificationDtoTranslator.translate(any())).thenReturn(ResourceSpecificationDto.builder().build());

		res = testingClass.findResourceSpecificationsWithLifecycle(lifecycleDto);
		assertNotNull(res);
		assertFalse(res.isEmpty());

		verify(lifecycleRepository, times(2)).findResourceSpecificationsWithLifecycle(any());
	}

	@Test
	public void shouldGetPhaseOfResource() throws Exception {
		ResourceInstanceDto resourceInstanceDto = ResourceInstanceDto.builder().id(1L).build();
		ResourceLifecyclePhase phase = ResourceLifecyclePhase.builder().build();
		when(resourceRepository.findOne(eq(1L))).thenReturn(ResourceInstance.builder().currentLifecyclePhase(phase).build());

		when(phaseDtoTranslator.translate(any())).thenReturn(ResourceLifecyclePhaseDto.builder().build());

		ResourceLifecyclePhaseDto res = testingClass.getPhaseOfResource(resourceInstanceDto);

		assertNotNull(res);
		verify(resourceRepository, times(1)).findOne(anyLong());
	}

	@Test
	public void shouldLoadTransitions() throws Exception {
		ResourceInstanceDto resourceInstanceDto = ResourceInstanceDto.builder().id(1L).build();

		when(resourceRepository.findOne(eq(1L))).thenReturn(ResourceInstance.builder().build());

		List<ResourceLifecyclePhaseTransitionDto> res = testingClass.loadTransitions(resourceInstanceDto);

		assertNotNull(res);
		assertTrue(res.isEmpty());

		ResourceLifecyclePhase phase = ResourceLifecyclePhase.builder().build();
		ResourceLifecyclePhaseTransition transition = ResourceLifecyclePhaseTransition.builder().build();
		phase.getOutcomingPhases().add(transition);
		when(resourceRepository.findOne(eq(1L))).thenReturn(ResourceInstance.builder().currentLifecyclePhase(phase).build());

		when(transitionDtoTranslator.translate(any())).thenReturn(ResourceLifecyclePhaseTransitionDto.builder().build());

		res = testingClass.loadTransitions(resourceInstanceDto);

		assertNotNull(res);
		assertFalse(res.isEmpty());
	}

	@Test
	public void shouldCheckHasLifecycle() throws Exception {
		ResourceSpecificationDto specificationDto = ResourceSpecificationDto.builder().id(1L).build();
		ResourceInstanceDto resourceInstanceDto = ResourceInstanceDto.builder().id(1L).specification(specificationDto).build();

		when(resourceSpecificationRepository.findOne(eq(1L))).thenReturn(ResourceSpecification.builder().build());

		assertFalse(testingClass.hasLifecycle(resourceInstanceDto));

		when(resourceSpecificationRepository.findOne(eq(1L))).thenReturn(ResourceSpecification.builder().lifecycle(new ResourceLifecycle(1L)).build());

		assertTrue(testingClass.hasLifecycle(resourceInstanceDto));
	}

	@Test
	public void shouldChangeResourcePhase() throws Exception {
		ResourceInstanceDto resourceInstanceDto = ResourceInstanceDto.builder().id(1L).build();
		ResourceInstance resourceInstance = ResourceInstance.builder().id(1L).build();

		ResourceLifecyclePhase phase = ResourceLifecyclePhase.builder().build();
		ResourceLifecyclePhaseDto phaseDto = ResourceLifecyclePhaseDto.builder().id(1L).build();

		when(resourceRepository.findOne(eq(1L))).thenReturn(resourceInstance);
		when(phaseRepository.findById(eq(1L))).thenReturn(phase);
		when(lifecycleRepository.updateResourcePhase(eq(resourceInstance), eq(phase))).thenReturn(resourceInstance);
		when(resourceInstanceDtoTranslator.translate(eq(resourceInstance))).thenReturn(resourceInstanceDto);

		ResourceInstanceDto res = testingClass.changeResourcePhase(resourceInstanceDto, phaseDto);

		assertNotNull(res);
		verify(lifecycleRepository, times(1)).updateResourcePhase(any(),any());
	}

	@Test
	public void shouldLoadAllPossiblePhasesForResource() throws Exception {
		ResourceInstanceDto resourceInstanceDto = ResourceInstanceDto.builder().id(1L).build();
		ResourceSpecification specification = ResourceSpecification.builder().build();
		ResourceInstance resourceInstance = ResourceInstance.builder().id(1L).specification(specification).build();
		when(resourceRepository.findOne(eq(1L))).thenReturn(resourceInstance);

		List<ResourceLifecyclePhaseDto> res = testingClass.loadAllPossiblePhasesForResource(resourceInstanceDto);

		assertNotNull(res);
		assertTrue(res.isEmpty());

		ResourceLifecycle lifecycle = new ResourceLifecycle(1L);
		specification = ResourceSpecification.builder().lifecycle(lifecycle).build();
		resourceInstance = ResourceInstance.builder().id(1L).specification(specification).build();
		when(resourceRepository.findOne(eq(1L))).thenReturn(resourceInstance);

		when(lifecycleRepository.findById(anyLong())).thenReturn(new ResourceLifecycle(1L));

		List<ResourceLifecyclePhase> phases = new ArrayList<>();
		ResourceLifecyclePhase phase = ResourceLifecyclePhase.builder().build();
		phases.add(phase);
		when(lifecycleRepository.findAllPhases(any())).thenReturn(phases);

		when(phaseDtoTranslator.translate(any())).thenReturn(ResourceLifecyclePhaseDto.builder().build());

		res = testingClass.loadAllPossiblePhasesForResource(resourceInstanceDto);

		assertNotNull(res);
		assertFalse(res.isEmpty());
	}

	@Test
	public void shouldRemovePhase() throws Exception {
		ResourceLifecyclePhaseDto remove = ResourceLifecyclePhaseDto.builder().id(1L).build();
		ResourceLifecyclePhaseDto substitution = ResourceLifecyclePhaseDto.builder().id(2L).build();

		testingClass.removePhase(remove, substitution);
		verify(phaseRepository, times(1)).remove(eq(1L), eq(2L));
	}
}