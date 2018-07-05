package ru.argustelecom.box.env.billing.subscription;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.contract.ContractRepository;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.pricing.ProductOfferingRepository;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@ApplicationService
public class ProductAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ProductOfferingRepository productOfferingRp;

	@Inject
	private ContractRepository contractRp;

	public List<ContractEntry> findContractEntriesWithoutSubs(Long contractId) {
		return contractRp.findContractEntriesWithoutSubs(contractId);
	}

	public List<ProductOffering> findRecurrentProductsByPricelist(Long pricelistId) {
		AbstractPricelist pricelist = em.find(AbstractPricelist.class, pricelistId);
		return productOfferingRp.findProductOfferings(pricelist).stream().filter(ProductOffering::isRecurrentProduct)
				.map(EntityManagerUtils::initializeAndUnproxy).collect(Collectors.toList());
	}

	private static final long serialVersionUID = 6831817940994438743L;

}