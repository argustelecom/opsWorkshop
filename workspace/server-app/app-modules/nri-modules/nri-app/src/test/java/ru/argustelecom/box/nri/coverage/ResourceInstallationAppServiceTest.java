package ru.argustelecom.box.nri.coverage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.nri.building.BuildingElementDto;
import ru.argustelecom.box.nri.building.BuildingElementRepository;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.box.nri.resources.ResourceInstanceRepository;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author d.khekk
 * @since 01.09.2017
 */
@RunWith(PowerMockRunner.class)
public class ResourceInstallationAppServiceTest {

	@Mock
	private ResourceInstallationDtoTranslator translator;

	@Mock
	private ResourceInstallationRepository repository;

	@Mock
	private BuildingElementRepository buildingElementRepository;

	@Mock
	private ResourceInstanceRepository resourceInstanceRepository;

	@InjectMocks
	private ResourceInstallationAppService service;

	@Before
	public void setUp() throws Exception {
		when(translator.translate(any(ResourceInstallation.class))).then(invocation -> {
			ResourceInstallation resourceInstallation = invocation.getArgumentAt(0, ResourceInstallation.class);
			if (resourceInstallation == null)
				return null;
			return ResourceInstallationDto.builder().id(resourceInstallation.getId()).comment(resourceInstallation.getComment()).build();
		});
	}

	@Test
	public void shouldFindAllInstallationsByRoot() throws Exception {
		ResourceInstallation installation1 = ResourceInstallation.builder().id(1L).build();
		ResourceInstallation installation2 = ResourceInstallation.builder().id(2L).build();
		ResourceInstallation installation3 = ResourceInstallation.builder().id(3L).build();

		ResourceInstallationDto installationDto1 = ResourceInstallationDto.builder().id(1L).build();
		ResourceInstallationDto installationDto2 = ResourceInstallationDto.builder().id(2L).build();
		ResourceInstallationDto installationDto3 = ResourceInstallationDto.builder().id(3L).build();

		when(buildingElementRepository.findElementById(anyLong())).thenReturn(BuildingElement.builder().id(1L).name("name").build());
		when(repository.findAllInstallationsByBuilding(any(BuildingElement.class))).thenReturn(Arrays.asList(installation3, installation2, installation1));

		List<ResourceInstallationDto> installedAtBuildingElement = service.findAllByParentBuildingElement(1L);
		assertNotNull(installedAtBuildingElement);
		assertFalse(installedAtBuildingElement.isEmpty());

		assertEquals(Arrays.asList(installationDto1, installationDto2, installationDto3), installedAtBuildingElement);
	}

	@Test
	public void shouldFindAllInstallationsByBuildingElement() throws Exception {
		ResourceInstallation installation1 = ResourceInstallation.builder().id(1L).build();
		ResourceInstallation installation2 = ResourceInstallation.builder().id(2L).build();
		ResourceInstallation installation3 = ResourceInstallation.builder().id(3L).build();

		ResourceInstallationDto installationDto1 = ResourceInstallationDto.builder().id(1L).build();
		ResourceInstallationDto installationDto2 = ResourceInstallationDto.builder().id(2L).build();
		ResourceInstallationDto installationDto3 = ResourceInstallationDto.builder().id(3L).build();

		BuildingElement child = BuildingElement.builder().id(2L)
				.name("child")
				.parent(BuildingElement.builder().id(1L).name("parent").build())
				.build();

		when(buildingElementRepository.findElementById(anyLong())).thenReturn(child);
		when(repository.findAllByBuildingElement(any(BuildingElement.class))).thenReturn(Arrays.asList(installation3, installation2, installation1));

		List<ResourceInstallationDto> installedAtBuildingElement = service.findAllByBuildingElement(1L);
		assertNotNull(installedAtBuildingElement);
		assertFalse(installedAtBuildingElement.isEmpty());

		assertEquals(Arrays.asList(installationDto1, installationDto2, installationDto3), installedAtBuildingElement);
	}

	@Test
	public void shouldFindCoverByBuildingElement() throws Exception {
		ResourceInstallation installation1 = ResourceInstallation.builder().id(1L).build();
		ResourceInstallation installation2 = ResourceInstallation.builder().id(2L).build();
		ResourceInstallation installation3 = ResourceInstallation.builder().id(3L).build();

		ResourceInstallationDto installationDto1 = ResourceInstallationDto.builder().id(1L).build();
		ResourceInstallationDto installationDto2 = ResourceInstallationDto.builder().id(2L).build();
		ResourceInstallationDto installationDto3 = ResourceInstallationDto.builder().id(3L).build();

		when(buildingElementRepository.findElementById(anyLong())).thenReturn(BuildingElement.builder().id(1L).name("name").build());
		when(repository.findAllCover(any(BuildingElement.class))).thenReturn(Arrays.asList(installation3, installation2, installation1));

		List<ResourceInstallationDto> coverByBuildingElement = service.findAllCoveringBuildingElement(1L);
		assertNotNull(coverByBuildingElement);
		assertFalse(coverByBuildingElement.isEmpty());
		assertEquals(Arrays.asList(installationDto1, installationDto2, installationDto3), coverByBuildingElement);
	}

	@Test
	public void shouldFindCoverByBuildingElementWithParent() throws Exception {
		BuildingElement parent = BuildingElement.builder().id(1L).name("parent").build();
		BuildingElement child = BuildingElement.builder().id(2L).name("child").parent(parent).build();

		when(buildingElementRepository.findElementById(anyLong())).thenReturn(child);
		List<ResourceInstallation> cover = new ArrayList<>();
		cover.add(ResourceInstallation.builder().id(1L).build());
		when(repository.findAllCover(any(BuildingElement.class))).thenReturn(cover);

		List<ResourceInstallationDto> coverByBuildingElement = service.findAllCoveringBuildingElement(1L);
		assertNotNull(coverByBuildingElement);
		assertFalse(coverByBuildingElement.isEmpty());
	}

	@Test
	public void shouldUpdateInstallationPoint() throws Exception {
		ResourceInstallationDto installation = ResourceInstallationDto.builder().id(1L).comment("aaa").build();
		BuildingElementDto buildingElement = BuildingElementDto.builder().id(2L).build();

		BuildingElement element = BuildingElement.builder().id(2L).build();
		ResourceInstallation inst = ResourceInstallation.builder().id(1L).build();

		when(buildingElementRepository.findElementById(eq(buildingElement.getId()))).thenReturn(element);
		when(repository.setInstalledAt(eq(installation.getId()), eq(element))).thenReturn(inst);

		ResourceInstallationDto res = service.updateInstallationPoint(installation, buildingElement);

		assertEquals(installation, res);
		verify(translator, times(1)).translate(eq(inst));
	}

	@Test
	public void shouldUpdateInstallationComment() throws Exception {
		ResourceInstallationDto installation = ResourceInstallationDto.builder().id(1L).comment("aaa").build();

		ResourceInstallation inst = ResourceInstallation.builder().id(1L).build();

		when(repository.updateComment(eq(installation.getId()), eq("1111"))).thenReturn(inst);

		ResourceInstallationDto res = service.updateInstallationComment(installation, "1111");

		assertEquals(installation, res);
		verify(translator, times(1)).translate(eq(inst));
	}

	@Test
	public void shouldUpdateInstallationCoveredElements() throws Exception {
		ResourceInstallationDto installation = ResourceInstallationDto.builder().id(1L).comment("aaa").build();

		ResourceInstallation inst = ResourceInstallation.builder().id(1L).build();

		when(repository.updateCoveredElements(eq(installation.getId()), any())).thenReturn(inst);

		ResourceInstallationDto res = service.updateInstallationCoveredElements(installation);

		assertEquals(installation, res);
		verify(translator, times(1)).translate(eq(inst));
	}

	@Test
	public void shouldFindInstByResource() throws Exception {
		ResourceInstanceDto res = ResourceInstanceDto.builder().id(1L).build();
		ResourceInstallation inst = ResourceInstallation.builder().id(1L).build();

		when(repository.findByResource(eq(res.getId()))).thenReturn(inst);

		Assert.assertNotNull(service.findInstallationByResource(res));

		verify(repository, times(1)).findByResource(anyLong());
	}

	@Test
	public void shouldDeleteInstallation() {

		ResourceInstallationDto inst = ResourceInstallationDto.builder().id(1L).build();
		when(repository.findById(eq(inst.getId()))).thenReturn(ResourceInstallation.builder().id(1L).build());

		service.deleteInstallation(inst);
		verify(repository, times(1)).delete(eq(inst.getId()));
	}

	@Test
	public void shouldCreateInstallation() throws Exception {
		when(buildingElementRepository.findElementById(any())).thenReturn(BuildingElement.builder().build());
		when(resourceInstanceRepository.findOne(any())).thenReturn(ResourceInstance.builder().id(1L).build());
		when(repository.createInstallation(any(), any())).thenReturn(ResourceInstallation.builder().build());

		ResourceInstallationDto installation = service.createInstallation(1L, 1L);

		assertNotNull(installation);

		verify(translator, times(1)).translate(any());
	}

	@Test
	public void shouldCreateNothing() throws Exception {
		when(buildingElementRepository.findElementById(any())).thenReturn(BuildingElement.builder().build());
		when(resourceInstanceRepository.findOne(any())).thenReturn(ResourceInstance.builder().build());

		ResourceInstallationDto installation = service.createInstallation(1L, 1L);

		assertNull(installation);

		verify(translator, times(0)).translate(any());
	}
}