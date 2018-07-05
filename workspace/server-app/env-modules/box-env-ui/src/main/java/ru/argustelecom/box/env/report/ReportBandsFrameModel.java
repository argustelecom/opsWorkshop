package ru.argustelecom.box.env.report;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;
import static ru.argustelecom.system.inf.tree.LazyTreeNodeStrategy.DEFAULT_TYPE;

import java.io.Serializable;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.TreeNode;

import lombok.Getter;
import ru.argustelecom.box.env.report.model.ReportBandModel;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.tree.LazyTreeNode;

@Named(value = "reportBandsFm")
@PresentationModel
public class ReportBandsFrameModel implements Serializable {

	@Inject
	private ReportBandModelAppService reportBandModelAs;

	@Inject
	private ReportBandDtoTranslator reportBandDtoTr;

	@Inject
	private ReportBandsLazyTreeNodeLoader loader;

	@Getter
	private ReportBandsLazyTreeNodeStrategy strategy;

	private ReportType reportType;

	@Getter
	private LazyTreeNode<ReportBandDto> treeNode;

	public void preRender(ReportType reportType) {
		this.reportType = reportType;
		initTree();
	}

	public void onNodeExpand(NodeExpandEvent event) {
		strategy.addExpandedUnit((ReportBandDto) event.getTreeNode().getData());
	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		strategy.removeExpandedUnit((ReportBandDto) event.getTreeNode().getData());
	}

	@SuppressWarnings("unchecked")
	public void onDragDrop(TreeDragDropEvent event) {
		LazyTreeNode<ReportBandDto> dragNode = (LazyTreeNode<ReportBandDto>) event.getDragNode();
		LazyTreeNode<ReportBandDto> dropNode = (LazyTreeNode<ReportBandDto>) event.getDropNode();

		if (dropNode.getType().equals(DEFAULT_TYPE)) {
			throw new BusinessException("Невозможно изменить структуру полос, на нижнем уровне может быть только root");
		}

		ReportBandDto dragBand = ((ReportBandDto) dragNode.getData());
		ReportBandDto dropBand = ((ReportBandDto) dropNode.getData());

		strategy.addExpandedUnit(dropBand);

		int dropIndex = event.getDropIndex();
		reportBandModelAs.changeBandParent(dragBand.getId(), dropBand.getId());
		reportBandModelAs.changeOrdinalNumber(dragBand.getId(), dropIndex);

		// исправление работы TreeRenderer
		if (!Objects.equals(dropNode, dragNode.getParent())) {
			dragNode.getParent().getChildren().remove(dragNode);
			dragNode.setParent(dropNode);
		} else {
			dropNode.getChildren().removeIf(treeNode -> treeNode.equals(dragNode));
			dropNode.getChildren().add(dropIndex, dragNode);
		}

		treeNode.refresh();
	}

	@SuppressWarnings("unchecked")
	public void removeSelectedNode() {
		LazyTreeNode<ReportBandDto> selectedNode = strategy.getSelectedNode();
		Long id = ((ReportBandDto) selectedNode.getData()).getId();
		LazyTreeNode<ReportBandDto> parent = (LazyTreeNode<ReportBandDto>) selectedNode.getParent();

		reportBandModelAs.remove(id);
		parent.getChildren().remove(selectedNode);

		strategy.setSelectedNode(null);
		if (parent.getChildren().isEmpty()) {
			parent.setLeaf(true);
			parent.setExpanded(true);
		}

	}

	public void changeSelectedBand() {
		reportBandModelAs.changeBandKeyword(getSelectedBand().getId(), getSelectedBand().getKeyword());
		reportBandModelAs.changeBandDataLoaderType(getSelectedBand().getId(), getSelectedBand().getDataLoaderType());
		reportBandModelAs.changeBandOrientation(getSelectedBand().getId(), getSelectedBand().getOrientation());
	}

	public void changeSelectedBandQuery() {
		reportBandModelAs.changeBandQuery(getSelectedBand().getId(), getSelectedBand().getQuery());
	}

	public boolean hasSelectedNode() {
		return strategy != null && strategy.getSelectedNode() != null;
	}

	public Callback<ReportBandModel> getCallback() {
		return (band -> {
			ReportBandDto bandDto = reportBandDtoTr.translate(band);
			LazyTreeNode<ReportBandDto> selectedNode = strategy.getSelectedNode();
			LazyTreeNode<ReportBandDto> node = new LazyTreeNode<>(loader, bandDto, selectedNode, strategy);

			if (selectedNode.getChildren().isEmpty()) {
				selectedNode.setLeaf(false);
			}

			strategy.getSelectedNode().getChildren().add(node);
			strategy.addExpandedUnit(bandDto);

			strategy.setSelectedNode(node);
			node.setSelected(true);
			selectedNode.setSelected(false);
			selectedNode.setExpanded(true);
		});
	}

	public ReportBandDto getSelectedBand() {
		return strategy != null
				? (ReportBandDto) ofNullable(strategy.getSelectedNode()).map(LazyTreeNode::getData).orElse(null)
				: null;
	}

	public String getParentBandName() {
		return strategy != null ? ofNullable(strategy.getSelectedNode()).map(LazyTreeNode::getParent)
				.map(TreeNode::getData).map(ReportBandDto.class::cast).map(ReportBandDto::getKeyword).orElse(null)
				: null;
	}

	private void initTree() {
		if (reportType == null) {
			treeNode = null;
			return;
		}

		// loader = new ReportBandsLazyTreeNodeLoader();
		strategy = new ReportBandsLazyTreeNodeStrategy();
		ReportBandDto rootBand = reportBandDtoTr.translate(reportType.getRootBand());
		treeNode = LazyTreeNode.createRoot(newArrayList(rootBand), strategy, loader);
		// treeNode.refresh();
	}

	private static final long serialVersionUID = 2210283861193632305L;

}