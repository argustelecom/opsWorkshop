package ru.argustelecom.box.nri.map.network.accessports;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.nri.building.BuildingElementAppService;
import ru.argustelecom.box.nri.building.BuildingElementDto;
import ru.argustelecom.box.nri.building.BuildingElementTypeDto;
import ru.argustelecom.box.nri.coverage.ResourceInstallationAppService;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by b.bazarov on 03.04.2018.
 */
@RunWith(PowerMockRunner.class)
public class ResourceDetailInfoFrameModelTest {

	@Mock
	private ResourceInstallationAppService resourceInstallationAppService;

	@Mock
	private BuildingElementAppService buildingElementAppService;

	@InjectMocks
	private ResourceDetailInfoFrameModel testingClass;

	@Test
	public void shouldNotExecFindElementByLocation(){
		testingClass.preRender(null);
		verify(buildingElementAppService, times(0)).findElementByLocation(any());
	}
	@Test
	public void shouldNotExecFindAllByBuilding(){
		when(buildingElementAppService.findElementByLocation(any())).thenReturn(null);
		testingClass.preRender(null);
		verify(resourceInstallationAppService, times(0)).findAllByBuilding(any());
	}
	@Test
	public void shouldReturn34(){
		Location location = new Location(1L);
		Building building = new Building(1L);
		List<BuildingElementDto> list = new ArrayList<>();
		list.add(BuildingElementDto.builder().id(1L).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.LODGING)).build()).build());
		list.add(BuildingElementDto.builder().id(2L).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.LODGING)).build()).build());


		List<BuildingElementDto> list1 = new ArrayList<>();
		list1.add(BuildingElementDto.builder().id(3L).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.LODGING)).build()).build());
		list1.add(BuildingElementDto.builder().id(4L).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.LODGING)).build()).build());

		list.add(BuildingElementDto.builder().id(5L).childElements(list1).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.BUILDING)).build()).build());

		BuildingElementDto buildingElementDto = BuildingElementDto.builder().id(6L).childElements(list).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.BUILDING)).build()).build();

		List<BuildingElementDto> covered = new ArrayList<>();
		covered.add(list.get(0));
		covered.add(list.get(2));

		when(buildingElementAppService.findElementByLocation(eq(location))).thenReturn(buildingElementDto);
		when(resourceInstallationAppService.findAllByBuilding(eq(building))).thenReturn(Collections.singletonList(ResourceInstallationDto.builder()
				.installedAt(buildingElementDto).cover(covered).id(1L).build()));
		testingClass.preRender(building);
		assertEquals(testingClass.getCurrentBuildingElement(),buildingElementDto);
		assertEquals(testingClass.getAll(),4);
		assertEquals(testingClass.getCovered(),3);
	}
	@Test
	public void shouldReturn34WithoutCopy(){
		Location location = new Location(1L);
		Building building = new Building(1L);
		List<BuildingElementDto> list = new ArrayList<>();
		list.add(BuildingElementDto.builder().id(1L).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.LODGING)).build()).build());
		list.add(BuildingElementDto.builder().id(2L).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.LODGING)).build()).build());


		List<BuildingElementDto> list1 = new ArrayList<>();
		list1.add(BuildingElementDto.builder().id(3L).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.LODGING)).build()).build());
		list1.add(BuildingElementDto.builder().id(4L).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.LODGING)).build()).build());

		list.add(BuildingElementDto.builder().id(5L).childElements(list1).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.BUILDING)).build()).build());

		BuildingElementDto buildingElementDto = BuildingElementDto.builder().id(6L).childElements(list).type(BuildingElementTypeDto.builder()
				.level(new LocationLevel(LocationLevel.BUILDING)).build()).build();

		List<BuildingElementDto> covered = new ArrayList<>();
		covered.add(list.get(0));
		covered.add(list.get(2));
		List<ResourceInstallationDto> installation = new ArrayList<>();
		installation.add(ResourceInstallationDto.builder()
				.installedAt(buildingElementDto).cover(covered).id(1L).build());
		installation.add(ResourceInstallationDto.builder()
				.installedAt(buildingElementDto).cover(list1).id(2L).build());


		when(buildingElementAppService.findElementByLocation(eq(location))).thenReturn(buildingElementDto);
		when(resourceInstallationAppService.findAllByBuilding(eq(building))).thenReturn(installation);
		testingClass.preRender(building);
		assertEquals(testingClass.getCurrentBuildingElement(),buildingElementDto);
		assertEquals(testingClass.getAll(),4);
		assertEquals(testingClass.getCovered(),3);
	}

}