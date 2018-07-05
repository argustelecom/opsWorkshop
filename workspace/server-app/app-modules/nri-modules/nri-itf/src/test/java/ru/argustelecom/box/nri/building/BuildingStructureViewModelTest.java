package ru.argustelecom.box.nri.building;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.box.ContextMocker;
import ru.argustelecom.box.env.address.LocationRepository;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.box.nri.service.ServiceSpecificationRepository;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.validator.ValidatorException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author a.wisniewski
 * @since 12.09.2017
 */
@RunWith(MockitoJUnitRunner.class)
public class BuildingStructureViewModelTest {

	@Mock
	private UnitOfWork unitOfWork;

	@Mock
	private LocationRepository locationRepository;

	@Mock
	private BuildingElementAppService elementService;

	@Mock
	private BuildingStructureViewState viewState;

	@Mock
	private BuildingElementTypeAppService dtoService;

	@Mock
	private ServiceSpecificationRepository specificationRepository;

	@InjectMocks
	private BuildingStructureViewModel model;

	@Before
	public void before() {
		doNothing().when(unitOfWork).makePermaLong();
		when(specificationRepository.findAll()).thenReturn(new ArrayList<>());
		ContextMocker.mockFacesContext();
	}

	@Test(expected = BusinessException.class)
	public void postConstruct_withViewStateWithLocationNotInstanceOfBuilding() throws Exception {
		when(viewState.getLocation()).thenReturn(new Location(1L));
		model.postConstruct();
	}

	@Test(expected = BusinessException.class)
	public void postConstruct_withEmptyViewState() throws Exception {
		when(viewState.getLocation()).thenReturn(null);
		when(viewState.getBuildingElement()).thenReturn(null);
		model.postConstruct();
	}

	@Test
	public void postConstruct_withViewStateLocation() throws Exception {
		Building location = new Building(1L);
		when(viewState.getLocation()).thenReturn(location);

		BuildingElementDto rootDto = BuildingElementDto.builder().id(1L).childElements(newArrayList()).build();
		when(elementService.findElementByLocation(location)).thenReturn(rootDto);

		model.postConstruct();
		assertEquals(rootDto, model.getRootElement());
		assertEquals(rootDto, model.getSelectedNode().getData());
	}

	@Test
	public void postConstruct_withViewStateBuildingElement() throws Exception {
		Building location = new Building(3L);
		BuildingElement root = new BuildingElement(1L, null, location, null, null, null);
		BuildingElement child = new BuildingElement(2L, null, null, null, null, null);
		BuildingElementDto rootDto = BuildingElementDto.builder().id(1L).childElements(newArrayList()).build();
		root.setChildren(singletonList(child));
		child.setParent(root);

		when(viewState.getBuildingElement()).thenReturn(child);
		when(elementService.findElementByLocation(location)).thenReturn(rootDto);

		model.postConstruct();
		assertEquals(rootDto, model.getRootElement());
	}

	@Test
	public void postConstruct_initTree_nullRoot() throws Exception {
		Building location = new Building(1L);
		when(viewState.getLocation()).thenReturn(location);

		when(elementService.findElementByLocation(location)).thenReturn(null);

		model.postConstruct();
		assertNotNull(model.getElementsTree());
		assertTrue(model.getElementsTree().getChildren().isEmpty());
	}

	@Test
	public void postConstruct_initTree_normal() throws Exception {
		Building location = new Building(1L);
		when(viewState.getLocation()).thenReturn(location);

		BuildingElementDto childDto = BuildingElementDto.builder().id(2L).name("кв. 2").childElements(newArrayList()).build();
		BuildingElementDto childDto2 = BuildingElementDto.builder().id(2L).name("кв. 10").childElements(newArrayList()).build();
		BuildingElementDto childDto3 = BuildingElementDto.builder().id(2L).name("кв. 1").childElements(newArrayList()).build();
		BuildingElementDto rootDto = BuildingElementDto.builder().id(1L).childElements(newArrayList(childDto, childDto2, childDto3)).build();
		when(elementService.findElementByLocation(location)).thenReturn(rootDto);

		model.postConstruct();
		assertNotNull(model.getElementsTree());
		assertEquals(rootDto, model.getElementsTree().getChildren().get(0).getData());
		assertEquals(childDto, model.getElementsTree().getChildren().get(0).getChildren().get(0).getData());
		assertEquals("кв. 1", ((BuildingElementDto) model.getElementsTree().getChildren().get(0).getChildren().get(0).getData()).getName());
		assertEquals("кв. 2", ((BuildingElementDto) model.getElementsTree().getChildren().get(0).getChildren().get(1).getData()).getName());
		assertEquals("кв. 10", ((BuildingElementDto) model.getElementsTree().getChildren().get(0).getChildren().get(2).getData()).getName());
	}

	// если мы не передаем buildingElement извне. значит, в дереве должен выбираться/раскрываться рут
	@Test
	public void postConstruct_expandTree_toNullElement() throws Exception {
		Building location = new Building(1L);
		when(viewState.getLocation()).thenReturn(location);

		BuildingElementDto rootDto = BuildingElementDto.builder().id(1L).childElements(newArrayList()).build();
		when(elementService.findElementByLocation(location)).thenReturn(rootDto);

		model.postConstruct();
		assertTrue(model.getElementsTree().getChildren().get(0).isSelected());
		assertTrue(model.getElementsTree().getChildren().get(0).isExpanded());
	}

	// если передан элемент, который нам надо раскрыть, все должно быть раскрыто
	@Test
	public void postConstruct_expandTree_toChildElement() throws Exception {
		Building location = new Building(3L);
		BuildingElement root = new BuildingElement(1L, null, location, null, null, null);
		BuildingElement child = new BuildingElement(2L, null, null, null, null, null);
		BuildingElementDto childDto = BuildingElementDto.builder().id(2L).childElements(newArrayList()).build();
		BuildingElementDto rootDto = BuildingElementDto.builder().id(1L).childElements(newArrayList(childDto)).build();
		root.setChildren(singletonList(child));
		child.setParent(root);

		when(viewState.getBuildingElement()).thenReturn(child);
		when(elementService.findElementByLocation(location)).thenReturn(rootDto);

		model.postConstruct();
		assertTrue(model.getElementsTree().getChildren().get(0).isExpanded());
		assertTrue(model.getElementsTree().getChildren().get(0).getChildren().get(0).isSelected());
		assertTrue(model.getElementsTree().getChildren().get(0).getChildren().get(0).isExpanded());
		assertEquals(childDto, model.getSelectedNode().getData());
	}

	//
	@Test
	public void initDefaultStructire() throws Exception {
		// по адресу нашего строения будут прописаны два адреса помещения

		LocationType room = new LocationType(1L);
		room.setName("Квартира");
		room.setShortName("Кв.");

		Lodging room100address = new Lodging(2L);
		room100address.setType(room);
		room100address.setNumber("100");

		Lodging room200address = new Lodging(4L);
		room200address.setType(room);
		room200address.setNumber("200");

		Building location = new Building(1L);
		when(viewState.getLocation()).thenReturn(location);
		when(locationRepository.findAllLodgingsByBuilding(location)).thenReturn(asList(room100address, room200address));
		when(elementService.createNewElementWithChildren(any(), any()))
				.thenAnswer(invoke -> invoke.getArguments()[0]);

		model.postConstruct();
		model.initDefaultStructure();

		TreeNode buildingNode = model.getElementsTree().getChildren().get(0);
		BuildingElementDto building = (BuildingElementDto) buildingNode.getData();
		assertEquals("Строение", building.getName());
		assertEquals(2, building.getChildElements().size());
		assertEquals(2, buildingNode.getChildren().size());

		// проверим, что создались квартирки
		List<BuildingElementDto> rooms = buildingNode.getChildren().stream()
				.map(children -> (BuildingElementDto) children.getData()).collect(toList());
		assertTrue(rooms.stream().anyMatch(room_ -> "Кв. 100".equals(room_.getName())));
		assertTrue(rooms.stream().anyMatch(room_ -> "Кв. 200".equals(room_.getName())));
	}

	@Test
	public void getFreeLodgings() throws Exception {
		model.getFreeLodgings();
		verify(elementService, times(1)).getFreeLodgings(any(), any());
	}

	@Test
	public void create() throws Exception {
		Building location = new Building(1L);
		when(viewState.getLocation()).thenReturn(location);

		BuildingElementDto childDto = BuildingElementDto.builder().id(2L).childElements(newArrayList()).build();
		BuildingElementDto rootDto = BuildingElementDto.builder().id(1L).childElements(newArrayList(childDto)).build();
		when(elementService.findElementByLocation(location)).thenReturn(rootDto);

		// инициализируем модель
		model.postConstruct();

		// создаем новый элемент в модели
		BuildingElementDto newElement = BuildingElementDto.builder().id(3L).childElements(emptyList()).build();
		model.setNewElement(newElement);
		TreeNode buildingNode = model.getElementsTree().getChildren().get(0);
		model.setSelectedNode(buildingNode);

		when(elementService.createNewElement(any(), any())).thenAnswer(invoke -> invoke.getArguments()[0]);

		// вызываем сохранение элемента в базу
		model.create();

		verify(elementService, times(1)).createNewElement(any(), any());
		assertEquals(2, buildingNode.getChildren().size());
		BuildingElementDto building = (BuildingElementDto) buildingNode.getData();
		assertTrue(building.getChildElements().contains(newElement));
	}

	@Test
	public void delete() throws Exception {
		Building location = new Building(1L);
		when(viewState.getLocation()).thenReturn(location);

		BuildingElementDto child1Dto = BuildingElementDto.builder().id(2L).childElements(newArrayList()).build();
		BuildingElementDto child2Dto = BuildingElementDto.builder().id(3L).childElements(newArrayList()).build();

		BuildingElementDto rootDto = BuildingElementDto.builder().id(1L).childElements(newArrayList(child1Dto, child2Dto)).build();
		when(elementService.findElementByLocation(location)).thenReturn(rootDto);

		// инициализируем модель
		model.postConstruct();

		TreeNode buildingNode = model.getElementsTree().getChildren().get(0);
		BuildingElementDto building = (BuildingElementDto) buildingNode.getData();
		model.setSelectedNode(buildingNode.getChildren().get(0));

		model.delete();

		verify(elementService, times(1)).delete(any());
		assertEquals(1, buildingNode.getChildren().size());
		assertEquals(1, building.getChildElements().size());
		assertEquals(buildingNode, model.getSelectedNode());
		assertTrue(model.getSelectedNode().isSelected());
	}

	@Test
	public void delete_shouldFailIfElementContainsResourceInstallation() throws Exception {
		Building location = new Building(1L);
		when(viewState.getLocation()).thenReturn(location);

		BuildingElementDto child1 = BuildingElementDto.builder().id(2L).childElements(newArrayList()).build();
		BuildingElementDto child2 = BuildingElementDto.builder().id(3L).childElements(newArrayList()).build();

		BuildingElementDto root = BuildingElementDto.builder().id(1L).childElements(newArrayList(child1, child2)).build();
		when(elementService.findElementByLocation(location)).thenReturn(root);

		// инициализируем модель
		model.postConstruct();

		TreeNode buildingNode = model.getElementsTree().getChildren().get(0);
		BuildingElementDto building = (BuildingElementDto) buildingNode.getData();
		model.setSelectedNode(buildingNode.getChildren().get(0));

		// внутри элемента будет найдена точка монтирования
		when(elementService.getResourceInstallations(any())).thenReturn(singletonList(ResourceInstallationDto.builder().build()));

		model.delete();

		// удаление не должно пройти. дети должны остаться.
		assertEquals(2, buildingNode.getChildren().size());
		assertEquals(2, building.getChildElements().size());
	}

	@Test
	public void delete_rootBuilding() throws Exception {
		Building location = new Building(1L);
		when(viewState.getLocation()).thenReturn(location);

		BuildingElementDto root = BuildingElementDto.builder().id(1L).childElements(newArrayList()).build();
		when(elementService.findElementByLocation(location)).thenReturn(root);

		// инициализируем модель
		model.postConstruct();

		TreeNode buildingNode = model.getElementsTree().getChildren().get(0);
		model.setSelectedNode(buildingNode);

		// внутри элемента будет найдена точка монтирования
		when(elementService.getResourceInstallations(any())).thenReturn(emptyList());

		model.delete();

		// удаление должно нормально пройти
		verify(elementService, times(1)).delete(any());
		assertEquals(0, model.getElementsTree().getChildCount());
		assertNull(model.getSelectedNode());
		assertNull(model.getRootElement());
	}

	// null должен игнорироваться
	@Test
	public void nameValidator_null() throws Exception {
		model.nameValidator(null, null, null);
	}

	@Test(expected = ValidatorException.class)
	public void nameValidator_empty() throws Exception {
		model.nameValidator(null, null, "");
	}

	@Test
	public void nameValidator_normal() throws Exception {
		model.nameValidator(null, null, "ValidName");
	}

	@Test
	public void clearCreationParams() throws Exception {
		model.setNewElement(BuildingElementDto.builder().name("element").build());
		model.setTypeForAll(BuildingElementTypeDto.builder().name("elmeentType").build());

		model.cleanCreationParams();

		assertNull(model.getNewElement().getName());
		assertNull(model.getTypeForAll().getName());
	}

	@Test
	public void isAddressVisible_true() throws Exception {
		LocationLevel locationLevel = new LocationLevel(1L);
		BuildingElementTypeDto type = BuildingElementTypeDto.builder().level(locationLevel).build();
		BuildingElementDto newElement = BuildingElementDto.builder().type(type).build();
		model.setNewElement(newElement);
		assertTrue(model.isAddressVisible());
	}

	// не отрисовываем адрес, если у него нет locationLevel'a
	@Test
	public void isAddressVisible_false() throws Exception {
		BuildingElementTypeDto type = BuildingElementTypeDto.builder().build();
		BuildingElementDto newElement = BuildingElementDto.builder().type(type).build();
		model.setNewElement(newElement);
		assertFalse(model.isAddressVisible());
	}

	// когда перетаскиваем ноду в дереве, она должна исчезнуть в одном месте и появиться в другом
	@Test
	public void onNodeParentChange() throws Exception {
		//// инициализируем дерево
		Building location = new Building(1L);
		when(viewState.getLocation()).thenReturn(location);

		BuildingElementDto apt = BuildingElementDto.builder().name("квартира").childElements(newArrayList()).build();
		BuildingElementDto room = BuildingElementDto.builder().name("комната").childElements(newArrayList()).build();

		BuildingElementDto rootDto = BuildingElementDto.builder().id(1L).childElements(newArrayList(apt, room)).build();
		when(elementService.findElementByLocation(location)).thenReturn(rootDto);

		// инициализируем модель
		model.postConstruct();

		TreeNode buildingNode = model.getElementsTree().getChildren().get(0);
		TreeNode aptNode = buildingNode.getChildren().stream().filter(node -> apt.equals(node.getData())).findFirst().orElse(null);
		TreeNode roomNode = buildingNode.getChildren().stream().filter(node -> room.equals(node.getData())).findFirst().orElse(null);


		// перетащим комнату в квартиру
		UIComponent component = mock(UIComponent.class);
		Behavior behaviour = mock(Behavior.class);
		TreeDragDropEvent event = new TreeDragDropEvent(component, behaviour, roomNode, aptNode, 1);
		model.onNodeParentChange(event);

		verify(elementService, times(1)).changeElementParent(any(), any());
	}

	@Test
	public void onNodeParentChange_reinitWhenException() throws Exception {
		//// инициализируем дерево
		Building location = new Building(1L);
		when(viewState.getLocation()).thenReturn(location);

		BuildingElementDto apt = BuildingElementDto.builder().name("квартира").childElements(newArrayList()).build();
		BuildingElementDto room = BuildingElementDto.builder().name("комната").childElements(newArrayList()).build();

		BuildingElementDto rootDto = BuildingElementDto.builder().id(1L).childElements(newArrayList(apt, room)).build();
		when(elementService.findElementByLocation(location)).thenReturn(rootDto);

		// инициализируем модель
		model.postConstruct();

		TreeNode buildingNode = model.getElementsTree().getChildren().get(0);
		TreeNode aptNode = buildingNode.getChildren().stream().filter(node -> apt.equals(node.getData())).findFirst().orElse(null);
		TreeNode roomNode = buildingNode.getChildren().stream().filter(node -> room.equals(node.getData())).findFirst().orElse(null);

		doThrow(new RuntimeException("ошибка из сервиса")).when(elementService).changeElementParent(any(), any());

		// перетащим комнату в квартиру
		UIComponent component = mock(UIComponent.class);
		Behavior behaviour = mock(Behavior.class);
		TreeDragDropEvent event = new TreeDragDropEvent(component, behaviour, roomNode, aptNode, 1);
		model.onNodeParentChange(event);

		verify(elementService, times(1)).changeElementParent(any(), any());
	}

	@Test
	public void getTypes() throws Exception {
		model.getTypes();
		verify(dtoService, times(1)).findAllElementTypes();
	}

	@Test
	public void getBuildingTypes() throws Exception {
		LocationLevel levelBuilding = new LocationLevel(5L);
		LocationLevel levelLodging = new LocationLevel(6L);
		List<BuildingElementTypeDto> locations = newArrayList(
				BuildingElementTypeDto.builder().id(1L).level(levelBuilding).build(),
				BuildingElementTypeDto.builder().id(2L).level(levelLodging).build(),
				BuildingElementTypeDto.builder().id(3L).level(levelLodging).build(),
				BuildingElementTypeDto.builder().id(4L).level(levelLodging).build());
		when(dtoService.findAllElementTypes()).thenReturn(locations);

		assertEquals(1, model.getBuildingTypes().size());
	}

	@Test
	public void getLodgingTypes() throws Exception {
		LocationLevel levelBuilding = new LocationLevel(5L);
		LocationLevel levelLodging = new LocationLevel(6L);
		List<BuildingElementTypeDto> locations = newArrayList(
				BuildingElementTypeDto.builder().id(1L).level(levelBuilding).build(),
				BuildingElementTypeDto.builder().id(2L).level(levelLodging).build(),
				BuildingElementTypeDto.builder().id(3L).level(levelLodging).build(),
				BuildingElementTypeDto.builder().id(4L).level(levelLodging).build());
		when(dtoService.findAllElementTypes()).thenReturn(locations);

		assertEquals(3, model.getLodgingTypes().size());
	}

	@Test
	public void onNodeExpand() throws Exception {
		UIComponent component = mock(UIComponent.class);
		Behavior behavior = mock(Behavior.class);
		TreeNode node = new DefaultTreeNode();
		node.setExpanded(false);
		NodeExpandEvent expandEvent = new NodeExpandEvent(component, behavior, node);
		model.onNodeExpand(expandEvent);
		assertTrue(node.isExpanded());

	}

	@Test
	public void onNodeCollapse() throws Exception {
		UIComponent component = mock(UIComponent.class);
		Behavior behavior = mock(Behavior.class);
		TreeNode node = new DefaultTreeNode();
		node.setExpanded(true);
		NodeCollapseEvent collapseEvent = new NodeCollapseEvent(component, behavior, node);
		model.onNodeCollapse(collapseEvent);
		assertFalse(node.isExpanded());
	}

	@Test
	public void onNodeSelect() throws Exception {
		UIComponent component = mock(UIComponent.class);
		Behavior behavior = mock(Behavior.class);
		TreeNode node = new DefaultTreeNode();
		node.setSelected(false);
		NodeSelectEvent selectEvent = new NodeSelectEvent(component, behavior, node);
		model.onNodeSelect(selectEvent);
		assertEquals(node, model.getSelectedNode());
	}
}