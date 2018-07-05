package ru.argustelecom.box.nri.resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.event.SelectEvent;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Street;
import ru.argustelecom.box.nri.building.BuildingElementAppService;
import ru.argustelecom.box.nri.building.BuildingElementDto;
import ru.argustelecom.box.nri.coverage.ResourceInstallationAppService;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;

import javax.faces.component.UIInput;
import javax.faces.event.ValueChangeEvent;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by s.kolyada on 29.09.2017.
 */
@RunWith(PowerMockRunner.class)
public class ResourceInstallationInfoFrameModelTest {

	/**
	 * Сервис для работы с установками ресурсов
	 */
	@Mock
	private ResourceInstallationAppService installationAppService;

	/**
	 * Сервис элементов строений
	 */
	@Mock
	private BuildingElementAppService buildingService;

	@InjectMocks
	private ResourceInstallationInfoFrameModel model;

	@Test
	public void shouldRenderInstallation() throws Exception {
		ResourceInstanceDto dto = ResourceInstanceDto.builder().build();

		BuildingElementDto buildingElementDto = BuildingElementDto.builder().build();
		ResourceInstallationDto installationDto = ResourceInstallationDto.builder().installedAt(buildingElementDto).build();

		when(installationAppService.findInstallationByResource(eq(dto))).thenReturn(installationDto);
		when(buildingService.findBuildingElementLocation(eq(buildingElementDto))).thenReturn(new Building(1L));

		model.preRender(dto);

		assertNotNull(model.getInstallation());
		assertNotNull(model.getLocation());

		verify(installationAppService, times(1)).findInstallationByResource(eq(dto));
		verify(buildingService, times(1)).findBuildingElementLocation(eq(buildingElementDto));
	}

	@Test
	public void shouldRenderInstallationWithLocation() throws Exception {
		ResourceInstanceDto dto = ResourceInstanceDto.builder().build();

		BuildingElementDto buildingElementDto = BuildingElementDto.builder().location(new Building(1L)).build();
		ResourceInstallationDto installationDto = ResourceInstallationDto.builder().installedAt(buildingElementDto).build();

		when(installationAppService.findInstallationByResource(eq(dto))).thenReturn(installationDto);

		model.preRender(dto);

		assertNotNull(model.getInstallation());
		assertNotNull(model.getLocation());

		verify(installationAppService, times(1)).findInstallationByResource(eq(dto));
		verify(buildingService, times(0)).findBuildingElementLocation(eq(buildingElementDto));
	}

	@Test
	public void shouldRenderWithoutInstallation() throws Exception {
		ResourceInstanceDto dto = ResourceInstanceDto.builder().build();

		model.preRender(dto);

		when(installationAppService.findInstallationByResource(eq(dto))).thenReturn(null);

		assertNull(model.getInstallation());
		assertNull(model.getLocation());

		verify(installationAppService, times(1)).findInstallationByResource(eq(dto));
	}

	@Test
	public void shouldDoNothingRemoveInstallationWithNullInstallation() {
		ResourceInstanceDto dto = ResourceInstanceDto.builder().build();
		model.preRender(dto);
		model.removeInstallation();
		verify(installationAppService, times(0)).deleteInstallation(any());
	}

	@Test
	public void shouldRemoveInstallationWithNullInstallation() {
		ResourceInstanceDto dto = ResourceInstanceDto.builder().id(1L).build();
		BuildingElementDto buildingElementDto = BuildingElementDto.builder().build();
		ResourceInstallationDto inst = ResourceInstallationDto.builder().id(1L).installedAt(buildingElementDto).build();
		when(installationAppService.findInstallationByResource(eq(dto))).thenReturn(inst);
		model.preRender(dto);
		model.removeInstallation();
		verify(installationAppService, times(1)).deleteInstallation(eq(inst));
	}

	@Test
	public void shouldCreateInstallation() throws Exception {
		ResourceInstanceDto dto = ResourceInstanceDto.builder().id(1L).build();
		BuildingElementDto buildingElementDto = BuildingElementDto.builder().build();
		ResourceInstallationDto installationDto = ResourceInstallationDto.builder().installedAt(buildingElementDto).build();
		when(installationAppService.createInstallation(any(), any())).thenReturn(installationDto);
		when(installationAppService.findInstallationByResource(eq(dto))).thenReturn(null);
		when(buildingService.findBuildingElementLocation(eq(buildingElementDto))).thenReturn(new Building(1L));

		model.preRender(dto);
		model.setSelectedBuildingElementDto(buildingElementDto);
		model.createNewInstallation();

		assertNotNull(model.getInstallation());
		assertNotNull(model.getLocation());
		assertNull(model.getSelectedBuildingElementDto());
		verify(buildingService, times(1)).findBuildingElementLocation(eq(buildingElementDto));
	}

	@Test
	public void shouldCreateNothing() throws Exception {
		model.createNewInstallation();

		assertNull(model.getInstallation());
	}

	@Test
	public void shouldCompleteLocation() throws Exception {
		when(buildingService.findAllByLocationName(any())).thenReturn(Collections.singletonList(BuildingElementDto.builder().id(1L).build()));

		List<BuildingElementDto> buildingElements = model.completeLocation("Name of location");

		assertNotNull(buildingElements);
		assertFalse(buildingElements.isEmpty());
	}

	@Test
	public void shouldCleanCreationParams() throws Exception {
		model.setSelectedBuildingElementDto(BuildingElementDto.builder().build());

		model.cleanCreationParams();

		assertNull(model.getSelectedBuildingElementDto());
	}

	@Test
	public void shouldSelectLocation() throws Exception {
		when(buildingService.findElementByLocation(any())).thenReturn(BuildingElementDto.builder().build());

		model.selectLocation(new SelectEvent(new UIInput(), new AjaxBehavior(), BuildingElementDto.builder().id(1L).build()));

		assertNotNull(model.getSelectedBuildingElementDto());
	}

	@Test
	public void shouldSelectLocation_2() throws Exception {
		when(buildingService.findElementByLocation(any())).thenReturn(BuildingElementDto.builder().build());

		model.selectLocation(new ValueChangeEvent(new UIInput(), new AjaxBehavior(), BuildingElementDto.builder().id(1L).build()));

		assertNotNull(model.getSelectedBuildingElementDto());
	}

	@Test
	public void shouldReturnNameOfLocation() throws Exception {
		Street street = new Street(1L);
		street.setName("Красного Курсанта");
		Building building = new Building(1L);
		building.setName("25");
		building.setParent(street);

		String locationName = model.getName(building);

		assertEquals("Красного Курсанта, 25", locationName);
	}
}