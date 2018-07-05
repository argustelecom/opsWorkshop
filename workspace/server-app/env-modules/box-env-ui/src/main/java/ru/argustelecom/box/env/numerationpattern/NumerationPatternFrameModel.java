package ru.argustelecom.box.env.numerationpattern;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.bill.BillTypeRepository;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.contract.ContractTypeRepository;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.document.model.Document;
import ru.argustelecom.box.env.dto.DefaultDtoConverterUtils;
import ru.argustelecom.box.env.numerationpattern.model.BillNumerationPattern;
import ru.argustelecom.box.env.numerationpattern.model.ContractNumerationPattern;
import ru.argustelecom.box.env.numerationpattern.model.NumerationPattern;
import ru.argustelecom.box.env.numerationpattern.nls.NumerationMessagesBundle;
import ru.argustelecom.box.env.numerationpattern.parser.NumerationPatternParser;
import ru.argustelecom.box.env.numerationpattern.statement.Statement;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "numerationPatternFm")
@PresentationModel
public class NumerationPatternFrameModel implements Serializable {

	private static final String NODE_NAME = "node";

	@Inject
	private ContractTypeRepository contractTypeRepository;

	@Inject
	private NumerationPatternDtoTranslator numerationPatternDtoTranslator;

	@Inject
	private NumerationPatternParser parser;

	@Inject
	private NumberGenerator numberGenerator;

	@Inject
	private NumerationPatternAppService numerationPatternAppService;

	@Inject
	private NumerationPatternRepository numerationPatternRepository;

	@Inject
	private NumerationPatternFrameState numerationPatternFrameState;

	@Getter
	private List<NumerationPatternNodeData> numerationPatternNodeDataList;

	@Inject
	private BillTypeRepository billTypeRepository;

	@Inject
	private TypeDtoTranslator typeDtoTranslator;

	@Getter
	private TreeNode root;

	@Getter
	@Setter
	private NodeType selectedNodeType;

	@Getter
	@Setter
	private TreeNode selectedNode;

	@Getter
	@Setter
	private NumerationPatternNodeData selectedNumerationPatternNodeData;

	@Getter
	private String previewPattern;

	@PostConstruct
	private void init() {
		initNodes();
	}

	public void onSave() {
		if (!isAvailableNumberLength()) {
			throw new BusinessException(getGreaterThanMaxNumberLengthMsg());
		}

		NumerationPatternDto selectedNumerationDto = selectedNumerationPatternNodeData.getNumerationPatternDto();

		String pattern = selectedNumerationDto.getPattern() == null ? null : selectedNumerationDto.getPattern().trim();

		if (isNullOrEmpty(pattern)) {
			if (isEditMode(selectedNumerationPatternNodeData)) {
				numerationPatternAppService.deleteNumerationPattern(selectedNumerationDto.getId());
				selectedNumerationDto.setId(null);
			}
		} else {
			List<Statement> statements = parser.parse(selectedNumerationDto.getClassName(), pattern);

			if (isEditMode(selectedNumerationPatternNodeData)) {
				numerationPatternAppService.editNumerationPattern(selectedNumerationDto.getId(), pattern, statements);
			} else {
				create(statements);
			}
		}
		invert(selectedNumerationPatternNodeData);
	}

	public String getGreaterThanMaxNumberLengthMsg() {
		if (selectedNumerationPatternNodeData == null)
			return StringUtils.EMPTY;

		NumerationMessagesBundle numerationMessages = LocaleUtils.getMessages(NumerationMessagesBundle.class);
		return numerationMessages.numberIsTooLarge(previewPattern.length(),
				selectedNumerationPatternNodeData.getNodeType().getMaxNumberLength());
	}

	public boolean isAvailableNumberLength() {
		if (selectedNumerationPatternNodeData == null)
			return false;

		createPreviewPattern();
		return previewPattern.length() <= selectedNumerationPatternNodeData.getNodeType().maxNumberLength;
	}

	private void create(List<Statement> statements) {
		Long createdNumerationPatternId;
		String className = selectedNumerationPatternNodeData.getNodeType().getClazz().getName();
		String pattern = selectedNumerationPatternNodeData.getNumerationPatternDto().getPattern();
		Long typeId = selectedNumerationPatternNodeData.getTypeDto() != null
				? selectedNumerationPatternNodeData.getTypeDto().getId()
				: null;

		switch (selectedNumerationPatternNodeData.getNodeType()) {
		case CONTRACT:
		case CONTRACT_EXT:
			createdNumerationPatternId = numerationPatternAppService
					.createContractNumerationPattern(className, pattern, statements, typeId).getId();
			break;
		case BILL:
			createdNumerationPatternId = numerationPatternAppService
					.createBillNumerationPattern(className, pattern, statements, typeId).getId();
			break;
		case TASK:
		case ORDER:
		case PERSONAL_ACCOUNT:
			createdNumerationPatternId = numerationPatternAppService
					.createNumerationPattern(className, pattern, statements).getId();
			break;
		default:
			throw new BusinessException(String.format("No create method associated with %s NodeType",
					selectedNumerationPatternNodeData.getNodeType()));
		}

		selectedNumerationPatternNodeData.getNumerationPatternDto().setId(createdNumerationPatternId);
	}

	private boolean isEditMode(NumerationPatternNodeData numerationPatternNodeDto) {
		return numerationPatternNodeDto.getNumerationPatternDto().getId() != null;
	}

	public void createPreviewPattern() {
		NumerationPatternDto numPattern = selectedNumerationPatternNodeData.getNumerationPatternDto();
		previewPattern = numberGenerator.generatePreviewNumber(numPattern.getClassName(),
				parser.parse(numPattern.getClassName(), numPattern.getPattern()));
	}

	public void invert(NumerationPatternNodeData numerationPatternNodeDto) {
		boolean currentState = numerationPatternNodeDto.isEdit();
		numerationPatternNodeDto.setEdit(!currentState);
	}

	private void initNodes() {
		root = numerationPatternFrameState.getRoot();

		if (root == null) {
			root = new DefaultTreeNode("root", null);

			Map<NodeType, NodeDtoRootChildrenPair> nodesData = getNodesData();
			numerationPatternNodeDataList = Lists.newArrayList();

			Arrays.stream(NodeType.values()).forEach(nodeType -> {
				DefaultTreeNode treeNode = new DefaultTreeNode(NODE_NAME, nodesData.get(nodeType).getRootNodeData(),
						root);
				nodesData.get(nodeType).getChildrenNodeData()
						.forEach(nodeData -> new DefaultTreeNode(NODE_NAME, nodeData, treeNode));

				numerationPatternNodeDataList.add(nodesData.get(nodeType).getRootNodeData());
				numerationPatternNodeDataList.addAll(nodesData.get(nodeType).getChildrenNodeData());
			});

			numerationPatternFrameState.setRoot(root);
		}
	}

	private Map<NodeType, NodeDtoRootChildrenPair> getNodesData() {

		Map<NodeType, NodeDtoRootChildrenPair> nodeDataMap = getNodeDataMapWithDefaultData();

		numerationPatternRepository.findAllNumerationPatterns().forEach(numerationPattern -> {
			NodeType nodeType = NodeType.findByClassName(numerationPattern.getClassName());

			if (!NodeType.isNodeTypeWithChildren(nodeType)) {
				nodeDataMap.get(nodeType).getRootNodeData()
						.setNumerationPatternDto(numerationPatternDtoTranslator.translate(numerationPattern));
			} else {
				completeNodeDtoPair(nodeDataMap.get(nodeType), nodeType, numerationPattern);
			}
		});

		return nodeDataMap;
	}

	private Map<NodeType, NodeDtoRootChildrenPair> getNodeDataMapWithDefaultData() {
		Map<NodeType, NodeDtoRootChildrenPair> nodeDataMap = new EnumMap<>(NodeType.class);
		Map<NodeType, List<TypeDto>> nodeTypeListMap = getTypeDtoDtoMap();
		Arrays.stream(NodeType.values()).forEach(nodeType -> {

			List<NumerationPatternNodeData> childrenNodeData = nodeTypeListMap
					.getOrDefault(nodeType, Lists.newArrayList()).stream()
					.map(typeDto -> NumerationPatternNodeData.builder().numerationPatternDto(new NumerationPatternDto())
							.typeDto(typeDto).nodeType(nodeType).build())
					.collect(Collectors.toList());

			NumerationPatternNodeData emptyRootNodeData = NumerationPatternNodeData.builder()
					.numerationPatternDto(new NumerationPatternDto()).nodeType(nodeType).build();

			nodeDataMap.put(nodeType, new NodeDtoRootChildrenPair(emptyRootNodeData, childrenNodeData));
		});
		return nodeDataMap;
	}

	private Map<NodeType, List<TypeDto>> getTypeDtoDtoMap() {
		Map<NodeType, List<TypeDto>> typeDtoMap = new EnumMap<>(NodeType.class);
		typeDtoMap.put(NodeType.CONTRACT, translateTypes(contractTypeRepository.findAllContractTypes()));
		typeDtoMap.put(NodeType.CONTRACT_EXT, translateTypes(contractTypeRepository.findAllExtensionTypes()));
		typeDtoMap.put(NodeType.BILL, translateTypes(billTypeRepository.findAll()));
		return typeDtoMap;
	}

	private List<TypeDto> translateTypes(List<? extends Type> types) {
		List<Type> unproxiedTypeList = types.stream().map(EntityManagerUtils::initializeAndUnproxy)
				.collect(Collectors.toList());
		return DefaultDtoConverterUtils.translate(typeDtoTranslator, unproxiedTypeList);
	}

	private void completeNodeDtoPair(NodeDtoRootChildrenPair nodeDtoRootChildrenPair, NodeType nodeType,
			NumerationPattern numerationPattern) {
		Long typeId;
		switch (nodeType) {
		case CONTRACT:
		case CONTRACT_EXT:
			typeId = getTypeId(ContractNumerationPattern.class.cast(numerationPattern).getContractType());
			break;
		case BILL:
			typeId = getTypeId(BillNumerationPattern.class.cast(numerationPattern).getBillType());
			break;
		default:
			throw new BusinessException("Unsupported children node type");
		}
		NumerationPatternDto numerationPatternDto = numerationPatternDtoTranslator.translate(numerationPattern);
		if (typeId == null) {
			nodeDtoRootChildrenPair.getRootNodeData().setNumerationPatternDto(numerationPatternDto);
		} else {
			nodeDtoRootChildrenPair.getChildrenNodeData().stream()
					.filter(nodeData -> nodeData.getTypeDto().getId().equals(typeId)).findFirst()
					.ifPresent(nodeData -> nodeData.setNumerationPatternDto(numerationPatternDto));
		}
	}

	private Long getTypeId(Type type) {
		return type != null ? type.getId() : null;
	}

	@Getter
	@AllArgsConstructor
	public static class NodeDtoRootChildrenPair {
		private NumerationPatternNodeData rootNodeData;
		private List<NumerationPatternNodeData> childrenNodeData;
	}

	@Getter
	@AllArgsConstructor
	public enum NodeType {

		//@formatter:off
		CONTRACT			(Contract.class, Document.MAX_NUMBER_LENGTH, ContractType.class),
		CONTRACT_EXT		(ContractExtension.class, Document.MAX_NUMBER_LENGTH, ContractExtensionType.class),
		BILL				(Bill.class, Document.MAX_NUMBER_LENGTH, BillType.class),
		PERSONAL_ACCOUNT	(PersonalAccount.class, PersonalAccount.MAX_NUMBER_LENGTH, null),
		TASK				(Task.class, Task.MAX_NUMBER_LENGTH, null),
		ORDER				(Order.class, Order.MAX_NUMBER_LENGTH, null);
		//@formatter:on

		private Class<? extends Identifiable> clazz;
		private int maxNumberLength;
		private Class<? extends Identifiable> typeClass;

		public String getName() {
			NumerationMessagesBundle messages = LocaleUtils.getMessages(NumerationMessagesBundle.class);

			switch (this) {
			case CONTRACT:
				return messages.contract();
			case CONTRACT_EXT:
				return messages.contractExtension();
			case BILL:
				return messages.bill();
			case PERSONAL_ACCOUNT:
				return messages.personalAccount();
			case TASK:
				return messages.task();
			case ORDER:
				return messages.order();
			default:
				throw new SystemException("Unsupported NodeType");
			}
		}

		public static NodeType findByClassName(String className) {
			return Arrays.stream(NodeType.values()).filter(nodeType -> nodeType.getClazz().getName().equals(className))
					.findFirst().orElse(null);
		}

		public static boolean isNodeTypeWithChildren(NodeType nodeType) {
			return NodeType.nodeTypesWithChildren().contains(nodeType);
		}

		private static List<NodeType> nodeTypesWithChildren() {
			return Arrays.asList(CONTRACT, CONTRACT_EXT, BILL);
		}

	}

	private static final long serialVersionUID = 6734945370605666792L;
}
