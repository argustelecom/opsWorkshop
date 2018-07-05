package ru.argustelecom.box.nri.building;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.building.model.BuildingElementType;
import ru.argustelecom.box.nri.coverage.ResourceInstallationAppService;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDtoTranslator;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class ResourceInstallationViewModelTest {


	@Mock
	private UnitOfWork unitOfWork;


	@Mock
	private ResourceInstallationAppService installationService;

	@Mock
	private BuildingElementAppService buildingService;

	@Mock
	private ResourceInstallationViewState viewState;

	@Spy
	private BuildingElementTypeDtoTranslator typeTranslator = new BuildingElementTypeDtoTranslator();
	@Spy
	@InjectMocks
	private BuildingElementDtoTranslator elementDtoTranslator = new BuildingElementDtoTranslator();
	@Spy
	@InjectMocks
	private ResourceInstallationDtoTranslator installationDtoTranslator = new ResourceInstallationDtoTranslator();

	@InjectMocks
	ResourceInstallationViewModel model;

	@Test
	public void postConstruct_withNullInstalledAt() throws Exception {
		when(viewState.getInstallation()).thenReturn(ResourceInstallation.builder().id(1L).build());
		model.postConstruct();
		assertNotNull(model.getInstallation());
		assertNull(model.getBuilding());
	}
	@Test
	public void postConstruct() throws Exception {
		ResourceInstallation installation = ResourceInstallation.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElement.builder().id(3L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()))
				.installedAt(BuildingElement.builder().id(2L).name("name").type(BuildingElementType.builder().id(1L)
						.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name").build()).build()).build();
		when(viewState.getInstallation()).thenReturn(installation);
		when(buildingService.findBuildingByResInstallation(any())).thenReturn(BuildingElementDto.builder().build());
		model.postConstruct();
		assertNotNull(model.getInstallation());
		assertNotNull(model.getBuilding());
	}

	@Test(expected = BusinessException.class)
	public void postConstruct_withNullInstallation() throws Exception {
		when(viewState.getInstallation()).thenReturn(null);
		model.postConstruct();

	}
	@Test
	public void changedComment() throws Exception {
		ResourceInstallationDto installation = ResourceInstallationDto.builder().comment("1212").build();
		model.setInstallation(installation);
		model.changedComment();
		verify(installationService,times(1)).updateInstallationComment(eq(installation),eq(installation.getComment()));
	}
}