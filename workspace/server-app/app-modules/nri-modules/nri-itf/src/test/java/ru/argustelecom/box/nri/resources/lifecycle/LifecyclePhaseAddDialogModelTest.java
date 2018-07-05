package ru.argustelecom.box.nri.resources.lifecycle;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.component.menu.Menu;
import org.primefaces.event.RowEditEvent;
import ru.argustelecom.box.inf.util.Callback;

import javax.faces.context.FacesContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by s.kolyada on 17.11.2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FacesContext.class })
public class LifecyclePhaseAddDialogModelTest {

	private Callback<ResourceLifecyclePhaseDto> phaseDtoCallback = instance -> {

	};

	private Callback<ResourceLifecyclePhaseTransitionDto> transitionDtoCallback = instance -> {

	};

	@Mock
	private ResourceLifecycleAppService lifecycleAppService;

	@InjectMocks
	private LifecyclePhaseAddDialogModel testingClass;

	@Test
	public void shouldResturnDialogHeader() throws Exception {
		assertNotNull(testingClass.getDialogHeader());

		testingClass.setPhaseCreationMode(true);

		assertNotNull(testingClass.getDialogHeader());
	}

	@Test
	public void shouldCreateNewPhaseTransition() throws Exception {
		ResourceLifecyclePhaseTransitionDto newTransitionDto = ResourceLifecyclePhaseTransitionDto.builder().build();
		when(lifecycleAppService.addNewTransition(any(), any(), anyString())).thenReturn(newTransitionDto);

		testingClass.setTransitionCallback(transitionDtoCallback);

		testingClass.createNewPhaseTransition();

		verify(lifecycleAppService, times(1)).addNewTransition(any(), any(), anyString());
	}

	@Test
	public void shouldCreateNewPhase() throws Exception {
		ResourceLifecyclePhaseDto phaseDto = ResourceLifecyclePhaseDto.builder().build();
		when(lifecycleAppService.createPhase(any(), anyString())).thenReturn(phaseDto);

		ResourceLifecyclePhaseTransitionDto transitionDto = ResourceLifecyclePhaseTransitionDto.builder().build();
		when(lifecycleAppService.addNewTransition(any(), any(), anyString())).thenReturn(transitionDto);

		testingClass.setPhases(Collections.emptySet());
		testingClass.setPhaseCallback(phaseDtoCallback);
		testingClass.setTransitionCallback(transitionDtoCallback);
		testingClass.addNewIncomingPhase();
		testingClass.addNewOutcomingPhase();

		testingClass.createNewPhase();

		verify(lifecycleAppService, times(1)).createPhase(any(), anyString());
		verify(lifecycleAppService, times(2)).addNewTransition(any(), any(), anyString());
	}

	@Test
	public void shouldAddNewIncomingPhase() throws Exception {
		assertTrue(CollectionUtils.isEmpty(testingClass.getNewIncomingPhases()));
		testingClass.addNewIncomingPhase();
		assertFalse(CollectionUtils.isEmpty(testingClass.getNewIncomingPhases()));
	}

	@Test
	public void shouldAddNewOutcomingPhase() throws Exception {
		assertTrue(CollectionUtils.isEmpty(testingClass.getNewOutcomingPhases()));
		testingClass.addNewOutcomingPhase();
		assertFalse(CollectionUtils.isEmpty(testingClass.getNewOutcomingPhases()));
	}

	@Test
	public void shouldAcceptEditNewIncomingPhase() throws Exception {
		ResourceLifecyclePhaseDto phase = ResourceLifecyclePhaseDto.builder().build();
		LifecyclePhaseAddDialogModel.PhaseTransitionHolder holder
				= new LifecyclePhaseAddDialogModel.PhaseTransitionHolder(phase, "name");
		RowEditEvent event = new RowEditEvent(new Menu(), new AjaxBehavior(), holder);

		testingClass.acceptEditNewIncomingPhase(event);
	}

	@Test
	public void shouldAcceptEditNewInvalidIncomingPhase() throws Exception {
		ResourceLifecyclePhaseDto phase = ResourceLifecyclePhaseDto.builder().build();
		Set<ResourceLifecyclePhaseDto> phs = new HashSet<>();
		phs.add(phase);
		testingClass.setPhases(phs);
		LifecyclePhaseAddDialogModel.PhaseTransitionHolder holder
				= new LifecyclePhaseAddDialogModel.PhaseTransitionHolder(phase, "name");
		RowEditEvent event = new RowEditEvent(new Menu(), new AjaxBehavior(), holder);

		PowerMockito.mockStatic(FacesContext.class);

		FacesContext facesContext = mock(FacesContext.class);
		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

		testingClass.addNewIncomingPhase();
		testingClass.addNewIncomingPhase();
		testingClass.acceptEditNewIncomingPhase(event);

		verify(facesContext, times(1)).addMessage(anyString(), any());
		verify(facesContext, times(1)).validationFailed();
	}

	@Test
	public void shouldAcceptEditNewInvalidOutcomingPhase() throws Exception {
		ResourceLifecyclePhaseDto phase = ResourceLifecyclePhaseDto.builder().build();
		Set<ResourceLifecyclePhaseDto> phs = new HashSet<>();
		phs.add(phase);
		testingClass.setPhases(phs);
		LifecyclePhaseAddDialogModel.PhaseTransitionHolder holder
				= new LifecyclePhaseAddDialogModel.PhaseTransitionHolder(phase, "name");
		RowEditEvent event = new RowEditEvent(new Menu(), new AjaxBehavior(), holder);

		PowerMockito.mockStatic(FacesContext.class);

		FacesContext facesContext = mock(FacesContext.class);
		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

		testingClass.addNewOutcomingPhase();
		testingClass.addNewOutcomingPhase();
		testingClass.acceptEditNewOutcomingPhase(event);

		verify(facesContext, times(1)).addMessage(anyString(), any());
		verify(facesContext, times(1)).validationFailed();
	}

	@Test
	public void shouldCancelEditNewIncomingPhase() throws Exception {
		assertTrue(CollectionUtils.isEmpty(testingClass.getNewIncomingPhases()));
		testingClass.cancelEditNewIncomingPhase();
		assertTrue(CollectionUtils.isEmpty(testingClass.getNewIncomingPhases()));
		testingClass.addNewIncomingPhase();
		assertFalse(CollectionUtils.isEmpty(testingClass.getNewIncomingPhases()));
		testingClass.cancelEditNewIncomingPhase();
		assertTrue(CollectionUtils.isEmpty(testingClass.getNewIncomingPhases()));
	}

	@Test
	public void shouldCancelEditNewOutcomingPhase() throws Exception {
		assertTrue(CollectionUtils.isEmpty(testingClass.getNewOutcomingPhases()));
		testingClass.cancelEditNewOutcomingPhase();
		assertTrue(CollectionUtils.isEmpty(testingClass.getNewOutcomingPhases()));
		testingClass.addNewOutcomingPhase();
		assertFalse(CollectionUtils.isEmpty(testingClass.getNewOutcomingPhases()));
		testingClass.cancelEditNewOutcomingPhase();
		assertTrue(CollectionUtils.isEmpty(testingClass.getNewOutcomingPhases()));
	}
}