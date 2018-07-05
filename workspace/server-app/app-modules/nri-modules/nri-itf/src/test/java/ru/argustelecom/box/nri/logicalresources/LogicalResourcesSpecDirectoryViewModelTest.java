package ru.argustelecom.box.nri.logicalresources;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.box.ContextMocker;
import ru.argustelecom.box.env.type.CurrentType;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationRepository;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by s.kolyada on 01.11.2017.
 */
@RunWith(PowerMockRunner.class)
public class LogicalResourcesSpecDirectoryViewModelTest {

	@Mock
	private UnitOfWork unitOfWork;

	@Mock
	private PhoneNumberSpecificationRepository phoneNumberSpecificationRepository;

	@Mock
	private CurrentType CurrentType;

	@Mock
	private EntityManager em;

	@Mock
	private PhoneNumberSpecCreationDialogModel phoneNumberSpecCreationDialogModel;

	@InjectMocks
	private LogicalResourcesSpecDirectoryViewModel testingClass;


	@Test
	public void shouldInitOnPostConstruct() throws Exception {
		List<PhoneNumberSpecification> specs = Arrays.asList(new PhoneNumberSpecification(1L));
		when(phoneNumberSpecificationRepository.getAllSpecs()).thenReturn(specs);

		testingClass.postConstruct();

		assertNotNull(testingClass.getLogicalResourceSpecNode());
		assertFalse(testingClass.getLogicalResourceSpecNode().getChildren().isEmpty());

		verify(unitOfWork, times(1)).makePermaLong();
		verify(phoneNumberSpecificationRepository,times(1)).getAllSpecs();
	}

	@Test
	public void shouldRemoveSpec() throws Exception {
		PhoneNumberSpecification spec = new PhoneNumberSpecification(1L);
		TreeNode rootNode = new DefaultTreeNode(LogicalResNodeType.ROOT.getKeyword(), null, null);
		TreeNode treeNode = new DefaultTreeNode(LogicalResNodeType.PARTY_SPEC.getKeyword(), spec, rootNode);
		testingClass.setSelectedNode(treeNode);

		testingClass.removeSpec();

		verify(CurrentType, atLeastOnce()).setValue(eq(null));
		verify(em, times(1)).remove(eq(spec));
		assertNull(testingClass.getSelectedNode());
	}

	@Test
	public void shouldCheckIsRemovableNode() throws Exception {
		assertFalse(testingClass.isRemovableNode());

		List<PhoneNumberSpecification> specs = Arrays.asList(new PhoneNumberSpecification(1L));
		when(phoneNumberSpecificationRepository.getAllSpecs()).thenReturn(specs);
		testingClass.postConstruct();
		testingClass.setSelectedNode(testingClass.getLogicalResourceSpecNode().getChildren().get(0));
		assertFalse(testingClass.isRemovableNode());

		specs = Arrays.asList(new PhoneNumberSpecification(1L));
		when(phoneNumberSpecificationRepository.getAllSpecs()).thenReturn(specs);
		testingClass.postConstruct();
		testingClass.setSelectedNode(testingClass.getLogicalResourceSpecNode().getChildren().get(0).getChildren().get(0));
		assertTrue(testingClass.isRemovableNode());
	}

	@Test
	public void shouldSetNullSelectedNode() throws Exception {
		TreeNode rootNode = new DefaultTreeNode(LogicalResNodeType.ROOT.getKeyword(), null, null);
		testingClass.setSelectedNode(rootNode);

		testingClass.setSelectedNode(null);
		verify(CurrentType, atLeastOnce()).setValue(eq(null));
		assertNull(testingClass.getSelectedNode());
	}

	@Test
	public void shouldCreateSpec() throws Exception {
		List<PhoneNumberSpecification> specs = Arrays.asList(new PhoneNumberSpecification(1L));
		when(phoneNumberSpecificationRepository.getAllSpecs()).thenReturn(specs);

		testingClass.postConstruct();

		PhoneNumberSpecification spec = new PhoneNumberSpecification(1L);
		when(phoneNumberSpecCreationDialogModel.create()).thenReturn(spec);

		testingClass.onSpecCreated();

		assertNotNull(testingClass.getSelectedNode());
		assertEquals(spec, testingClass.getSelectedNode().getData());
	}

	@Ignore
	@Test(expected = IllegalStateException.class)
	public void shouldNotOpenDialog(){
		ContextMocker.mockFacesContext();
		testingClass.setNewNodeType(LogicalResNodeType.ROOT);
		testingClass.onDialogOpen();
	}

	@Ignore
	@Test
	public void shouldOpenDialog(){
		ContextMocker.mockFacesContext();
		testingClass.setNewNodeType(LogicalResNodeType.PHONE_NUMBER);
		testingClass.onDialogOpen();
		verify(RequestContext.getCurrentInstance(),times(1)).update(eq("phone_number_spec_creation_form"));
		verify(RequestContext.getCurrentInstance(),times(1)).execute(eq("PF('phoneNumberSpecCreationDlgVar').show()"));
	}
}