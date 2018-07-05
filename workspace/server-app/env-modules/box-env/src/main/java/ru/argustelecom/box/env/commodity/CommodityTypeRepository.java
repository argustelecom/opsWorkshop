package ru.argustelecom.box.env.commodity;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.CommodityType.CommodityTypeQuery;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup.CommodityTypeGroupQuery;
import ru.argustelecom.box.env.commodity.model.GoodsType;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.commodity.model.ServiceType.ServiceTypeQuery;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.env.type.model.TypePropertyFilterContainer;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@Repository
public class CommodityTypeRepository implements Serializable {

	private static final long serialVersionUID = 1616641354925268921L;

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private IdSequenceService idSequenceSvc;

	@Inject
	private TypeFactory typeFactory;

	public ServiceType createServiceType(String name, String keyword, CommodityTypeGroup group, String description) {
		checkNotNull(name);
		checkNotNull(group);

		ServiceType newType = typeFactory.createType(ServiceType.class);
		newType.setKeyword(keyword);
		newType.changeGroup(group);
		newType.setName(name);
		newType.setDescription(description);
		em.persist(newType);

		return newType;
	}

	public GoodsType createGoodsType(String name, String keyword, CommodityTypeGroup group, String description) {
		checkNotNull(name);
		checkNotNull(group);

		GoodsType newType = typeFactory.createType(GoodsType.class);
		newType.setKeyword(keyword);
		newType.changeGroup(group);
		newType.setName(name);
		newType.setDescription(description);
		em.persist(newType);

		return newType;
	}

	public CommodityTypeGroup createGroup(String name, String keyword, CommodityTypeGroup parent) {
		checkNotNull(name);

		CommodityTypeGroup instance = new CommodityTypeGroup(idSequenceSvc.nextValue(CommodityTypeGroup.class));

		instance.setName(name);
		instance.setKeyword(keyword);
		instance.changeParent(parent);

		em.persist(instance);
		em.flush();

		return instance;
	}

	private static final String FIND_PARENT_GROUPS = "CommodityTypeRepository.findParentGroups";

	//@formatter:off
	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = CommodityTypeRepository.FIND_PARENT_GROUPS, query =
			"WITH RECURSIVE parent_group(id, parent_id) AS ( " +
					"  SELECT" +
					"    id," +
					"    parent_id" +
					"  FROM system.commodity_type_group" +
					"  WHERE id = :first_parent_id" +
					"  UNION ALL" +
					"  SELECT" +
					"    tg.id," +
					"    tg.parent_id" +
					"  FROM system.commodity_type_group tg" +
					"    JOIN parent_group pg ON tg.id = pg.parent_id)" +
					"SELECT id " +
					"FROM parent_group")
	//@formatter:on
	public List<CommodityTypeGroup> findParentGroups(Long firstParentId) {
		checkNotNull(firstParentId);

		List<Object> objects = em.createNamedQuery(FIND_PARENT_GROUPS).setParameter("first_parent_id", firstParentId)
				.getResultList();
		List<Long> ids = objects.stream().map(o -> ((BigInteger) o).longValue()).collect(toList());
		return EntityManagerUtils.findList(em, CommodityTypeGroup.class, ids);
	}

	public CommodityType findTypeByKeyword(String keyword) {
		CommodityTypeQuery<CommodityType> query = new CommodityTypeQuery<>(CommodityType.class);
		query.and(query.keyword().equal(keyword));
		return query.getSingleResult(em, false);
	}

	public CommodityTypeGroup findGroupByKeyword(String keyword, boolean required) {
		CommodityTypeGroupQuery query = new CommodityTypeGroupQuery();
		query.and(query.keyword().equal(keyword));
		return query.getSingleResult(em, required);
	}

	public List<CommodityTypeGroup> findRootGroups() {
		CommodityTypeGroupQuery query = new CommodityTypeGroupQuery();
		query.and(query.parent().isNull());
		return query.getResultList(em);
	}

	public List<CommodityTypeGroup> findGroups() {
		return new CommodityTypeGroupQuery().getResultList(em);
	}

	public List<CommodityTypeGroup> findChildrenGroups(CommodityTypeGroup parentGroup) {
		CommodityTypeGroupQuery query = new CommodityTypeGroupQuery();
		query.and(query.parent().equal(parentGroup));
		return query.getResultList(em);
	}

	public List<CommodityType> findCommodityTypesByGroup(CommodityTypeGroup group) {
		CommodityTypeQuery<CommodityType> query = new CommodityTypeQuery<>(CommodityType.class);
		query.and(query.group().equal(group));
		return query.getResultList(em);
	}

	public List<ServiceType> findAllServiceTypes() {
		return new ServiceTypeQuery<>(ServiceType.class).getResultList(em);
	}

	public TypePropertyFilterContainer createServiceTypePropertyFilters() {
		return typeFactory.createFilterContainer(findAllServiceTypes());
	}
}
