package ru.argustelecom.box.nri.logicalresources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.event.NodeSelectEvent;
import ru.argustelecom.box.ContextMocker;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

/**
 * @author d.khekk
 * @since 02.11.2017
 */
@RunWith(PowerMockRunner.class)
public class PhoneNumberPoolViewModelTest {

	@Mock
	private UnitOfWork unitOfWork;

	@Mock
	private PhoneNumberPoolAppService poolService;

	@Mock
	private PhoneNumberAppService phoneNumberService;

	@Mock
	private PhoneNumberPoolViewState viewState;

	@InjectMocks
	private PhoneNumberPoolViewModel model;

	private PhoneNumberPoolDto defaultPool = PhoneNumberPoolDto.builder().id(1L).name("Pool").build();
	private PhoneNumberDto defaultPhoneNumber = PhoneNumberDto.builder().id(1L).name("Phone number").pool(defaultPool).build();
	private UIComponent component = mock(UIComponent.class);
	private Behavior behavior = mock(Behavior.class);

	@Before
	public void setUp() throws Exception {
		ContextMocker.mockFacesContext();
		doNothing().when(unitOfWork).makePermaLong();
		doNothing().when(poolService).remove(defaultPool);
		doNothing().when(phoneNumberService).remove(any(Long.class));

		defaultPool.getPhoneNumbers().add(defaultPhoneNumber);
		when(poolService.findAllLazy()).thenReturn(singletonList(defaultPool));

		when(viewState.getPool()).thenReturn(PhoneNumberPool.builder().id(1L).name("Pool").build());

		model.postConstruct();
		model.getSelectedPool().getPhoneNumbers().add(defaultPhoneNumber);
	}

	@Test
	public void shouldInitTree() throws Exception {
		assertNotNull(model.getRootElement());
		assertNotNull(model.getSelectedNode());
		assertNotNull(model.getSelectedPool());
		assertTrue(model.getRootElement().getChildren().contains(model.getSelectedNode()));
		assertTrue(model.getSelectedNode().isLeaf());
	}

	@Test
	public void shouldSelectPoolNode() throws Exception {
		model.setSelectedNode(model.getRootElement().getChildren().get(0));
		model.setSelectedPool(null);
		model.onNodeSelect(new NodeSelectEvent(component, behavior, model.getSelectedNode()));

		assertNotNull(model.getSelectedPool());
		assertEquals(defaultPool, model.getSelectedPool());
	}

	@Test
	public void shouldSelectNullNode() throws Exception {
		model.setSelectedNode(null);
		model.setSelectedPool(null);
		model.onNodeSelect(new NodeSelectEvent(component, behavior, model.getSelectedNode()));

		assertNull(model.getSelectedPool());
	}

	@Test
	public void shouldNotRemoveAnythingIfSelectedNodeIsNull() throws Exception {
		model.setSelectedNode(null);
		model.removePool();

		verify(poolService, never()).remove(any());
		verify(phoneNumberService, never()).remove(any(Long.class));
	}

	@Test
	public void shouldRemovePoolNode() throws Exception {
		model.removePool();

		assertNull(model.getSelectedNode());
		assertNull(model.getSelectedPool());
	}

	@Test
	public void shouldNotRemovePoolNode() throws Exception {
		doThrow(new IllegalArgumentException()).when(poolService).remove(defaultPool);
		model.removePool();

		assertNotNull(model.getSelectedNode());
		assertNotNull(model.getSelectedPool());
		assertEquals(defaultPool, model.getSelectedPool());
	}

	@Test
	public void shouldCreateNewPool() throws Exception {
		PhoneNumberPoolDto testPool = PhoneNumberPoolDto.builder().build();
		when(poolService.createPool(any())).thenReturn(testPool);
		when(poolService.findAllLazy()).thenReturn(singletonList(defaultPool));
		model.postConstruct();

		model.getCreatePool().execute(testPool);
		verify(poolService, atLeastOnce()).createPool(any());
	}

	@Test
	public void shouldCreateNewPhoneNumberWithSelectedPool() throws Exception {
		PhoneNumberDto testPhoneNumber = PhoneNumberDto.builder().build();
		when(phoneNumberService.createPhoneNumber(any(), any())).thenReturn(testPhoneNumber);

		model.getCreatePhoneNumber().accept(testPhoneNumber, new PhoneNumberSpecification(1L));
		verify(phoneNumberService, atLeastOnce()).createPhoneNumber(any(), any());
	}

	@Test
	public void shouldRefreshPoolInTree() throws Exception {
		model.getRefreshPoolInTree().execute(model.getSelectedPool());
	}

	@Test
	public void shouldRemovePhoneFromPool() throws Exception {
		assertTrue(model.getSelectedPool().getPhoneNumbers().contains(defaultPhoneNumber));
		model.getRemovePhoneFromPoolDto().execute(defaultPhoneNumber);
		assertFalse(model.getSelectedPool().getPhoneNumbers().contains(defaultPhoneNumber));
	}
}