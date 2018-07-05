package ru.argustelecom.box.env.commodity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.system.inf.dataloader.LazyTreeNodeLoader;

public class CommodityTypeLazyTreeNodeLoader implements LazyTreeNodeLoader<CommodityTypeTreeNodeDto> {

	private EntityManager em;
	private CommodityTypeRepository repository;
	private CommodityTypeTreeNodeDtoTranslator translator;

	public CommodityTypeLazyTreeNodeLoader(EntityManager em, CommodityTypeRepository repository,
			CommodityTypeTreeNodeDtoTranslator translator) {
		this.em = em;
		this.repository = repository;
		this.translator = translator;
	}

	@Override
	public List<CommodityTypeTreeNodeDto> loadChildren(CommodityTypeTreeNodeDto node) {
		return hasChildren(node) ? initChildren(node) : Collections.emptyList();
	}

	private boolean hasChildren(CommodityTypeTreeNodeDto node) {
		return node.getType().equals(CommodityTypeRef.GROUP);
	}

	private List<CommodityTypeTreeNodeDto> initChildren(CommodityTypeTreeNodeDto node) {
		CommodityTypeGroup group = em.find(CommodityTypeGroup.class, node.getId());

		List<CommodityType> types = repository.findCommodityTypesByGroup(group);
		List<CommodityTypeGroup> groups = repository.findChildrenGroups(group);

		List<CommodityTypeTreeNodeDto> nodes = new ArrayList<>();

		nodes.addAll(types.stream().map(translator::translate).collect(Collectors.toList()));
		nodes.addAll(groups.stream().map(translator::translate).collect(Collectors.toList()));

		return nodes;
	}

}