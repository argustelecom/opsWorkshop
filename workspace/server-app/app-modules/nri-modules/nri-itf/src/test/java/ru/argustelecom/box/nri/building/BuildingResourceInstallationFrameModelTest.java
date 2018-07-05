package ru.argustelecom.box.nri.building;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.coverage.ResourceInstallationAppService;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by s.kolyada on 14.09.2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FacesContext.class})
public class BuildingResourceInstallationFrameModelTest {

	@Mock
	private ResourceInstallationAppService service;

	@InjectMocks
	private BuildingResourceInstallationFrameModel frameModel;

	@Test
	public void shouldInitDuePreRender() throws Exception {
		BuildingElementDto dto = BuildingElementDto.builder().id(1L).isRoot(false).build();
		ResourceInstallationDto resInstDto = ResourceInstallationDto.builder().build();

		when(service.findAllByBuildingElement(eq(1L))).thenReturn(Collections.singletonList(resInstDto));
		when(service.findAllCoveringBuildingElement(eq(1L))).thenReturn(Collections.singletonList(resInstDto));

		frameModel.preRender(dto);

		assertFalse(frameModel.getCover().isEmpty());
		assertFalse(frameModel.getInstalledResources().isEmpty());

		verify(service, times(1)).findAllByBuildingElement(eq(1L));
		verify(service, times(1)).findAllCoveringBuildingElement(eq(1L));
	}

	@Test
	public void shuoldCallRedirect() throws Exception {
		ResourceInstallationDto selectedDto = ResourceInstallationDto.builder().id(1L).build();
		frameModel.setSelectedInstallation(selectedDto);

		PowerMockito.mockStatic(FacesContext.class);
		FacesContext facesContext = mock(FacesContext.class);
		ExternalContext externalContext = mock(ExternalContext.class);

		when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
		when(facesContext.getExternalContext()).thenReturn(externalContext);

		frameModel.installedResourcesRowSelect(null);

		verify(externalContext, times(1)).redirect(eq("ResourceInstallationView.xhtml?installation=ResourceInstallation-1"));
	}

	@Test
	public void shouldSetInstallation() throws Exception {
		ResourceInstallationDto resInstDto = ResourceInstallationDto.builder().build();
		frameModel.setSelectedInstallation(resInstDto);
		assertNotNull(frameModel.getSelectedInstallation());
	}

	@Test
	public void shouldSetNullWhenElementNull() throws Exception {
		frameModel.preRender(null);
		assertTrue(frameModel.getInstalledResources().isEmpty());
		assertTrue(frameModel.getCover().isEmpty());
	}
}