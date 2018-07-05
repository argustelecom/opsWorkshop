package ru.argustelecom.box.env.pricing;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.pricing.model.PricelistState.CANCELLED;
import static ru.argustelecom.box.env.pricing.model.PricelistState.CREATED;
import static ru.argustelecom.box.env.pricing.model.PricelistState.INFORCE;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.billing.provision.model.NonRecurrentTerms;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist.PricelistQuery;
import ru.argustelecom.box.env.pricing.model.CommonPricelist;
import ru.argustelecom.box.env.pricing.model.CommonPricelist.CommonPricelistQuery;
import ru.argustelecom.box.env.pricing.model.CustomPricelist;
import ru.argustelecom.box.env.pricing.model.CustomPricelist.CustomPricelistQuery;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@Repository
public class PricelistRepository implements Serializable {

	private static final long serialVersionUID = 7285308980039835924L;

	private static final String NON_RECURRENT_PRODUCT_ENTRIES = "PricelistRepository.getNonRecurrentProductEntries";
	private static final String FIND_PRICELIST_ENTRIES_BY_PRODUCT_TYPES = "PricelistRepository.findPricelistEntriesByProductTypes";
	private static final String FIND_CUSTOM_PRICELISTS_FOR = "PricelistRepository.findCustomPricelistsFor";
	private static final String FIND_COMMON_PRICELISTS_FOR = "PricelistRepository.findCommonPricelistsFor";
	public static final String FIND_ACTIVE_PRICELISTS_WITH_RECURRENT_PRODUCTS_AND_SUITABLE_FOR_CUSTOMER = "PricelistRepository.findActivePricelistsWithRecurrentProductsAndSuitableForCustomer";
	public static final String FIND_ACTIVE_PRICELISTS_WITH_NONRECURRENT_PRODUCTS_AND_SUITABLE_FOR_CUSTOMER = "PricelistRepository.findActivePricelistsWithNonRecurrentProductsAndSuitableForCustomer";

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private ProductOfferingRepository productOfferingRp;

	@Inject
	private IdSequenceService idSequence;

	public CommonPricelist createCommonPricelist(String name, Date validFrom, Date validTo,
			List<CustomerSegment> segments, Owner owner) {

		checkRequiredArgument(name, "name");
		checkRequiredArgument(validFrom, "validFrom");

		Long newPricelistId = idSequence.nextValue(CommonPricelist.class);

		CommonPricelist newPricelist = new CommonPricelist(newPricelistId, owner);
		newPricelist.setObjectName(name);
		newPricelist.setValidFrom(validFrom);
		newPricelist.setValidTo(validTo);
		newPricelist.setState(CREATED);
		segments.forEach(newPricelist::addCustomerSegment);

		em.persist(newPricelist);
		return newPricelist;
	}

	public CustomPricelist createCustomPricelist(String name, Date validFrom, Date validTo, Customer customer,
			Owner owner) {

		checkRequiredArgument(name, "name");
		checkRequiredArgument(validFrom, "validFrom");

		Long newPricelistId = idSequence.nextValue(CustomPricelist.class);

		CustomPricelist newPricelist = new CustomPricelist(newPricelistId, customer, owner);
		newPricelist.setObjectName(name);
		newPricelist.setValidFrom(validFrom);
		newPricelist.setValidTo(validTo);
		newPricelist.setState(CREATED);

		em.persist(newPricelist);
		return newPricelist;
	}

	public void removePriceList(@NotNull AbstractPricelist pricelist) {
		checkState(CANCELLED.equals(pricelist.getState()));
		checkState(productOfferingRp.findProductOfferings(pricelist).isEmpty());

		em.remove(pricelist);
	}

	public List<AbstractPricelist> getAllPricelists() {
		return new PricelistQuery<>(AbstractPricelist.class).getResultList(em);
	}

	public List<CommonPricelist> findAllCommonPricelists() {
		return new CommonPricelistQuery().getResultList(em);
	}

	public List<CustomPricelist> findAllCustomPricelists() {
		return new CustomPricelistQuery().getResultList(em);
	}

	//@formatter:off
	
	@NamedQuery(
		name  = FIND_COMMON_PRICELISTS_FOR, 
		query = "select pl "
			  + "  from CommonPricelist pl "
			  + "       join pl.customerSegments cs "
			  + " where cs.customerType = :customerType"
	)
	public List<CommonPricelist> findCommonPricelistsFor(CustomerType customerType) {
		return em.createNamedQuery(FIND_COMMON_PRICELISTS_FOR, CommonPricelist.class)
				.setParameter("customerType", customerType)
				.getResultList();
	}
	
	public List<CommonPricelist> findCommonPricelistsFor(Customer customer) {
		return findCommonPricelistsFor(customer.getTypeInstance().getType());
	}
	

	@NamedQuery(
		name  = FIND_CUSTOM_PRICELISTS_FOR, 
		query = " from CustomPricelist pl "
			  + "where pl.customer = :customer"
	)
	public List<CustomPricelist> findCustomPricelistsFor(Customer customer) {
		return em.createNamedQuery(FIND_CUSTOM_PRICELISTS_FOR, CustomPricelist.class)
				.setParameter("customer", customer)
				.getResultList();
	}

	/**
	 * Возвращает все прайс-листы, которые подходят под условия фильтрации:
	 * <ul>
	 *     <li>находятся в состоянии {@linkplain ru.argustelecom.box.env.pricing.model.PricelistState#INFORCE действует}</li>
	 *     <li>точка интереса лежит внутри периода действия</li>
	 *     <li>прайс лист подходит для клиента через сегмент или напрямую</li>
	 *     <li>в прайс листе есть продуктовые предложения, предоставляющиеся на единовременной основе</li>
	 * </ul>
	 */
	//@formatter:off
	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = FIND_ACTIVE_PRICELISTS_WITH_NONRECURRENT_PRODUCTS_AND_SUITABLE_FOR_CUSTOMER,
			query = "SELECT DISTINCT pl.id " +
					"FROM system.pricelist pl " +
					"  JOIN system.product_offering po ON pl.id = po.pricelist_id " +
					"  JOIN system.provision_terms pt ON po.provision_terms_id = pt.id " +
					"  LEFT JOIN system.pricelist_customer_segments pcs ON pl.id = pcs.pricelist_id " +
					"  LEFT JOIN system.customer_segment cs ON pcs.segment_id = cs.id " +
					"WHERE " +
					"  :poi >= pl.valid_from AND (pl.valid_to IS NULL OR :poi <= pl.valid_to) " +
					"  AND pl.state = 'INFORCE' " +
					"  AND pt.dtype = 'NonRecurrentTerms' " +
					"  AND (cs.customer_type_id = :customer_type_id OR cs.customer_type_id IS NULL) " +
					"  AND (pl.customer_id = :customer_id OR pl.customer_id IS NULL) "
	)
	//@formatter:on
	public List<AbstractPricelist> findActivePricelistsWithNonRecurrentProductsAndSuitableForCustomers(Date poi,
			Customer customer) {
		List<BigInteger> ids = em
				.createNamedQuery(FIND_ACTIVE_PRICELISTS_WITH_NONRECURRENT_PRODUCTS_AND_SUITABLE_FOR_CUSTOMER)
				.setParameter("poi", poi).setParameter("customer_id", customer.getId())
				.setParameter("customer_type_id", customer.getTypeInstance().getType().getId()).getResultList();
		List<Long> idsLongValue = ids.stream().map(BigInteger::longValue).collect(Collectors.toList());
		return EntityManagerUtils.findList(em, AbstractPricelist.class, idsLongValue);
	}

	/**
	 * Возвращает все прайс-листы, которые подходят под условия фильтрации:
	 * <ul>
	 * <li>находятся в состоянии {@linkplain ru.argustelecom.box.env.pricing.model.PricelistState#INFORCE
	 * действует}</li>
	 * <li>точка интереса лежит внутри периода действия</li>
	 * <li>прайс лист подходит для клиента через сегмент или напрямую</li>
	 * <li>прайс лист принадлежит указанной компании-владельцу</li>
	 * <li>в прайс листе есть продуктовые предложения, предоставляющиеся на переодической основе</li>
	 * </ul>
	 */
	//@formatter:off
	@SuppressWarnings("unchecked")
	@NamedNativeQuery(name = FIND_ACTIVE_PRICELISTS_WITH_RECURRENT_PRODUCTS_AND_SUITABLE_FOR_CUSTOMER,
			query = "SELECT DISTINCT pl.id " +
					"FROM system.pricelist pl " +
					"  JOIN system.product_offering po ON pl.id = po.pricelist_id " +
					"  JOIN system.provision_terms pt ON po.provision_terms_id = pt.id " +
					"  LEFT JOIN system.pricelist_customer_segments pcs ON pl.id = pcs.pricelist_id " +
					"  LEFT JOIN system.customer_segment cs ON pcs.segment_id = cs.id " +
					"WHERE " +
					"  :poi >= pl.valid_from AND (pl.valid_to IS NULL OR :poi <= pl.valid_to) " +
					"  AND pl.state = 'INFORCE' " +
					"  AND pt.dtype = 'RecurrentTerms' " +
					"  AND (cs.customer_type_id = :customer_type_id OR cs.customer_type_id IS NULL) " +
					"  AND (pl.customer_id = :customer_id OR pl.customer_id IS NULL) " +
					"  AND pl.owner_id = :owner_id"
	)
	//@formatter:on
	public List<AbstractPricelist> findActivePricelistsWithRecurrentProductsAndSuitableForCustomer(Date poi,
			Customer customer, Owner owner) {
		List<BigInteger> ids = em
				.createNamedQuery(FIND_ACTIVE_PRICELISTS_WITH_RECURRENT_PRODUCTS_AND_SUITABLE_FOR_CUSTOMER)
				.setParameter("poi", poi).setParameter("customer_id", customer.getId())
				.setParameter("customer_type_id", customer.getTypeInstance().getType().getId())
				.setParameter("owner_id", owner.getId()).getResultList();
		List<Long> idsLongValue = ids.stream().map(BigInteger::longValue).collect(Collectors.toList());
		return EntityManagerUtils.findList(em, AbstractPricelist.class, idsLongValue);
	}

	@NamedQuery(name = FIND_PRICELIST_ENTRIES_BY_PRODUCT_TYPES, query = " from ProductOffering po "
			+ "where po.productType in (:productTypes) " + "  and po.pricelist.state     = :state "
			+ "  and po.pricelist.validTo   > :currentDate " + "  and po.pricelist.validFrom < :currentDate ")
	public List<ProductOffering> findPricelistEntriesByProductTypes(List<AbstractProductType> productTypes) {
		return em.createNamedQuery(FIND_PRICELIST_ENTRIES_BY_PRODUCT_TYPES, ProductOffering.class)
				.setParameter("productTypes", productTypes).setParameter("state", INFORCE)
				.setParameter("currentDate", new Date()).getResultList();
	}

	@NamedQuery(name = NON_RECURRENT_PRODUCT_ENTRIES, query = " from ProductOffering po "
			+ "where po.pricelist            = :pricelist " + "  and type(po.provisionTerms) = :className ")
	public List<ProductOffering> getNonRecurrentProductEntries(AbstractPricelist pricelist) {
		return em.createNamedQuery(NON_RECURRENT_PRODUCT_ENTRIES, ProductOffering.class)
				.setParameter("pricelist", pricelist).setParameter("className", NonRecurrentTerms.class.getSimpleName())
				.getResultList();
	}

}