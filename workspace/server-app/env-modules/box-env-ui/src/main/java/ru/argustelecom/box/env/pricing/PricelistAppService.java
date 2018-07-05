package ru.argustelecom.box.env.pricing;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;

import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.CommonPricelist;
import ru.argustelecom.box.env.pricing.model.CustomPricelist;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class PricelistAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private PricelistRepository pricelistRp;

	public List<CommonPricelist> findCommonPricelists(Long customerId) {
		Customer customer = em.find(Customer.class, customerId);
		return pricelistRp.findCommonPricelistsFor(customer);
	}

	public List<CustomPricelist> findCustomPricelists(Long customerId) {
		Customer customer = em.find(Customer.class, customerId);
		return pricelistRp.findCustomPricelistsFor(customer);
	}

	public void changeName(Long id, String name) {
		checkNotNull(id);
		checkArgument(StringUtils.isNotBlank(name));

		AbstractPricelist pricelist = em.find(AbstractPricelist.class, id);

		if (!name.equals(pricelist.getObjectName())) {
			pricelist.setObjectName(name);
		}
	}

	public void changeOwner(Long id, Long ownerId) {
		checkNotNull(id);
		checkNotNull(ownerId);

		AbstractPricelist pricelist = em.find(AbstractPricelist.class, id);
		Owner owner = em.find(Owner.class, ownerId);

		if (!ownerId.equals(pricelist.getOwner().getId())) {
			pricelist.setOwner(owner);
		}
	}

	public void changeDate(Long id, Date validFrom, Date validTo) {
		checkNotNull(id);
		checkNotNull(validFrom);

		AbstractPricelist pricelist = em.find(AbstractPricelist.class, id);

		if (!validFrom.equals(pricelist.getValidFrom())) {
			pricelist.setValidFrom(validFrom);
		}

		pricelist.setValidTo(validTo);
	}

	public List<AbstractPricelist> findActivePricelistsWithNonRecurrentProductsAndSuitableForCustomers(Date poi,
																									   Long customerId) {
		checkNotNull(poi);
		checkNotNull(customerId);

		Customer customer = em.find(Customer.class, customerId);
		checkNotNull(customer);

		return pricelistRp.findActivePricelistsWithNonRecurrentProductsAndSuitableForCustomers(poi, customer);
	}

	private static final long serialVersionUID = -6982319208189710650L;

}