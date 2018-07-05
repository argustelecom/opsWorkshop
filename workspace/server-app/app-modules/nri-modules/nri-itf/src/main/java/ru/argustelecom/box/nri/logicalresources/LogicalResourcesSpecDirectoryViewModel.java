package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.box.env.type.CurrentType;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.logicalresources.nls.LogicalResourcesSpecDirectoryVMMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationRepository;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

/**
 * Контроллер страницы справочника спецификация логических ресурсов
 * Created by s.kolyada on 31.10.2017.
 */
@PresentationModel
public class LogicalResourcesSpecDirectoryViewModel  extends ViewModel {


	private static final long serialVersionUID = -4221557663822235310L;

	private static final Logger logger = Logger.getLogger(LogicalResourcesSpecDirectoryViewModel.class);

	/**
	 * Рутовая нода дерева спецификаций
	 */
	@Getter
	private TreeNode logicalResourceSpecNode;

	/**
	 * Нода со спецификациями телефонных номеров
	 */
	private TreeNode phoneNumberSpecNode;

	/**
	 * Выбранная нода
	 */
	@Getter
	private TreeNode selectedNode;

	/**
	 * Тип создаваемой спецификации
	 */
	@Getter
	@Setter
	private LogicalResNodeType newNodeType;

	/**
	 * Доступные типы спецификаций
	 */
	@Getter
	private LogicalResNodeType[] nodeTypes = new LogicalResNodeType[]{LogicalResNodeType.PHONE_NUMBER};

	/**
	 * Текущая выбранная спецификация
	 */
	@Inject
	private CurrentType currentSpec;

	/**
	 * Диалог создания спецификации телефонного номера
	 */
	@Inject
	private PhoneNumberSpecCreationDialogModel phoneNumberSpecCreationDialogModel;

	/**
	 * Репозиторий доступа к хранилищу спецификаций телефонных номеров
	 */
	@Inject
	private PhoneNumberSpecificationRepository phoneNumberSpecificationRepository;

	/**
	 * Инициализация страницы
	 */
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		init();
	}

	/**
	 * Инициализация данных
	 */
	private void init() {
		logicalResourceSpecNode = new DefaultTreeNode(LogicalResNodeType.ROOT, null);
		initPhoneNumbers();
	}

	/**
	 * Инициализация дерева телефонных номеров
	 */
	private void initPhoneNumbers() {
		phoneNumberSpecNode = new DefaultTreeNode(LogicalResNodeType.PHONE_NUMBER.getKeyword(),
				LogicalResNodeType.PHONE_NUMBER, logicalResourceSpecNode);
		for (PhoneNumberSpecification specification : phoneNumberSpecificationRepository.getAllSpecs()) {
			addPhoneNumberSpecNode(specification, false);
		}
	}

	/**
	 * Удалить спецификацию
	 */
	public void removeSpec() {
		cleanCurrentSpec();
		if (selectedNode != null) {
			try {
				em.remove(selectedNode.getData());
				em.flush();
				selectedNode.getParent().getChildren().remove(selectedNode);
			}catch(PersistenceException e){
				logger.warn(LocaleUtils.getMessages(LogicalResourcesSpecDirectoryVMMessagesBundle.class).canNot(),e);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								LocaleUtils.getMessages(LogicalResourcesSpecDirectoryVMMessagesBundle.class).canNot(), ""));
			}
			setSelectedNode(null);
			cleanCurrentSpec();
		}
	}

	/**
	 * Проверка что ноду можно удалять
	 * @return истина если можно, иначе ложь
	 */
	public boolean isRemovableNode() {
		return selectedNode != null && !selectedNode.getParent().equals(logicalResourceSpecNode);
	}

	/**
	 * Выставить выбранную ноду
	 * @param selectedNode
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		if (selectedNode != null && selectedNode.getData() instanceof Type) {
			currentSpec.setValue((Type)selectedNode.getData());
		} else {
			cleanCurrentSpec();
		}
		this.selectedNode = selectedNode;
	}

	/**
	 * Очистить текущую выбранныую спецификацию
	 */
	private void cleanCurrentSpec() {
		currentSpec.setValue(null);
	}

	/**
	 * Действия на открытие диалога создания
	 */
	public void onDialogOpen() {
		//TODO: оверлей-панель отключена, т.к. не рисуется список
//		RequestContext.getCurrentInstance().execute("PF('logicalResSpecCreationPanelVar').hide()");
//		switch (newNodeType) {
//			case PHONE_NUMBER: openPhoneNumberCreationDlg(); break;
//			default: throw new IllegalStateException("Unsupported logical resource type");
//		}
//		newNodeType = null;
		openPhoneNumberCreationDlg();
	}

	/**
	 * Открыть диалог создания спеки телефонного номера
	 */
	private void openPhoneNumberCreationDlg() {
		RequestContext.getCurrentInstance().update("phone_number_spec_creation_form");
		RequestContext.getCurrentInstance().execute("PF('phoneNumberSpecCreationDlgVar').show()");
	}

	/**
	 * Действия на создание спецификации
	 */
	public void onSpecCreated() {
		PhoneNumberSpecification newCustomerSpec = phoneNumberSpecCreationDialogModel.create();
		addPhoneNumberSpecNode(newCustomerSpec, true);
	}

	/**
	 * Добавление ноды телефонного номера
	 * @param spec спецификация
	 * @param markSelected сделать ли ноду выбранной
	 * @return созданную ноду
	 */
	private TreeNode addPhoneNumberSpecNode(PhoneNumberSpecification spec, boolean markSelected) {
		TreeNode treeNode = new DefaultTreeNode(LogicalResNodeType.PARTY_SPEC.getKeyword(), spec, phoneNumberSpecNode);
		if (markSelected) {
			if (selectedNode != null)
				selectedNode.setSelected(false);
			treeNode.getParent().setExpanded(true);
			treeNode.setSelected(true);
			setSelectedNode(treeNode);
		}
		return treeNode;
	}
}
