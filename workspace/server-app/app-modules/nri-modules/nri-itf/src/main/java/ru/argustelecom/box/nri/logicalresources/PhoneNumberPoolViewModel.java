package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import org.jboss.logging.Logger;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.nri.LazyNode;
import ru.argustelecom.box.nri.logicalresources.nls.LogicalResourcesMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.google.common.collect.Iterables.getFirst;

/**
 * Контроллер страницы просмотра и редактирования пулов номеров телефонов
 *
 * @author d.khekk
 * @since 31.10.2017
 */
@Named(value = "phoneNumberPoolVM")
@PresentationModel
public class PhoneNumberPoolViewModel extends ViewModel {

	private static final Logger log = Logger.getLogger(PhoneNumberPoolViewModel.class);

	/**
	 * Корневой элемент дерева
	 */
	@Getter
	private TreeNode rootElement;

	/**
	 * Выбранный элемент дерева
	 */
	@Setter
	@Getter
	private TreeNode selectedNode;

	/**
	 * Выбранный пул
	 */
	@Setter
	@Getter
	private PhoneNumberPoolDto selectedPool;

	/**
	 * Сервис доступа к пулам телефонных номеров
	 */
	@Inject
	private PhoneNumberPoolAppService poolService;

	/**
	 * Сервис доступа к хранилищу телефонных номеров
	 */
	@Inject
	private PhoneNumberAppService phoneService;

	/**
	 * Состояние страницы пулов
	 */
	@Inject
	private PhoneNumberPoolViewState viewState;

	/**
	 * Создать пул
	 */
	@Getter
	private final Callback<PhoneNumberPoolDto> createPool = poolToCreate -> {
		PhoneNumberPoolDto persistedPool = poolService.createPool(poolToCreate);
		Optional.ofNullable(selectedNode).ifPresent(node -> node.setSelected(false));
		selectedNode = select(new LazyNode(persistedPool, rootElement, this::loadPhoneNumbers));
	};

	/**
	 * Удалить номер из пула
	 */
	@Getter
	private final Callback<PhoneNumberDto> removePhoneFromPoolDto = number -> selectedPool.getPhoneNumbers().remove(number);

	/**
	 * Создает номер из дтошки и спецификации
	 */
	@Getter
	private final BiConsumer<PhoneNumberDto, PhoneNumberSpecification> createPhoneNumber =
			(phoneNumber, phoneSpec) -> selectedPool.getPhoneNumbers().add(phoneService.createPhoneNumber(phoneNumber, phoneSpec));

	/**
	 * Обновляет пул
	 */
	@Getter
	private final Callback<PhoneNumberPoolDto> refreshPoolInTree = pool -> {
		selectedPool.getPhoneNumbers().clear();
		selectedPool.getPhoneNumbers().addAll(pool.getPhoneNumbers());
	};

	/**
	 * Действия после создания модели
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		rootElement = initTree();

		Optional<TreeNode> poolFromViewState = viewState.getPool() == null ? Optional.empty() :
				rootElement.getChildren().stream()
						.filter(node -> ((PhoneNumberPoolDto) node.getData()).getId().equals(viewState.getPool().getId()))
						.findFirst();
		selectedNode = select(poolFromViewState.orElse(getFirst(rootElement.getChildren(), null)));
		unitOfWork.makePermaLong();
	}

	/**
	 * Проинициализировать дерево
	 *
	 * @return проинициализированное дерево
	 */
	private TreeNode initTree() {
		TreeNode root = new DefaultTreeNode("рут", null);
		poolService.findAllLazy().forEach(pool -> new LazyNode(pool, root, this::loadPhoneNumbers));
		return root;
	}

	/**
	 * Слушатель события выбора ноды в дереве
	 *
	 * @param event событие выбора ноды в дереве
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		select(event.getTreeNode());
	}

	/**
	 * произвести действия, требуемые при выборе ноды
	 *
	 * @param treeNode выбранная нода
	 * @return выбранная нода
	 */
	private TreeNode select(TreeNode treeNode) {
		if (treeNode != null) {
			((LazyNode) treeNode).loadChildren();
			if (treeNode.getData() instanceof PhoneNumberPoolDto) {
				selectedPool = (PhoneNumberPoolDto) treeNode.getData();
			}
			treeNode.setSelected(true);
		} else {
			selectedPool = null;
		}
		return treeNode;
	}

	/**
	 * Удалить выбранный пул
	 */
	public void removePool() {
		if (selectedNode != null) {
			PhoneNumberPoolDto pool = (PhoneNumberPoolDto) selectedNode.getData();
			try {
				poolService.remove(pool);
				List<TreeNode> nodeNeighbors = selectedNode.getParent().getChildren();
				nodeNeighbors.remove(selectedNode);
				selectedNode = select(getFirst(nodeNeighbors, null));
				viewState.setPool(null);
			} catch (Exception e) {
				log.error("Ошибка при удалении пула", e);
				Notification.error(LocaleUtils.getMessages(LogicalResourcesMessagesBundle.class).error(), e.getMessage());
			}
		}
	}

	/**
	 * Подгрузить номера
	 *
	 * @param poolNode ветка с пулом
	 */
	private void loadPhoneNumbers(LazyNode poolNode) {
		Long poolId = ((PhoneNumberPoolDto) poolNode.getData()).getId();
		((PhoneNumberPoolDto) poolNode.getData()).setPhoneNumbers(poolService.getPhoneNumbers(poolId));
	}
}
