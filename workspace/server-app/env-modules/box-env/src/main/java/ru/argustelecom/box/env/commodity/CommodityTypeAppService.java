package ru.argustelecom.box.env.commodity;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.type.model.TypePropertyFilterContainer;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class CommodityTypeAppService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CommodityTypeRepository commodityTypeRp;

	public List<ServiceType> findAllServiceTypes() {
		return commodityTypeRp.findAllServiceTypes();
	}

	public TypePropertyFilterContainer createServiceTypePropertyFilters() {
		return commodityTypeRp.createServiceTypePropertyFilters();
	}

	public void changeName(Long commodityTypeId, String name) {
		checkNotNull(commodityTypeId);
		checkNotNull(name);

		CommodityType commodityType = em.find(CommodityType.class, commodityTypeId);

		if (!commodityType.getName().equals(name)) {
			commodityType.setName(name);
		}
	}

	public void changeDescription(Long commodityTypeId, String description) {
		checkNotNull(commodityTypeId);

		CommodityType commodityType = em.find(CommodityType.class, commodityTypeId);

		if (!Objects.equals(commodityType.getDescription(), description)) {
			commodityType.setDescription(description);
		}
	}

	public void changeGroup(Long commodityTypeId, Long groupId) {
		checkNotNull(commodityTypeId);
		checkNotNull(groupId);

		CommodityType commodityType = em.find(CommodityType.class, commodityTypeId);
		CommodityTypeGroup newGroup = em.find(CommodityTypeGroup.class, groupId);

		if (!Objects.equals(commodityType.getGroup(), newGroup)) {
			commodityType.changeGroup(newGroup);
		}
	}

}
