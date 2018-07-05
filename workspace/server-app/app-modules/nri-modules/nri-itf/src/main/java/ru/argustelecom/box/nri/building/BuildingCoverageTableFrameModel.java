package ru.argustelecom.box.nri.building;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;
import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.building.nls.BuildingCoverageTableFrameModelMessagesBundle;
import ru.argustelecom.box.nri.coverage.ResourceInstallationAppService;
import ru.argustelecom.box.nri.coverage.ResourceInstallationDto;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Фрейм таблицы стукртуры дома с указанием установки ресурсов и зоны их покрытия
 *
 * @author s.kolyada
 * @since 05.08.2017
 */
@Named(value = "buildingCoverageTableFrameModel")
@PresentationModel
public class BuildingCoverageTableFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BuildingCoverageTableFrameModel.class);

	/**
	 * Инсталляция ресурса
	 */
	private ResourceInstallationDto installation;

	/**
	 * Дерево помещений. По сути это исходное дерево BuildingElement'ов, нагруженное метаданными
	 */
	@Getter
	@Setter
	private CoverageNodeDescriptor buildingTree;

	/**
	 * Дерево помещений в виде специфической таблицы.
	 * Предназначена для отображения в ui.
	 */
	@Getter
	@Setter
	private List<List<CoverageNodeDescriptor>> buildingTable;

	/**
	 * Сервис для работы с элементами структуры строения
	 */
	@Inject
	private BuildingElementAppService buildingService;

	/**
	 * Сервис для работы с инсталляциями
	 */
	@Inject
	private ResourceInstallationAppService installationService;

	/**
	 * Транслятор элементов строений в ДТО
	 */
	@Inject
	private BuildingElementDtoTranslator elementDtoTranslator;

	/**
	 * Инициализирует фрейм
	 * @param installation точка монтирования
	 */
	public void preRender(ResourceInstallationDto installation) {
		this.installation = installation;
		refresh();
	}

	/**
	 * Обновить данные представления
	 */
	public void refresh() {
		BuildingElementDto buildingElement = buildingService.findBuildingByResInstallation(installation);
		buildingTree = buildingToTree(buildingElement);
		initCoveredNodes(installation, buildingTree);
		setRowspans(buildingTree);
		buildingTable = treeToRows(buildingTree, newArrayList()).collect(toList());
	}

	/**
	 * закрашивает покрываемые ноды
	 * @param installation точка монтирования
	 * @param root дерево
	 */
	private void initCoveredNodes(ResourceInstallationDto installation, CoverageNodeDescriptor root) {
		// используем мапу, чтобы избежать O(n^2)
		Map<Long, CoverageNodeDescriptor> nodes = flatten(root)
				.collect(toMap(node -> node.getElement().getId(), node -> node));
		installation.getCover().stream()
				.map(coveredElem -> nodes.get(coveredElem.getId()))
				.filter(Objects::nonNull)
				.forEach(node -> setCoveredRecursive(node, true));
	}

	/**
	 * рекурсивно ставит/убирает покрытие ноде и всем ее потомкам
	 * @param node нода
	 * @param covered покрыта/не покрыта
	 */
	private void setCoveredRecursive(CoverageNodeDescriptor node, boolean covered) {
		flatten(node).forEach(child -> child.setCovered(covered));
	}

	/**
	 * Слушатель события изменения точки монтирования
	 */
	public void installationChanged() throws BusinessExceptionWithoutRollback {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ectx = ctx.getExternalContext();
		Map<String, String> reqParams = ectx.getRequestParameterMap();

		Long id = MapUtils.getLong(reqParams, "elementId");

		if (id == null) {
			throw new BusinessExceptionWithoutRollback("sd");
		}

		BuildingElementDto buildingElement = elementDtoTranslator.translate(buildingService.findElementById(id));
		installationService.updateInstallationPoint(installation, buildingElement);
		installation.setInstalledAt(buildingElement);

		refresh();

		BuildingCoverageTableFrameModelMessagesBundle messages = LocaleUtils.getMessages(BuildingCoverageTableFrameModelMessagesBundle.class);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, messages.changes(),messages.mountPointIsChanged()
				);
		ctx.addMessage(null, message);
	}

	/**
	 * Слушатель события изменения покрытия
	 */
	public void onElementClicked() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ectx = ctx.getExternalContext();
		Map<String, String> reqParams = ectx.getRequestParameterMap();
		Long id = Long.valueOf(reqParams.get("elementId"));
		CoverageNodeDescriptor selectedNode = flatten(buildingTree)
				.filter(node -> id.equals(node.getElement().getId()))
				.findFirst().orElseThrow(() -> new BusinessException(LocaleUtils.getMessages(BuildingCoverageTableFrameModelMessagesBundle.class)
						.couldNotFindNodeWithElementOfBuildingById()));
		if (!selectedNode.covered) {
			setCoveredRecursive(selectedNode, true);
			RequestContext.getCurrentInstance().update("coverage_table_form-myPanelGrid");
		} else {
			if (selectedNode.getParent() != null && selectedNode.getParent().covered) {
				log.info("Невозможно снять покрытие с элемента, если покрыт отец");
				return; // если папа есть и покрыт, то снятие покрытия с ребенка невозможно
			}
			setCoveredRecursive(selectedNode, false);
		}
		updateCoverage();
	}

	/**
	 * Слушатель события изменения покрытия
	 * @param selectedNode выбранный узел
	 */
	public void onElementClicked(CoverageNodeDescriptor selectedNode) {
		flatten(selectedNode).forEach(node -> node.setCovered(true));
		RequestContext.getCurrentInstance().update("coverage_table_form-myPanelGrid");
	}

	/**
	 * сплющивает дерево
	 * @param root дерево
	 * @return стрим нод
	 */
	private Stream<CoverageNodeDescriptor> flatten(CoverageNodeDescriptor root) {
		return Stream.concat(Stream.of(root), root.children.stream().flatMap(this::flatten));
	}

	/**
	 * Преобразует строение из дерева в стрим списков.
	 * Это конструкция, специально предназначенная для корректного отображения
	 * в элементе panelGrid в ui.
	 * Алгоритм представляет из себя распиливание дерева на ветки.
	 * Каждый узел дерева с x детьми разрезаем x-1 раз и получаем x веток, одна длинная (содержащая
	 * всю предыдущую часть ветки), и x-1 коротких. проделываем это со всеми узлами
	 * @param node дерево
	 * @param row предыдущая часть длинной ветки
	 * @return распиленная ветка
	 */
	public static Stream<List<CoverageNodeDescriptor>> treeToRows(CoverageNodeDescriptor node ,
			List<CoverageNodeDescriptor> row) {
		row.add(node);
		List<CoverageNodeDescriptor> children = node.getChildren();
		if (isEmpty(children))
			return Stream.of(row);
		else {
			Stream<List<CoverageNodeDescriptor>> longBranch = treeToRows(children.get(0), row);
			Stream<List<CoverageNodeDescriptor>> shortBranches = children.stream().skip(1)
					.flatMap(child -> treeToRows(child, newArrayList()));
			return Stream.concat(longBranch, shortBranches);
		}
	}

	/**
	 * Преобразует строение в дерево.
	 * Требуется для хранения метаданных элементов
	 * @param building строение
	 * @return дерево
	 */
	private CoverageNodeDescriptor buildingToTree(BuildingElementDto building) {
		CoverageNodeDescriptor node = new CoverageNodeDescriptor();
		node.setElement(building);
		node.setChildren(building.getChildElements().stream().map(this::buildingToTree).collect(toList()));
		node.getChildren().forEach(child -> child.setParent(node));
		if (building.getId().equals(installation.getInstalledAt().getId())) {
			node.hasInstallationPoint = true;
		}
		return node;
	}

	/**
	 * рекурсивно проставляет rowspan (т.е. высоту ячейки) для отображения нод в ui.
	 * каждая нода имеет rowspan равный сумме rowspan'ов всех ее детей.
	 * каждый ребенок без детей имеет rowspan 1
	 * @param node узел, который будем заполнять
	 * @return rowspan узла
	 */
	private int setRowspans(CoverageNodeDescriptor node) {
		int rowspan = node.getChildren().stream()
				.mapToInt(this::setRowspans)
				.reduce(Integer::sum).orElse(1);
		node.setRowspan(rowspan);
		return rowspan;
	}

	/**
	 * сохраняет покрытие
	 */
	private void updateCoverage() {
		List<BuildingElementDto> coveredNodes = flatten(buildingTree)
				.filter(CoverageNodeDescriptor::isCovered)
				.map(this::toOldestCoveredParent)
				.distinct()
				.map(CoverageNodeDescriptor::getElement)
				.collect(toList());
		installation.setCover(coveredNodes);
		installationService.updateInstallationCoveredElements(installation);
	}

	/**
	 * возвращает старшего покрытого отца
	 * @param node нода
	 * @return старший покрытый отец
	 */
	private CoverageNodeDescriptor toOldestCoveredParent(CoverageNodeDescriptor node) {
		CoverageNodeDescriptor parent = node.getParent();
		return (parent == null || !parent.isCovered()) ? node : toOldestCoveredParent(node.getParent());
	}


	/**
	 * Узел в таблице структуры дома
	 */
	@Getter
	@Setter
	public static class CoverageNodeDescriptor implements Serializable {
		private static final long serialVersionUID = 1L;
		private BuildingElementDto element;
		private CoverageNodeDescriptor parent;
		private List<CoverageNodeDescriptor> children = new ArrayList<>();
		private int rowspan;
		private boolean covered;
		private boolean hasInstallationPoint;
	}
}
