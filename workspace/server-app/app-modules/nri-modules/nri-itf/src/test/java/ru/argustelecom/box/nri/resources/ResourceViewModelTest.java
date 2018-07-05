package ru.argustelecom.box.nri.resources;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.box.ContextMocker;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceAppService;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDtoTranslator;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType.PHONE_NUMBER;

@RunWith(PowerMockRunner.class)
public class ResourceViewModelTest {

	@Mock
	private UnitOfWork unitOfWork;

	@Mock
	private ResourceViewState viewState;

	@Mock
	private ResourceInstanceDtoTranslator resourceTranslator;

	@Mock
	private ResourceInstanceAppService service;

	@InjectMocks
	private ResourceViewModel model;

	@Before
	public void setUp() throws Exception {
		doNothing().when(unitOfWork).makePermaLong();
		ContextMocker.mockFacesContext();
		when(resourceTranslator.translate(dlink)).thenReturn(translatedDlink);
	}

	private static final ResourceInstance port1 = ResourceInstance.builder()
			.id(17L).name("Порт").build();
	private static final ResourceInstanceDto translatedPort = ResourceInstanceDto.builder()
			.id(17L).name("Порт").build();
	private static final ResourceInstance dlink = ResourceInstance.builder()
			.id(13L).name("D-Link").children(newArrayList(port1)).build();
	private static final ResourceInstanceDto translatedDlink = ResourceInstanceDto.builder()
			.id(13L).name("D-Link").children(newArrayList(translatedPort)).build();

	@Test
	public void postConstruct() {
		when(viewState.getResource()).thenReturn(dlink);
		model.postConstruct();
		assertEquals(translatedDlink, model.getSelectedResource());
		assertEquals(translatedDlink, model.getCurrentResource());
		TreeNode rootResNode = model.getElementsTree().getChildren().get(0);
		assertEquals(translatedDlink, rootResNode.getData());
		assertEquals(translatedPort, rootResNode.getChildren().get(0).getData());

	}

	@Test
	public void initNullResource() {
		when(viewState.getResource()).thenReturn(null);
		model.init();
		assertNull(model.getCurrentResource());
		assertNull(model.getSelectedResource());
		assertNull(model.getSelectedNode());
		assertNotNull(model.getElementsTree());
		assertEquals("рут структуры",model.getElementsTree().getData());
		assertEquals(0,model.getElementsTree().getChildren().size());
	}

	@Test
	public void onNodeSelect() {
		NodeSelectEvent event = new NodeSelectEvent(
				mock(UIComponent.class),
				mock(Behavior.class),
				new DefaultTreeNode(translatedDlink,null));
		model.onNodeSelect(event);
		assertEquals(translatedDlink, model.getSelectedResource());
	}

	@Test
	public void cleanCreationParams() {
		model.setNewElemSpecificationId(1L);
		assertNotNull(model.getNewElemSpecificationId());
		model.cleanCreationParams();
		assertNull(model.getNewElemSpecificationId());
	}

	@Test
	public void removeSelectedResource() {
		doNothing().when(service).removeResource(any());
		when(viewState.getResource()).thenReturn(dlink).thenReturn(null);
		model.postConstruct();
		model.setSelectedNode(model.getSelectedNode().getChildren().get(0));

		model.removeSelectedResource();
		assertEquals(translatedDlink, model.getCurrentResource());
		assertEquals(translatedDlink, model.getSelectedResource());
		assertEquals(translatedDlink, model.getSelectedNode().getData());
		assertNotNull(model.getElementsTree());
		assertEquals("рут структуры",model.getElementsTree().getData());
		assertEquals(0, model.getElementsTree().getChildren().get(0).getChildren().size());
	}

	@Test
	public void shouldDoNothing_removeSelectedResource() {
		when(viewState.getResource()).thenReturn(dlink);
		model.postConstruct();

		UIComponent component = mock(UIComponent.class);
		Behavior behaviour = mock(Behavior.class);
		NodeSelectEvent event = new NodeSelectEvent(component,behaviour,new DefaultTreeNode(null,null));
		model.onNodeSelect(event);

		model.removeSelectedResource();
		//Проверим что ничего не удалилилось
		verify(service,times(0)).removeResource(any());
	}

	@Test
	public void isCanRemoveResourceTrueNotParent() {
		when(viewState.getResource()).thenReturn(dlink).thenReturn(null);
		model.postConstruct();
		model.setSelectedNode(model.getElementsTree().getChildren().get(0).getChildren().get(0));
		assertTrue(model.isRemovableResource());
	}

	@Test
	public void isCanRemoveResourceFalseParent() {
		when(viewState.getResource()).thenReturn(dlink).thenReturn(null);
		model.postConstruct();

		model.setSelectedNode(model.getElementsTree().getChildren().get(0));
		assertFalse(model.isRemovableResource());
	}

	@Test
	public void isCanRemoveResourceFalseRoot() {
		when(viewState.getResource()).thenReturn(dlink).thenReturn(null);
		model.postConstruct();

		model.setSelectedNode(model.getElementsTree());
		assertFalse(model.isRemovableResource());
	}

	@Test
	public void isCanRemoveResourceFalseNullSelectedNode() {
		when(viewState.getResource()).thenReturn(dlink).thenReturn(null);
		model.postConstruct();

		model.setSelectedNode(null);
		assertFalse(model.isRemovableResource());
	}

	@Test
	public void isCanRemoveResourceNullData() {
		when(viewState.getResource()).thenReturn(dlink).thenReturn(null);
		model.postConstruct();

		model.setSelectedNode(new DefaultTreeNode(null,null));
		assertFalse(model.isRemovableResource());
	}

	@Test
	public void resourceCanContainPhoneNumers() {
		ResourceSpecificationDto resSpec = ResourceSpecificationDto.builder()
				.supportedLogicalResources(ImmutableSet.of(PHONE_NUMBER)).build();
		ResourceInstanceDto res = ResourceInstanceDto.builder().specification(resSpec).build();
		model.setSelectedResource(res);
		model.init();
		assertTrue(model.resourceCanContainLogicalResources());
	}
}