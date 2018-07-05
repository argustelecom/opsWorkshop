package ru.argustelecom.box.env.product;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static ru.argustelecom.box.inf.modelbase.MetadataUnit.MetadataUnitStatus.ACTIVE;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.GoodsType;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.product.model.AbstractProductType.AbstractProductTypeQuery;
import ru.argustelecom.box.env.product.model.ProductType;
import ru.argustelecom.box.env.product.model.ProductTypeComposite;
import ru.argustelecom.box.env.product.model.ProductTypeGroup;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@Repository
public class ProductTypeRepository implements Serializable {

	private static final long serialVersionUID = -6743373306999186924L;

	private static final String ALL_PRODUCT_TYPE_GROUPS = "ProductTypeRepository.getAllProductTypeGroups";
	private static final String FIND_ALL_CHILDREN = "ProductTypeRepository.findAllChildren";
	private static final String ALL_SIMPLE_PRODUCT_TYPES = "ProductTypeRepository.getAllSimpleProductTypes";
	private static final String POSSIBLE_COMMODITY_TYPES = "ProductTypeRepository.getPossibleCommodityTypes";
	private static final String ALL_PRODUCT_TYPES = "ProductTypeRepository.getAllProductTypes";
	private static final String FIND_PRODUCT_TYPES_WITH_COMMODITY_TYPES = "ProductTypeRepository.findProductTypesWithCommodityTypes";
	public static final String FIND_PRODUCT_TYPE_COMPOSITE_WITH_COMPOSITE_PARTS = "ProductTypeRepository.findProductTypeCompositeWithCompositeParts";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private TypeFactory typeFactory;

	public ProductType createProductType(@NotNull String name, String keyword, String description,
			@NotNull ProductTypeGroup group) {
		ProductType productType = typeFactory.createType(ProductType.class);
		return (ProductType) createProductType(name, keyword, description, group, productType);
	}

	public ProductTypeComposite createProductTypeComposite(@NotNull String name, String keyword, String description,
			@NotNull ProductTypeGroup group) {
		ProductTypeComposite productTypeComposite = typeFactory.createType(ProductTypeComposite.class);
		return (ProductTypeComposite) createProductType(name, keyword, description, group, productTypeComposite);
	}

	public ProductTypeGroup createProductTypeGroup(@NotNull String name, String description) {
		ProductTypeGroup productTypeGroup = new ProductTypeGroup(idSequence.nextValue(ProductTypeGroup.class));
		productTypeGroup.setObjectName(name);
		productTypeGroup.setDescription(description);
		em.persist(productTypeGroup);
		return productTypeGroup;
	}

	@NamedQuery(name = ALL_PRODUCT_TYPE_GROUPS, query = "from ProductTypeGroup")
	public List<ProductTypeGroup> getAllProductTypeGroups() {
		return em.createNamedQuery(ALL_PRODUCT_TYPE_GROUPS, ProductTypeGroup.class).getResultList();
	}

	@NamedQuery(name = FIND_ALL_CHILDREN, query = "from AbstractProductType p where p.group = :group and p.status = :status")
	public List<AbstractProductType> findAllChildren(ProductTypeGroup group) {
		return em.createNamedQuery(FIND_ALL_CHILDREN, AbstractProductType.class).setParameter("group", group)
				.setParameter("status", ACTIVE).getResultList();
	}

	@NamedQuery(name = ALL_PRODUCT_TYPES, query = "from AbstractProductType p where p.status = :status")
	public List<AbstractProductType> getAllProductTypes() {
		return em.createNamedQuery(ALL_PRODUCT_TYPES, AbstractProductType.class).setParameter("status", ACTIVE)
				.getResultList();
	}

	@NamedQuery(name = ALL_SIMPLE_PRODUCT_TYPES, query = "from ProductType ps where ps.status = :status")
	public List<ProductType> getAllSimpleProductTypes() {
		return em.createNamedQuery(ALL_SIMPLE_PRODUCT_TYPES, ProductType.class).setParameter("status", ACTIVE)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@NamedQuery(name = POSSIBLE_COMMODITY_TYPES, query = "from CommodityType c where c.status = :status and type(c) in (:types)")
	public List<CommodityType> getPossibleCommodityTypes() {
		return em.createNamedQuery(POSSIBLE_COMMODITY_TYPES, CommodityType.class).setParameter("status", ACTIVE)
				.setParameter("types", Lists.newArrayList(ServiceType.class, GoodsType.class)).getResultList();
	}

	/**
	 * Возвращает все специцикации продуктов, как простые, так и составные. Ищет сначала простые спецификации продуктов
	 * ({@link ProductType}), у которых есть все спецификации услуг/товаров. Потом ищет спецификации составных продуктов
	 * ({@link ProductTypeComposite}), у которых есть все спецификации услуг/товарови.
	 * 
	 * @param commodityTypes
	 *            список спецификаций услуг/товаров, которым должны обладать спецификации продуктов.
	 * @return список {@link AbstractProductType}
	 */
	//@formatter:off
	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = FIND_PRODUCT_TYPES_WITH_COMMODITY_TYPES, query =
					"SELECT ps.product_type_id " +
					"FROM (SELECT " +
					"        pe.product_type_id, " +
					"        array_agg(cs.commodity_type_id) AS commodities " +
					"      FROM system.product_type_entry pe, system.commodity_spec cs " +
					"      WHERE pe.commodity_spec_id = cs.id " +
					"      GROUP BY product_type_id) ps " +
					"WHERE CAST(string_to_array(:commodityTypeIds, ',') AS BIGINT []) <@ CAST(ps.commodities AS BIGINT []) " +
					"UNION ALL " +
					"SELECT psc.product_type_composite_id " +
					"FROM (SELECT " +
					"        pscp.product_type_composite_id, " +
					"        array_agg(cs.commodity_type_id) AS commodities " +
					"      FROM system.product_type_entry pe, system.product_type_composite_parts pscp, system.commodity_spec cs " +
					"      WHERE pe.product_type_id = pscp.product_type_part_id AND pe.commodity_spec_id = cs.id " +
					"      GROUP BY pscp.product_type_composite_id) psc " +
					"WHERE CAST(string_to_array(:commodityTypeIds, ',') AS BIGINT []) <@ CAST(psc.commodities AS BIGINT []) ")
	//@formatter:on
	public List<AbstractProductType> findProductTypeWithCommodityTypes(List<CommodityType> commodityTypes) {
		if (commodityTypes.isEmpty()) {
			return new ArrayList<>();
		}
		List<Long> commodityTypeIds = commodityTypes.stream().map(Type::getId).collect(Collectors.toList());
		List<BigInteger> productTypeIds = em.createNamedQuery(FIND_PRODUCT_TYPES_WITH_COMMODITY_TYPES)
				.setParameter("commodityTypeIds", aggregateCommodityTypeIds(commodityTypeIds)).getResultList();
		return EntityManagerUtils.findList(em, AbstractProductType.class,
				productTypeIds.stream().map(BigInteger::longValue).collect(Collectors.toList()));
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private String aggregateCommodityTypeIds(List<Long> commodityTypeIds) {
		if (commodityTypeIds.isEmpty())
			return EMPTY;
		StringBuilder paramValue = new StringBuilder("");
		for (Long id : commodityTypeIds) {
			paramValue.append(id).append(",");
		}
		return paramValue.replace(paramValue.length() - 1, paramValue.length(), "").toString();
	}

	private AbstractProductType createProductType(String name, String keyword, String description,
			ProductTypeGroup group, AbstractProductType productType) {
		productType.setName(name);
		productType.setKeyword(keyword);
		productType.setDescription(description);
		productType.setGroup(group);
		em.persist(productType);
		return productType;
	}

	public List<AbstractProductType> findProductTypeByName(String name) {
		AbstractProductTypeQuery<AbstractProductType> query = new AbstractProductTypeQuery<>(AbstractProductType.class);
		query.and(query.name().like(name));
		return query.getResultList(em);
	}
}