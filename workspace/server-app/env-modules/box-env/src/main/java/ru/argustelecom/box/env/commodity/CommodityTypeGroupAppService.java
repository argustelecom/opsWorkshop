package ru.argustelecom.box.env.commodity;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class CommodityTypeGroupAppService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CommodityTypeRepository commodityTypeRp;

	public void changeName(Long groupId, String name) {
		checkNotNull(groupId);
		checkNotNull(name);

		CommodityTypeGroup group = em.find(CommodityTypeGroup.class, groupId);

		if (!group.getName().equals(name)) {
			group.setName(name);
		}
	}

	public void changeParent(Long groupId, Long parentGroupId) {
		checkNotNull(groupId);

		CommodityTypeGroup group = em.find(CommodityTypeGroup.class, groupId);
		CommodityTypeGroup newParent = null;
		if (parentGroupId != null) {
			newParent = em.find(CommodityTypeGroup.class, parentGroupId);
		}

		if (!Objects.equals(group.getParent(), newParent)) {
			group.changeParent(newParent);
		}
	}

	public void changeKeyword(Long groupId, String keyword) {
		checkNotNull(groupId);

		CommodityTypeGroup group = em.find(CommodityTypeGroup.class, groupId);

		if (!Objects.equals(group.getKeyword(), keyword)) {
			group.setKeyword(keyword);
		}
	}

	public List<CommodityTypeGroup> findAll() {
		return commodityTypeRp.findGroups();
	}

	public List<CommodityTypeGroup> findParentGroups(Long firstParentId) {
		if (firstParentId == null) {
			return Collections.emptyList();
		}
		return commodityTypeRp.findParentGroups(firstParentId);
	}

}