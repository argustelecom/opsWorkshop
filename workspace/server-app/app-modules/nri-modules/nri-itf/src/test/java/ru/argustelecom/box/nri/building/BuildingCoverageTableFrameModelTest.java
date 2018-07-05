package ru.argustelecom.box.nri.building;

import com.google.common.collect.Lists;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.argustelecom.box.ContextMocker;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.building.model.BuildingElementType;
import ru.argustelecom.box.nri.coverage.ResourceInstallationAppService;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class BuildingCoverageTableFrameModelTest {


	@Mock
	private BuildingElementAppService buildingService;

	@Mock
	private ResourceInstallationAppService installationService;

	@Spy
	BuildingElementTypeDtoTranslator typeTranslator = new BuildingElementTypeDtoTranslator();

	@Spy
	@InjectMocks
	private BuildingElementDtoTranslator elementDtoTranslator = new BuildingElementDtoTranslator();

	@InjectMocks
	private BuildingCoverageTableFrameModel model;

	@Test
	public void preRender() throws Exception {
		when(buildingService.findBuildingByResInstallation(any())).thenReturn(
				BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build())
						.childElements(Lists.newArrayList(BuildingElementDto.builder().id(2L).name("кв 1").type(BuildingElementTypeDto.builder().id(1L)
								.level(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build(),
								BuildingElementDto.builder().id(1L).name("кв 2").type(BuildingElementTypeDto.builder().id(1L)
										.level(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()
						))
						.build()
				);
		ResourceInstallationDto installation = ResourceInstallationDto.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build()).build()))
				.installedAt(BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build()).build()).build();
		model.preRender(installation);
		List<List<BuildingCoverageTableFrameModel.CoverageNodeDescriptor>> list = model.getBuildingTable();

		assertNotNull(list);
		assertEquals(2, list.size());
		assertEquals(2, list.get(0).size());
		assertEquals(2, list.get(0).get(0).getRowspan());
	}

	@Test(expected = BusinessExceptionWithoutRollback.class)
	public void shouldFailInstallationChanged() throws Exception {
		FacesContext context = ContextMocker.mockFacesContext();
		ExternalContext ext = mock(ExternalContext.class);
		HashMap<String,String> map = new HashMap<>();
		map.put("elementId",null);
		when(ext.getRequestParameterMap()).thenReturn(map);
		when(context.getExternalContext()).thenReturn(ext);
		model.installationChanged();
	}

	@Test
	public void shouldInstallationChanged() throws Exception {
		when(buildingService.findBuildingByResInstallation(any())).thenReturn(
				BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build())
						.childElements(Lists.newArrayList(BuildingElementDto.builder().id(2L).name("кв 1").type(BuildingElementTypeDto.builder().id(1L)
										.level(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build(),
								BuildingElementDto.builder().id(1L).name("кв 2").type(BuildingElementTypeDto.builder().id(1L)
										.level(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()
						))
						.build()
		);
		ResourceInstallationDto installation = ResourceInstallationDto.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build()).build()))
				.installedAt(BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build()).build()).build();
		model.preRender(installation);

		FacesContext context = ContextMocker.mockFacesContext();
		ExternalContext ext = mock(ExternalContext.class);
		HashMap<String,String> map = new HashMap<>();
		map.put("elementId","2L");
		when(ext.getRequestParameterMap()).thenReturn(map);
		when(context.getExternalContext()).thenReturn(ext);


		when(buildingService.findElementById(any())).thenReturn(BuildingElement.builder().id(2L).name("кв 1").type(BuildingElementType.builder().id(1L)
				.locationLevel(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build());

		model.installationChanged();
		BaseMatcher<FacesMessage> matcher = new BaseMatcher<FacesMessage>() {
			@Override
			public void describeTo(Description description) {
				// пустота
			}
			@Override
			public boolean matches(Object item) {
				FacesMessage actual = (FacesMessage) item;
				return actual.getSummary().equals("Изменение") ;
			}
		};
		verify(context).addMessage(eq(null),argThat(matcher));
	}

	@Test(expected = BusinessException.class)
	public void shouldNotOnElementClicked() throws Exception {
		when(buildingService.findBuildingByResInstallation(any())).thenReturn(
				BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build())
						.childElements(Lists.newArrayList(BuildingElementDto.builder().id(2L).name("кв 1").type(BuildingElementTypeDto.builder().id(1L)
										.level(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build(),
								BuildingElementDto.builder().id(1L).name("кв 2").type(BuildingElementTypeDto.builder().id(1L)
										.level(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()
						))
						.build()
		);
		ResourceInstallationDto installation = ResourceInstallationDto.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build()).build()))
				.installedAt(BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build()).build()).build();
		model.preRender(installation);
		FacesContext context = ContextMocker.mockFacesContext();
		ExternalContext ext = mock(ExternalContext.class);
		HashMap<String,String> map = new HashMap<>();
		map.put("elementId","111");
		when(ext.getRequestParameterMap()).thenReturn(map);
		when(context.getExternalContext()).thenReturn(ext);
		model.onElementClicked();
	}
	@Test
	public void shouldOnElementClickedCanNotChangChildCoverage() throws Exception {
		when(buildingService.findBuildingByResInstallation(any())).thenReturn(
				BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build())
						.childElements(Lists.newArrayList(BuildingElementDto.builder().id(2L).name("кв 1").type(BuildingElementTypeDto.builder().id(1L)
										.level(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build(),
								BuildingElementDto.builder().id(1L).name("кв 2").type(BuildingElementTypeDto.builder().id(1L)
										.level(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()
						))
						.build()
		);
		ResourceInstallationDto installation = ResourceInstallationDto.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build()).build()))
				.installedAt(BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build()).build()).build();
		model.preRender(installation);
		FacesContext context = ContextMocker.mockFacesContext();
		ExternalContext ext = mock(ExternalContext.class);
		HashMap<String,String> map = new HashMap<>();
		map.put("elementId","1");
		when(ext.getRequestParameterMap()).thenReturn(map);
		when(context.getExternalContext()).thenReturn(ext);
		model.onElementClicked();
		BuildingCoverageTableFrameModel.CoverageNodeDescriptor node = model.getBuildingTree();
		assertNotNull(node);
		assertTrue(node.isCovered());
		assertNotNull(node.getChildren());
		assertEquals(2,node.getChildren().size());

		assertTrue(node.getChildren().get(0).isCovered());
		assertTrue(node.getChildren().get(1).isCovered());

	}
	@Test
	public void shouldOnElementClickedChangeParentCoverage() throws Exception {
		when(buildingService.findBuildingByResInstallation(any())).thenReturn(
				BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build())
						.childElements(Lists.newArrayList(BuildingElementDto.builder().id(2L).name("кв 1").type(BuildingElementTypeDto.builder().id(1L)
										.level(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build(),
								BuildingElementDto.builder().id(1L).name("кв 2").type(BuildingElementTypeDto.builder().id(1L)
										.level(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()
						))
						.build()
		);
		ResourceInstallationDto installation = ResourceInstallationDto.builder().comment("comment").id(1L)
				.cover(Lists.newArrayList(BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build()).build()))
				.installedAt(BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build()).build()).build();
		model.preRender(installation);
		FacesContext context = ContextMocker.mockFacesContext();
		ExternalContext ext = mock(ExternalContext.class);
		HashMap<String,String> map = new HashMap<>();
		map.put("elementId","4");
		when(ext.getRequestParameterMap()).thenReturn(map);
		when(context.getExternalContext()).thenReturn(ext);
		model.onElementClicked();
		BuildingCoverageTableFrameModel.CoverageNodeDescriptor node = model.getBuildingTree();
		assertNotNull(node);
		assertFalse(node.isCovered());
		assertNotNull(node.getChildren());
		assertEquals(2,node.getChildren().size());

		assertFalse(node.getChildren().get(0).isCovered());
		assertFalse(node.getChildren().get(1).isCovered());

	}

	@Test
	public void shouldOnElementClickedChangeChildCoverage() throws Exception {
		when(buildingService.findBuildingByResInstallation(any())).thenReturn(
				BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build())
						.childElements(Lists.newArrayList(
								BuildingElementDto.builder().id(1L).name("кв 2").type(BuildingElementTypeDto.builder().id(1L)
										.level(new LocationLevel(LocationLevel.LODGING)).name("name1").build()).build()
						))
						.build()
		);
		ResourceInstallationDto installation = ResourceInstallationDto.builder().comment("comment").id(1L)
				.installedAt(BuildingElementDto.builder().id(4L).name("Здание").type(BuildingElementTypeDto.builder().id(1L)
						.level(new LocationLevel(LocationLevel.BUILDING)).name("Здание").build()).build()).build();
		model.preRender(installation);
		FacesContext context = ContextMocker.mockFacesContext();
		ExternalContext ext = mock(ExternalContext.class);
		HashMap<String,String> map = new HashMap<>();
		map.put("elementId","1");
		when(ext.getRequestParameterMap()).thenReturn(map);
		when(context.getExternalContext()).thenReturn(ext);
		model.onElementClicked();
		BuildingCoverageTableFrameModel.CoverageNodeDescriptor node = model.getBuildingTree();
		assertNotNull(node);
		assertFalse(node.isCovered());
		assertNotNull(node.getChildren());
		assertEquals(1,node.getChildren().size());

		assertTrue(node.getChildren().get(0).isCovered());

	}


}