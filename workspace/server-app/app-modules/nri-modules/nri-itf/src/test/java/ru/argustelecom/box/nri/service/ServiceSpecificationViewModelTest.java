package ru.argustelecom.box.nri.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.nri.resources.requirements.RequiredItemAppService;
import ru.argustelecom.box.nri.resources.requirements.RequiredItemDto;
import ru.argustelecom.box.nri.resources.requirements.RequiredParameterValueDto;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaAppService;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationAppService;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.system.inf.modelbase.NamedObject;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Модель страницы карточки ресурса
 * Created by b.bazarov on 11.10.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceSpecificationViewModelTest {

	/**
	 * Сервис для работы со схемами требований к ресурсам
	 */
	@Mock
	private ResourceSchemaAppService serviceResourceSchema;

	/**
	 * Состояние вьюхи
	 */
	@Mock
	private ServiceSpecificationViewState viewState;

	@Mock
	private RequiredItemAppService requiredItemAppService;
	@Mock
	protected UnitOfWork unitOfWork;

	/**
	 * Сервис спецификаций ресурсов
	 */
	@Mock
	private ResourceSpecificationAppService resourceSpecificationService;

	@InjectMocks
	private ServiceSpecificationViewModel model;

	private List<ResourceSchemaDto> schemas;

	private List<ResourceSchemaDto> schemasOneOne;

	@Before
	public void before() {
		doNothing().when(unitOfWork).makePermaLong();

		List<RequiredParameterValueDto> rpvlist = new ArrayList<>();
		rpvlist.add(RequiredParameterValueDto.builder().build());

		List<RequiredItemDto> requirements = new ArrayList<>();
		requirements.add(RequiredItemDto.builder().id(1L).resourceSpecification(ResourceSpecificationDto.builder().id(1L)
				.name("resource1")
				.build()).build());

		requirements.add(RequiredItemDto.builder().id(2L).resourceSpecification(ResourceSpecificationDto.builder().id(1L)
				.name("resource2")
				.build()).requiredParameters(rpvlist).children(

				new ArrayList<>()
		).build());

		requirements.get(1).getChildren().add(RequiredItemDto.builder().id(3L).resourceSpecification(ResourceSpecificationDto.builder().id(2L)
				.name("resource3")
				.build()).build());

		schemas = new ArrayList<>();
		schemas.add(null);
		schemas.add(ResourceSchemaDto.builder().id(1L).name("name 2").build());
		schemas.add(ResourceSchemaDto.builder().id(2L).name("name 1")
				.requirements(requirements).build());

		List<RequiredParameterValueDto> rpvlist1 = new ArrayList<>();
		rpvlist1.add(RequiredParameterValueDto.builder().build());

		List<RequiredItemDto> requirements1 = new ArrayList<>();
		requirements1.add(RequiredItemDto.builder().id(2L).resourceSpecification(ResourceSpecificationDto.builder().id(1L)
				.name("resource2")
				.build()).requiredParameters(rpvlist1).children(

				new ArrayList<>()
		).build());
		requirements1.get(0).getChildren().add(RequiredItemDto.builder().id(3L).resourceSpecification(ResourceSpecificationDto.builder().id(2L)
				.name("resource3")
				.build()).build());


		schemasOneOne = new ArrayList<>();
		schemasOneOne.add(ResourceSchemaDto.builder().id(2L).name("name")
				.requirements(requirements1).build());
	}

	@Test
	public void shouldInitSchemeAndOneResReq() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemas);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(Collections.singletonList(ResourceSpecificationDto.builder()
				.id(1L).name("ResSpec1").build()));

		model.postConstruct();
		assertNotNull(model.getElementsTree());
		assertEquals("рут схем", model.getElementsTree().getData());
		//Проверим что дерво выросло
		assertNotNull(model.getElementsTree().getChildren());
		//2 схемы
		assertEquals(2, model.getElementsTree().getChildren().size());
		//у второй два треб
		assertNotNull(model.getElementsTree().getChildren().get(1).getChildren());
		assertNotNull(model.getElementsTree().getChildren().get(0).getChildren());

		assertEquals("name 1", ((NamedObject) model.getElementsTree().getChildren().get(0).getData()).getObjectName());
		assertEquals("name 2", ((NamedObject) model.getElementsTree().getChildren().get(1).getData()).getObjectName());

		assertEquals(0, model.getElementsTree().getChildren().get(1).getChildren().size());
		assertEquals(2, model.getElementsTree().getChildren().get(0).getChildren().size());

		//И ещё одно
		assertNotNull(model.getElementsTree().getChildren().get(0).getChildren().get(1).getChildren());
		assertEquals(1, model.getElementsTree().getChildren().get(0).getChildren().get(1).getChildren().size());

		assertNotNull(model.getElementsTree().getChildren().get(0).getData());
		assertNotNull(model.getElementsTree().getChildren().get(1).getData());
	}

	@Test
	public void shouldInitNullListOnScheme() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(null);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();
		assertNotNull(model.getElementsTree());
		assertEquals("рут схем", model.getElementsTree().getData());
		assertNotNull(model.getElementsTree().getChildren());
		assertEquals(0, model.getElementsTree().getChildren().size());
	}

	@Test
	public void shouldCreateSchema() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemas);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(Collections.singletonList(ResourceSpecificationDto.builder()
				.id(1L).name("ResSpec1").build()));

		model.postConstruct();

		model.setNewSchemaName("Имя 3");
		when(serviceResourceSchema.createResourceSchema(eq("Имя 3"), any())).thenReturn(
				ResourceSchemaDto.builder().id(1L).name("Имя 3").build()
		);
		model.createSchema();
		assertEquals(3, model.getElementsTree().getChildren().size());
		assertEquals(1, model.getElementsTree().getChildren().stream()
				.filter(element -> ((ResourceSchemaDto) element.getData()).getName().equals("Имя 3")).collect(toList()).size());
		assertNotNull(model.getSelectedNode());
	}

	@Test
	public void shouldNotCreateSchemaNullName() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemas);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(Collections.singletonList(ResourceSpecificationDto.builder()
				.id(1L).name("ResSpec1").build()));

		model.postConstruct();

		model.setNewSchemaName(null);
		when(serviceResourceSchema.createResourceSchema(any(), any())).thenReturn(
				ResourceSchemaDto.builder().id(1L).name("Имя 3").build()
		);
		model.createSchema();
		assertEquals(2, model.getElementsTree().getChildren().size());
	}

	@Test
	public void shouldNotCreateRequirementNullSpecId() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemas);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(Collections.singletonList(ResourceSpecificationDto.builder()
				.id(1L).name("ResSpec1").build()));

		model.postConstruct();
		TreeNode node = model.getElementsTree().getChildren().get(0);
		int nodeChildSize = node.getChildren().size();
		model.setSelectedNode(node);
		model.setNewElemSpecificationId(null);
		model.createRequirement();
		assertEquals(nodeChildSize, node.getChildren().size());
	}

	@Test
	public void shouldNotCreateRequirementNullSelectedNode() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(new ArrayList<>());
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();
		model.setNewSchemaName("Имя 3");
		when(serviceResourceSchema.createResourceSchema(eq("Имя 3"), any())).thenReturn(
				ResourceSchemaDto.builder().id(1L).name("Имя 3").build()
		);
		model.createSchema();

		model.setSelectedNode(null);
		model.setNewElemSpecificationId(1L);
		model.createRequirement();

		assertNotNull(model.getElementsTree().getChildren().get(0));
		assertNotNull(model.getElementsTree().getChildren().get(0).getChildren());
		assertEquals(0, model.getElementsTree().getChildren().get(0).getChildren().size());
	}

	@Test
	public void shouldCreateRequirementFromSchema() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(new ArrayList<>());
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();
		model.setNewSchemaName("Имя 3");
		when(serviceResourceSchema.createResourceSchema(eq("Имя 3"), any())).thenReturn(
				ResourceSchemaDto.builder().id(1L).name("Имя 3").build()
		);
		model.createSchema();

		model.setSelectedNode(model.getElementsTree().getChildren().get(0));
		model.setNewElemSpecificationId(1L);
		when(requiredItemAppService.create(eq(model.getNewElemSpecificationId()), eq((ResourceSchemaDto) model.getElementsTree().getChildren().get(0).getData()))).
				thenReturn(RequiredItemDto.builder().build());
		model.createRequirement();

		assertNotNull(model.getElementsTree().getChildren().get(0));
		assertNotNull(model.getElementsTree().getChildren().get(0).getChildren());
		assertEquals(1, model.getElementsTree().getChildren().get(0).getChildren().size());
	}

	@Test
	public void shouldCreateRequirementFromReq() {


		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();

		model.setSelectedNode(model.getElementsTree().getChildren().get(0).getChildren().get(0));
		model.setNewElemSpecificationId(1L);

		RequiredItemDto newItem = RequiredItemDto.builder().id(4L).resourceSpecification(ResourceSpecificationDto.builder().name("Spec name").build()).build();

		when(requiredItemAppService.create(eq(model.getNewElemSpecificationId()), eq((RequiredItemDto) model.getElementsTree().getChildren().get(0).getChildren().get(0).getData()))).
				thenReturn(newItem);
		model.createRequirement();

		assertNotNull(model.getElementsTree().getChildren().get(0));
		assertNotNull(model.getElementsTree().getChildren().get(0).getChildren());
		assertNotNull(model.getSelectedNode());
		assertTrue(newItem.equals(model.getSelectedNode().getData()));
		assertEquals(1, model.getElementsTree().getChildren().get(0).getChildren().size());
		assertEquals(2, model.getElementsTree().getChildren().get(0).getChildren().get(0).getChildren().size());
	}

	@Test
	public void onNodeSelect() {
		UIComponent component = mock(UIComponent.class);
		Behavior behaviour = mock(Behavior.class);
		NodeSelectEvent event = new NodeSelectEvent(component, behaviour, new DefaultTreeNode(RequiredItemDto.builder().id(1L).build(), null));
		model.onNodeSelect(event);
		assertNotNull(model.getSelectedNode());
	}

	@Test
	public void shouldNotRemoveSelectedItem() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();
		model.setSelectedNode(null);
		model.removeSelectedItem();

		assertNotNull(model.getElementsTree());

		//схема
		assertNotNull(model.getElementsTree().getChildren());
		assertEquals(1, model.getElementsTree().getChildren().size());

		//требование
		assertNotNull(model.getElementsTree().getChildren().get(0).getChildren());
		assertEquals(1, model.getElementsTree().getChildren().get(0).getChildren().size());

		//Дочернее
		assertNotNull(model.getElementsTree().getChildren().get(0).getChildren().get(0));
		assertEquals(1, model.getElementsTree().getChildren().get(0).getChildren().get(0).getChildren().size());

		//Здесь пусто
		assertNotNull(model.getElementsTree().getChildren().get(0).getChildren().get(0).getChildren());
		assertEquals(0, model.getElementsTree().getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size());
	}

	@Test
	public void shouldRemoveSelectedItemRequiredItemDtoParentSchema() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();
		//предпоследнего
		model.setSelectedNode(model.getElementsTree().getChildren().get(0).getChildren().get(0));
		doNothing().when(requiredItemAppService).removeItem(any());
		model.removeSelectedItem();

		//root
		assertNotNull(model.getElementsTree().getChildren());
		assertEquals(1, model.getElementsTree().getChildren().size());

		//схема у неё нет требований
		assertNotNull(model.getElementsTree().getChildren().get(0).getChildren());
		assertEquals(0, model.getElementsTree().getChildren().get(0).getChildren().size());

	}


	@Test
	public void shouldRemoveSelectedItemRequiredItemDto() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();
		//Удаляем последнего
		model.setSelectedNode(model.getElementsTree().getChildren().get(0).getChildren().get(0).getChildren().get(0));
		doNothing().when(requiredItemAppService).removeItem(any());
		model.removeSelectedItem();

		//схема
		assertNotNull(model.getElementsTree().getChildren());
		assertEquals(1, model.getElementsTree().getChildren().size());

		//требование
		assertNotNull(model.getElementsTree().getChildren().get(0).getChildren());
		assertEquals(1, model.getElementsTree().getChildren().get(0).getChildren().size());

		//Дочернее Это мы удалили
		assertNotNull(model.getElementsTree().getChildren().get(0).getChildren().get(0));
		assertEquals(0, model.getElementsTree().getChildren().get(0).getChildren().get(0).getChildren().size());
	}

	@Test
	public void shouldRemoveSelectedItemResourceSchemaDto() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();
		//Удаляем схему
		model.setSelectedNode(model.getElementsTree().getChildren().get(0));
		doNothing().when(serviceResourceSchema).removeResourceSchema(any());
		model.removeSelectedItem();

		//схема удалили остался только корень
		assertNotNull(model.getElementsTree().getChildren());
		assertEquals(0, model.getElementsTree().getChildren().size());
	}


	@Test
	public void getSelectedRequiredItemDtoNull() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();
		//Удаляем схему
		model.setSelectedNode(model.getElementsTree().getChildren().get(0));

		assertNull(model.getSelectedRequiredItemDto());
	}

	@Test
	public void getSelectedRequiredItemDtoNullNullSelected() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();
		//Удаляем схему
		model.setSelectedNode(null);

		assertNull(model.getSelectedRequiredItemDto());
	}

	@Test
	public void getSelectedRequiredItemDtoOk() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();

		model.setSelectedNode(model.getElementsTree().getChildren().get(0).getChildren().get(0).getChildren().get(0));

		RequiredItemDto item = model.getSelectedRequiredItemDto();
		assertNotNull(item);
		assertEquals(new Long(3L), item.getId());
	}

	@Test
	public void getSelectedSchemaDtoOk() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();

		model.setSelectedNode(model.getElementsTree().getChildren().get(0));

		ResourceSchemaDto item = model.getSelectedSchemaDto();
		assertNotNull(item);
		assertEquals(new Long(2L), item.getId());
	}

	@Test
	public void getSelectedSchemaDtoNull() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();
		model.setSelectedNode(model.getElementsTree().getChildren().get(0).getChildren().get(0).getChildren().get(0));

		assertNull(model.getSelectedSchemaDto());
	}

	@Test
	public void getSelectedSchemaNullNullSelected() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();
		model.setSelectedNode(null);

		assertNull(model.getSelectedSchemaDto());
	}

	@Test
	public void shouldNotChangeName() {
		model.changeSchemeName();
		verify(serviceResourceSchema, times(0)).changeName(any());
	}

	@Test
	public void shouldChangeName() {
		when(viewState.getSpecification()).thenReturn(ServiceSpec.builder().id(1L).build());
		when(serviceResourceSchema.findAll(any())).thenReturn(schemasOneOne);
		when(resourceSpecificationService.findAllSpecifications()).thenReturn(null);

		model.postConstruct();

		model.setSelectedNode(model.getElementsTree().getChildren().get(0));

		model.changeSchemeName();
		verify(serviceResourceSchema, times(1)).changeName(eq(model.getSelectedSchemaDto()));
	}

	@Test
	public void shouldReturnPossibleResourceSpec() throws Exception {
		ResourceSpecificationDto childSpecificationDto = ResourceSpecificationDto.builder().build();
		ResourceSpecificationDto specificationDto = ResourceSpecificationDto.builder().childSpecifications(Collections.singletonList(childSpecificationDto)).build();
		RequiredItemDto requiredItemDto = RequiredItemDto.builder().resourceSpecification(specificationDto).build();
		DefaultTreeNode node = new DefaultTreeNode(requiredItemDto);
		model.setPossibleResourceSpecification(Arrays.asList(specificationDto, childSpecificationDto));
		model.setSelectedNode(node);

		List<ResourceSpecificationDto> specs = model.getPossibleResourceSpecification();
		assertFalse(specs.isEmpty());
		assertEquals(1, specs.size());
	}

	@Test
	public void shouldReturnAllResourceSpec() throws Exception {
		ResourceSpecificationDto specificationDto = ResourceSpecificationDto.builder().build();
		DefaultTreeNode node = new DefaultTreeNode(ResourceSchemaDto.builder().build());
		model.setPossibleResourceSpecification(Collections.singletonList(specificationDto));
		model.setSelectedNode(node);

		List<ResourceSpecificationDto> specs = model.getPossibleResourceSpecification();
		assertFalse(specs.isEmpty());
		assertEquals(1, specs.size());
	}

	@Test
	public void shouldReturnEmptyListOfResourceSpec() throws Exception {
		List<ResourceSpecificationDto> specs = model.getPossibleResourceSpecification();
		assertTrue(specs.isEmpty());
	}
	@Test
	public void onNodeExpand(){
		UIComponent component = mock(UIComponent.class);
		Behavior behaviour = mock(Behavior.class);
		TreeNode tn = new DefaultTreeNode(RequiredItemDto.builder().id(1L).build(), null);
		tn.setExpanded(false);
		NodeExpandEvent event = new NodeExpandEvent(component, behaviour, tn);
		model.onNodeExpand(event);
		assertTrue(tn.isExpanded());
	}

	@Test
	public void onNodeCollapse(){
		UIComponent component = mock(UIComponent.class);
		Behavior behaviour = mock(Behavior.class);
		TreeNode tn = new DefaultTreeNode(RequiredItemDto.builder().id(1L).build(), null);
		tn.setExpanded(true);
		NodeCollapseEvent event = new NodeCollapseEvent(component, behaviour, tn);
		model.onNodeCollapse(event);
		assertFalse(tn.isExpanded());
	}

}