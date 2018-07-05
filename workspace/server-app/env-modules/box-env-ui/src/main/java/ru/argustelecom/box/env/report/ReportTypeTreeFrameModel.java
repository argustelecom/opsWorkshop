package ru.argustelecom.box.env.report;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.abs;
import static java.util.Collections.binarySearch;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.system.inf.tree.LazyTreeNodeStrategy.DEFAULT_TYPE;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.report.ReportTypeAttributesFrameModel.ReportTypeEditingEvent;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.report.model.ReportTypeGroup;
import ru.argustelecom.box.env.type.CurrentType;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.tree.LazyTreeNode;

@Named("reportTypeTreeFm")
@PresentationModel
public class ReportTypeTreeFrameModel implements Serializable {

	private static final long serialVersionUID = -4590314053065263477L;

	@PersistenceContext
	private EntityManager em;

	@Getter
	private ReportTypeDto reportTypeDto;

	@Getter
	private BusinessObjectDto<ReportType> reportTypeBODto;

	@Inject
	private ReportTypeGroupAppService rtgApp;

	@Inject
	private ReportTypeAppService reportTypeAppSrv;

	@Inject
	private CurrentType currentReportType;

	@Inject
	private ReportTypeLazyTreeNodeDtoTranslator reportTypeTreeNodeDtoTr;

	@Inject
	private ReportTypeDtoTranslator reportTypeDtoTr;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private ReportTypeGroupDtoTranslator reportTypeGroupDtoTr;

	private ReportTypeLazyTreeNodeStrategy reportLazyTreeNodeStrategy;
	private ReportTypeLazyTreeNodeLoader reportLazyTreeNodeLoader;

	private List<ReportTypeTreeNodeDto> nodes;

	@Getter
	@Setter
	private LazyTreeNode<?> root;
	private ReportTypeTreeNodeDto currentReportTypeNode;

	public void preRender() {
		if (currentReportType != null && currentReportType.getValue() instanceof ReportType) {
			reportTypeDto = reportTypeDtoTr.translate((ReportType) currentReportType.getValue());
			reportTypeBODto = businessObjectDtoTr.translate((ReportType) currentReportType.getValue());
			currentReportTypeNode = reportTypeTreeNodeDtoTr.translate(reportTypeDto);
		}

		if (root == null) {
			initNodes();
		}
	}

	@SuppressWarnings("unchecked")
	public void setSelectedNode(TreeNode selectedNode) {
		reportLazyTreeNodeStrategy.setSelectedNode((LazyTreeNode<ReportTypeTreeNodeDto>) selectedNode);

		if (selectedNode != null) {
			ReportTypeTreeNodeDto node = (ReportTypeTreeNodeDto) selectedNode.getData();
			if (!selectedNode.getType().equals(DEFAULT_TYPE) && node.getCategory() != ReportTypeCategory.GROUP) {
				ReportType reportType = em.find(ReportType.class, node.getId());
				reportTypeDto = reportTypeDtoTr.translate(reportType);
				currentReportType.setValue(reportType);
			} else {
				reportTypeDto = null;
				reportTypeBODto = null;
				currentReportType.setValue(null);
			}
			selectedNode.setSelected(true);
			currentReportTypeNode = node;
		} else {
			currentReportTypeNode = null;
			currentReportType.setValue(null);
		}
	}

	public TreeNode getSelectedNode() {
		return reportLazyTreeNodeStrategy.getSelectedNode();
	}

	public ReportTypeGroupDto getReportTypeGroupDto() {
		if (currentReportTypeNode == null) {
			return null;
		} else if (currentReportTypeNode.getCategory() == ReportTypeCategory.TYPE) {
			return reportTypeDto.getReportTypeGroup();
		} else if (currentReportTypeNode.getCategory() == ReportTypeCategory.GROUP) {
			return reportTypeGroupDtoTr.translate(em.find(ReportTypeGroup.class, currentReportTypeNode.getId()));
		}
		throw new SystemException();
	}

	public void remove() {
		LazyTreeNode<ReportTypeTreeNodeDto> selectedNode = reportLazyTreeNodeStrategy.getSelectedNode();
		ReportTypeTreeNodeDto selectedNodeData = (ReportTypeTreeNodeDto) selectedNode.getData();
		if (selectedNodeData.getCategory() == ReportTypeCategory.TYPE) {
			TreeNode parent = selectedNode.getParent();
			parent.getChildren().remove(selectedNode);
			setSelectedNode(parent);
			rtgApp.removeResourceType(selectedNodeData.getId());
		} else {
			root.getChildren().remove(selectedNode);
			rtgApp.removeResourceTypeGroup(selectedNodeData.getId());
		}
		setSelectedNode(null);
	}

	public void onCreateReportType() {
		RequestContext.getCurrentInstance().execute("PF('reportCategoriesPanelVar').hide()");
		RequestContext.getCurrentInstance().execute("PF('reportTypeCreationDlgVar').show()");
	}

	public Callback<ReportTypeGroupDto> getReportTypeGroupCallback() {
		return (newReportTypeGroupDto -> {

			ReportTypeTreeNodeDto reportTypeTreeNode = reportTypeTreeNodeDtoTr.translate(newReportTypeGroupDto);

			TreeNode newNode = LazyTreeNode.create(Lists.newArrayList(reportTypeTreeNode), reportLazyTreeNodeStrategy,
					reportLazyTreeNodeLoader, root).get(0);

			root.getChildren().add(
					abs(binarySearch(root.getChildren(), newNode, Comparator.comparing(TreeNode::getType)) + 1),
					newNode);
			nodes.add(reportTypeTreeNode);

			if (getSelectedNode() != null) {
				getSelectedNode().setSelected(false);
			}
			setSelectedNode(newNode);
		});
	}

	public Callback<ReportTypeDto> getReportTypeCreatingCallback() {
		return (newReportTypeDto -> {

			ReportTypeTreeNodeDto newNodeDto = reportTypeTreeNodeDtoTr.translate(newReportTypeDto);

			ReportTypeTreeNodeDto parent = ofNullable(newNodeDto.getParent()).map(nodes::indexOf).map(nodes::get)
					.orElse(null);

			LazyTreeNode<?> parentNode = (LazyTreeNode<?>) root.getChildren().stream()
					.filter(tn -> Objects.equals(tn.getData(), parent)).findFirst().orElseGet(this::getRoot);

			LazyTreeNode<?> newNode = (LazyTreeNode<?>) LazyTreeNode.create(Lists.newArrayList(newNodeDto),
					reportLazyTreeNodeStrategy, reportLazyTreeNodeLoader, parentNode).get(0);
			parentNode.getChildren().add(newNode);
			parentNode.setLeaf(false);

			if (getSelectedNode() != null) {
				getSelectedNode().setSelected(false);
			}
			setSelectedNode(newNode);
		});
	}

	@SuppressWarnings("unchecked")
	private void postReportTypeGroupEditing(
			@Observes(during = TransactionPhase.BEFORE_COMPLETION, notifyObserver = Reception.IF_EXISTS) ReportTypeEditingEvent event) {
		ReportTypeTreeNodeDto newParent = reportTypeTreeNodeDtoTr.translate(event.getReportType().getReportTypeGroup());
		ReportTypeTreeNodeDto prevParent = reportTypeTreeNodeDtoTr.translate(event.getPrevGroupDto());
		ReportTypeTreeNodeDto type = reportTypeTreeNodeDtoTr.translate(event.getReportType());

		checkState(nodes.contains(newParent));
		checkState(nodes.contains(prevParent));

		TreeNode prevParentNode = root.getChildren().stream().filter(tn -> Objects.equals(tn.getData(), prevParent))
				.findFirst().orElseThrow(SystemException::new);
		TreeNode newParentNode = root.getChildren().stream().filter(tn -> Objects.equals(tn.getData(), newParent))
				.findFirst().orElseThrow(SystemException::new);
		TreeNode typeNode = prevParentNode.getChildren().stream().filter(tn -> Objects.equals(tn.getData(), type))
				.findFirst().orElseThrow(SystemException::new);

		prevParentNode.getChildren().remove(typeNode);
		TreeNode newNode = LazyTreeNode
				.create(Lists.newArrayList(type), reportLazyTreeNodeStrategy, reportLazyTreeNodeLoader, newParentNode)
				.get(0);
		newParentNode.getChildren().add(newNode);

		if (getSelectedNode() != null) {
			getSelectedNode().setSelected(false);
		}
		setSelectedNode(newNode);
	}

	private void initNodes() {
		reportLazyTreeNodeLoader = new ReportTypeLazyTreeNodeLoader(rtgApp, reportTypeTreeNodeDtoTr);
		reportLazyTreeNodeStrategy = new ReportTypeLazyTreeNodeStrategy();
		root = LazyTreeNode.createRoot(loadRootNodes(), reportLazyTreeNodeStrategy, reportLazyTreeNodeLoader);
		if (reportTypeDto != null) {
			if (nonNull(reportTypeDto.getReportTypeGroup())) {
				reportLazyTreeNodeStrategy.setExpandedUnits(
						Sets.newHashSet(reportTypeTreeNodeDtoTr.translate(reportTypeDto.getReportTypeGroup())));
			}
			reportLazyTreeNodeStrategy.setSelectedUnit(currentReportTypeNode);
		}
		root.refresh();
	}

	private List<ReportTypeTreeNodeDto> loadRootNodes() {
		if (nodes == null) {
			nodes = rtgApp.findRootGroups().stream().map(g -> reportTypeTreeNodeDtoTr.translate(g)).collect(toList());
			nodes.addAll(reportTypeAppSrv.findTypesWithoutGroup().stream()
					.map(reportType -> reportTypeTreeNodeDtoTr.translate(reportType)).collect(toList()));
		}
		return nodes;
	}
}
