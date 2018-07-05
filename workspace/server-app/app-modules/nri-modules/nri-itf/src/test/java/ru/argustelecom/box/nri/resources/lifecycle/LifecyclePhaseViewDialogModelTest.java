package ru.argustelecom.box.nri.resources.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.component.menu.Menu;
import org.primefaces.event.RowEditEvent;

import javax.faces.context.FacesContext;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 16.11.2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FacesContext.class })
public class LifecyclePhaseViewDialogModelTest {

	@Mock
	private ResourceLifecycleAppService lifecycleAppService;

	@InjectMocks
	private LifecyclePhaseViewDialogModel testingClass;

	@Test
	public void shouldAddNewOutcomingPhase() throws Exception {
		testingClass.addNewOutcomingPhase();
		assertNotNull(testingClass.getOutcomingPhases());
		assertFalse(testingClass.getOutcomingPhases().isEmpty());

		Set<ResourceLifecyclePhaseDto> phases = new HashSet<>();
		phases.add(ResourceLifecyclePhaseDto.builder().build());
		testingClass.setPhases(phases);
		testingClass.addNewOutcomingPhase();

		assertNotNull(testingClass.getOutcomingPhases());
		assertFalse(testingClass.getOutcomingPhases().isEmpty());
	}

	@Test
	public void shouldClear() throws Exception {
		testingClass.addNewOutcomingPhase();
		assertNotNull(testingClass.getOutcomingPhases());
		assertFalse(testingClass.getOutcomingPhases().isEmpty());

		testingClass.clear();

		assertNotNull(testingClass.getOutcomingPhases());
		assertTrue(testingClass.getOutcomingPhases().isEmpty());
	}

	@Test
	public void shouldSetSelectedPhase() throws Exception {
		ResourceLifecyclePhaseDto phaseDto = ResourceLifecyclePhaseDto.builder().build();
		testingClass.setSelectedPhase(phaseDto);

		assertNotNull(testingClass.getSelectedPhase());
		assertEquals(phaseDto, testingClass.getSelectedPhase());

		assertNotNull(testingClass.getOutcomingPhases());
		assertTrue(testingClass.getOutcomingPhases().isEmpty());
	}

	@Test
	public void shouldAcceptEditValidOutcomingPhase() throws Exception {
		ResourceLifecyclePhaseTransitionDto holder = ResourceLifecyclePhaseTransitionDto
				.builder()
				.comment("comment1")
				.build();
		RowEditEvent editEvent = new RowEditEvent(new Menu(), new AjaxBehavior(), holder);

		testingClass.acceptEditNewOutcomingPhase(editEvent);

		verify(lifecycleAppService, times(1)).renameTransition(eq(holder), eq(holder.getComment()));
	}

	@Test
	public void shouldAcceptEditNewValidOutcomingPhase() throws Exception {
		ResourceLifecyclePhaseDto outPhaseDto = ResourceLifecyclePhaseDto.builder().build();
		ResourceLifecyclePhaseTransitionDto holder = ResourceLifecyclePhaseTransitionDto
				.builder()
				.comment("comment1")
				.outcomingPhase(outPhaseDto)
				.build();
		RowEditEvent editEvent = new RowEditEvent(new Menu(), new AjaxBehavior(), holder);
		ResourceLifecyclePhaseDto phaseDto = ResourceLifecyclePhaseDto.builder().build();
		testingClass.setSelectedPhase(phaseDto);

		when(lifecycleAppService.addNewTransition(any(), any(), any())).thenReturn(ResourceLifecyclePhaseTransitionDto
				.builder()
				.id(3333L)
				.build());

		testingClass.addNewOutcomingPhase();
		testingClass.acceptEditNewOutcomingPhase(editEvent);

		verify(lifecycleAppService, times(1))
				.addNewTransition(eq(phaseDto),eq(outPhaseDto), eq(holder.getComment()));
	}

	@Test
	public void shouldAcceptEditInvalidOutcomingPhase() throws Exception {
		ResourceLifecyclePhaseDto outPhaseDto = ResourceLifecyclePhaseDto.builder().id(1L).build();
		ResourceLifecyclePhaseTransitionDto holder = ResourceLifecyclePhaseTransitionDto
				.builder()
				.id(1L)
				.comment("")
				.outcomingPhase(outPhaseDto)
				.build();
		RowEditEvent editEvent = new RowEditEvent(new Menu(), new AjaxBehavior(), holder);

		PowerMockito.mockStatic(FacesContext.class);

		FacesContext facesContext = mock(FacesContext.class);
		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

		testingClass.addNewOutcomingPhase();
		testingClass.addNewOutcomingPhase();
		testingClass.acceptEditNewOutcomingPhase(editEvent);

		verify(facesContext, times(1)).addMessage(anyString(), any());
		verify(facesContext, times(1)).validationFailed();
	}

	@Test
	public void shouldCancelEditNewOutcomingPhase() throws Exception {
		testingClass.addNewOutcomingPhase();
		assertFalse(testingClass.getOutcomingPhases().isEmpty());

		testingClass.cancelEditNewOutcomingPhase();
		assertTrue(testingClass.getOutcomingPhases().isEmpty());
	}

	@Test
	public void shouldDeleteUnsavedTransition() throws Exception {
		testingClass.deleteTransition(null);

		testingClass.addNewOutcomingPhase();
		assertFalse(testingClass.getOutcomingPhases().isEmpty());
		ResourceLifecyclePhaseTransitionDto transitionDto = testingClass.getOutcomingPhases().stream().findFirst().get();

		ResourceLifecyclePhaseDto phaseDto = ResourceLifecyclePhaseDto.builder().build();
		testingClass.setSelectedPhase(phaseDto);

		testingClass.deleteTransition(transitionDto);
		assertTrue(testingClass.getOutcomingPhases().isEmpty());
	}

	@Test
	public void shouldDeleteSavedTransition() throws Exception {
		testingClass.addNewOutcomingPhase();
		assertFalse(testingClass.getOutcomingPhases().isEmpty());
		ResourceLifecyclePhaseTransitionDto transitionDto = testingClass.getOutcomingPhases().stream().findFirst().get();
		transitionDto.setId(1L);

		ResourceLifecyclePhaseDto phaseDto = ResourceLifecyclePhaseDto.builder().build();
		testingClass.setSelectedPhase(phaseDto);

		testingClass.deleteTransition(transitionDto);

		assertTrue(testingClass.getOutcomingPhases().isEmpty());
		verify(lifecycleAppService, times(1)).removeTransition(eq(transitionDto));
	}

	@Test
	public void shouldNotDeleteSavedTransitionDueItsOnlyOne() throws Exception {
		testingClass.addNewOutcomingPhase();
		assertFalse(testingClass.getOutcomingPhases().isEmpty());
		ResourceLifecyclePhaseTransitionDto transitionDto = testingClass.getOutcomingPhases().stream().findFirst().get();
		transitionDto.setId(1L);

		Set<ResourceLifecyclePhaseTransitionDto> transitions = new HashSet<>();
		transitions.add(transitionDto);
		ResourceLifecyclePhaseDto phaseDto = ResourceLifecyclePhaseDto.builder().outcomingPhases(transitions).build();
		testingClass.setSelectedPhase(phaseDto);

		when(lifecycleAppService.phaseHasAtLeastOneIncomingTransition(any())).thenReturn(false);

		PowerMockito.mockStatic(FacesContext.class);

		FacesContext facesContext = mock(FacesContext.class);
		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

		testingClass.deleteTransition(transitionDto);

		verify(facesContext, times(1)).addMessage(anyString(), any());
		verify(facesContext, times(1)).validationFailed();
	}

	@Test
	public void shouldChangePhaseInfo() throws Exception {
		ResourceLifecyclePhaseDto phaseDto = ResourceLifecyclePhaseDto.builder().build();
		testingClass.setSelectedPhase(phaseDto);

		testingClass.changePhaseInfo();

		verify(lifecycleAppService, times(1)).updatePhase(eq(phaseDto));
	}
}