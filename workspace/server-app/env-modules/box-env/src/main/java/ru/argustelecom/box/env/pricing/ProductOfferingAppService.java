package ru.argustelecom.box.env.pricing;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Currency;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.provision.model.NonRecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasuredValue;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.MeasuredProductOffering;
import ru.argustelecom.box.env.pricing.model.PeriodProductOffering;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.privilege.model.PrivilegeType;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class ProductOfferingAppService implements Serializable {

	private static final long serialVersionUID = -5742149466873356293L;

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private ProductOfferingRepository productOfferingRp;

	public MeasuredProductOffering createMeasureProductOffering(Long pricelistId, Long productTypeId, Long amount,
			Long measureUnitId, Long provisionTermsId, Money price, Currency currency) {
		AbstractPricelist pricelist = em.find(AbstractPricelist.class, pricelistId);
		AbstractProductType productType = em.find(AbstractProductType.class, productTypeId);
		MeasureUnit measureUnit = em.find(MeasureUnit.class, measureUnitId);
		NonRecurrentTerms nonRecurrentTerms = em.find(NonRecurrentTerms.class, provisionTermsId);
		return productOfferingRp.createMeasuredProductOffering(pricelist, productType, price, currency, amount,
				measureUnit, nonRecurrentTerms);
	}

	public PeriodProductOffering createPeriodProductOffering(Long pricelistId, Long productTypeId, Long amount,
			PeriodUnit periodUnit, Long provisionTermsId, Money price, Currency currency) {
		AbstractPricelist pricelist = em.find(AbstractPricelist.class, pricelistId);
		AbstractProductType productType = em.find(AbstractProductType.class, productTypeId);
		RecurrentTerms recurrentTerms = em.find(RecurrentTerms.class, provisionTermsId);
		return productOfferingRp.createPeriodProductOffering(pricelist, productType, price, currency, amount,
				periodUnit, recurrentTerms);
	}

	public void changeProductOffering(Long productOfferingId, Long productTypeId, Money price, Currency currency) {
		ProductOffering offering = em.find(ProductOffering.class, productOfferingId);
		offering.changeOffering(em.find(AbstractProductType.class, productTypeId), price, currency);
	}

	public void changeMeasureVolume(Long productOfferingId, Long amount, Long measureUnitId) {
		MeasuredProductOffering offering = em.find(MeasuredProductOffering.class, productOfferingId);
		offering.changeVolume(new MeasuredValue(amount, em.find(MeasureUnit.class, measureUnitId)));
	}

	public void changePeriodVolume(Long recurrentTermsId, Long productOfferingId, Long amount, PeriodUnit periodUnit) {
		RecurrentTerms recurrentTerms = em.find(RecurrentTerms.class, recurrentTermsId);
		PeriodProductOffering offering = em.find(PeriodProductOffering.class, productOfferingId);
		offering.changeTerms(recurrentTerms);
		offering.changeVolume(PeriodDuration.of(Math.toIntExact(amount), periodUnit));
	}

	/**
	 * Изменяет настройки для привилегии, предоставляемой в рамках {@linkplain PeriodProductOffering продуктового
	 * предложения}.
	 */
	public void changePrivilegeParams(Long periodProductOfferingId, PrivilegeType type, Integer amount,
			PeriodUnit unit) {
		PeriodProductOffering productOffering = em.find(PeriodProductOffering.class, periodProductOfferingId);
		checkNotNull(productOffering);

		boolean hasAllPrivilegeParams = type != null && amount != null && unit != null;
		if (hasAllPrivilegeParams) {
			productOffering.setPrivilegeParams(type, amount, unit);
		} else {
			productOffering.removePrivilegeParams();
		}
	}

	public void remove(Long productOfferingId) {
		productOfferingRp.removeProductOffering(em.find(ProductOffering.class, productOfferingId));
	}

	public List<ProductOffering> findAllBy(Long pricelistId) {
		return productOfferingRp.findProductOfferings(em.find(AbstractPricelist.class, pricelistId));
	}

	public List<MeasuredProductOffering> getNonRecurrentProductEntries(Long pricelistId) {
		checkNotNull(pricelistId);

		AbstractPricelist pricelist = em.find(AbstractPricelist.class, pricelistId);
		checkNotNull(pricelist);

		return productOfferingRp.getNonRecurrentProductEntries(pricelist);
	}

}