package ru.argustelecom.box.env.pricing;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.inf.nls.LocaleUtils.format;

import java.io.Serializable;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.billing.provision.model.NonRecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasuredValue;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.MeasuredProductOffering;
import ru.argustelecom.box.env.pricing.model.PeriodProductOffering;
import ru.argustelecom.box.env.pricing.model.PricelistState;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.pricing.model.ProductOffering.ProductOfferingQuery;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.exception.BusinessException;

@Repository
public class ProductOfferingRepository implements Serializable {

	private static final long serialVersionUID = 1110293154930447996L;

	private static final String NON_RECURRENT_PRODUCT_ENTRIES = "ProductOfferingRepository.getNonRecurrentProductEntries";
	private static final String FIND_PRODUCT_OFFERINGS_BY_PRODUCT_TYPES = "ProductOfferingRepository.findProductOfferingsByProductTypes";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public MeasuredProductOffering createMeasuredProductOffering(AbstractPricelist pricelist,
			AbstractProductType productType, Money price, Currency currency, Long amount, MeasureUnit measureUnit,
			NonRecurrentTerms nonRecurrentTerms) {
		checkNotNull(amount, "Amount is required for measured product offering creation");
		checkNotNull(measureUnit, "Measure unit is required for measured product offering creation");

		//@formatter:off
		MeasuredProductOffering offering =
				MeasuredProductOffering.builder()
					.id(idSequence.nextValue(PeriodProductOffering.class))
					.pricelist(pricelist)
					.orderNum(nextOrderNum(pricelist))
					.productType(productType)
					.provisionTerms(nonRecurrentTerms)
					.price(price)
					.currency(currency)
					.volume(new MeasuredValue(amount, measureUnit))
				.build();
		//@formatter:on

		em.persist(offering);
		return offering;
	}

	public PeriodProductOffering createPeriodProductOffering(AbstractPricelist pricelist,
			AbstractProductType productType, Money price, Currency currency, Long amount, PeriodUnit periodUnit,
			RecurrentTerms recurrentTerms) {
		checkNotNull(amount, "Amount is required for period product offering creation");
		checkNotNull(periodUnit, "Period unit is required for period product offering creation");

		validatePeriodVolume(PeriodDuration.of(Math.toIntExact(amount), periodUnit), recurrentTerms);

		//@formatter:off
		PeriodProductOffering offering =
				PeriodProductOffering.builder()
					.id(idSequence.nextValue(PeriodProductOffering.class))
					.pricelist(pricelist)
					.orderNum(nextOrderNum(pricelist))
					.productType(productType)
					.provisionTerms(recurrentTerms)
					.price(price)
					.currency(currency)
					.volume(PeriodDuration.of(Math.toIntExact(amount), periodUnit))
				.build();
		//@formatter:on

		em.persist(offering);
		return offering;
	}

	private void validatePeriodVolume(PeriodDuration accountingDuration, RecurrentTerms provisionTerms) {
		if (!provisionTerms.getPeriodType().isSupportedAccountingPeriodUnit(accountingDuration.getUnit()))
			throw new BusinessException(format(
					"Единица измерения периода {0} не поддерживается текущим типом периода {1}: {2}",
					accountingDuration.getUnit(), provisionTerms.getPeriodType(), provisionTerms.getPeriodType()));
		if (!accountingDuration.greaterOrEquals(provisionTerms.getChargingDuration()))
			throw new BusinessException(format("Период списания не может превышать период расчета: {0} > {1}",
					provisionTerms.getChargingDuration(), accountingDuration));
		if (!accountingDuration.getUnit().greaterOrEquals(provisionTerms.getChargingDuration().getUnit()))
			throw new BusinessException(
					format("Единица измерения периода списания не может превышать единицу измерения периода расчета: {0} > {1}",
							provisionTerms.getChargingDuration().getUnit(), accountingDuration.getUnit()));
	}

	public void removeProductOffering(ProductOffering offering) {
		checkNotNull(offering);
		em.remove(offering);
	}

	public List<ProductOffering> findProductOfferings(@NotNull AbstractPricelist pricelist) {
		ProductOfferingQuery<ProductOffering> query = new ProductOfferingQuery<>(ProductOffering.class);
		query.and(query.pricelist().equal(pricelist));
		return query.getResultList(em);
	}

	//@formatter:off
	@NamedQuery(name = FIND_PRODUCT_OFFERINGS_BY_PRODUCT_TYPES, query
			= " from ProductOffering po "
			+ "where po.productType in (:productTypes) "
			+"   and po.pricelist.state = :state "
			+ "  and po.pricelist.validFrom < :currentDate "
			+ "  and po.pricelist.validTo > :currentDate")
	public List<ProductOffering> findProductOfferingsByProductTypes(List<AbstractProductType> productTypes) {
		return em.createNamedQuery(FIND_PRODUCT_OFFERINGS_BY_PRODUCT_TYPES, ProductOffering.class)
				.setParameter("productTypes", productTypes)
				.setParameter("state", PricelistState.INFORCE)
				.setParameter("currentDate", new Date())
				.getResultList();
	}

	@NamedQuery(name = NON_RECURRENT_PRODUCT_ENTRIES, query 
			= " from MeasuredProductOffering po "
			+ "where po.pricelist = :pricelist")
	public List<MeasuredProductOffering> getNonRecurrentProductEntries(AbstractPricelist pricelist) {
		return em.createNamedQuery(NON_RECURRENT_PRODUCT_ENTRIES, MeasuredProductOffering.class)
				.setParameter("pricelist", pricelist)
				.getResultList();
	}
	//@formatter:on

	// FIXME не уверен, что правильно написан метод. надо либо делать потоко-безопасный counter с локами таблицы в БД,
	// либо sequence. Последнее кажется проще и правильнее.
	private int nextOrderNum(AbstractPricelist pricelist) {
		List<ProductOffering> offerings = findProductOfferings(pricelist);
		int nextOrderNum = !offerings.isEmpty()
				? offerings.stream().mapToInt(ProductOffering::getOrderNum).max().getAsInt() : 0;
		return nextOrderNum + 1;
	}

}