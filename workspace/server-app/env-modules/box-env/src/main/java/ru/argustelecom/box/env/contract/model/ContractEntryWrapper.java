package ru.argustelecom.box.env.contract.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.address.model.LocationWrapper;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.ILocation;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.crm.model.IContractEntry;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Named(value = IContractEntry.WRAPPER_NAME)
public class ContractEntryWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private LocationWrapper lw;

	@Override
	public IContractEntry wrap(Identifiable entity) {
		checkNotNull(entity);
		ContractEntry contractEntry = (ContractEntry) entity;
		List<ILocation> locations = contractEntry.getLocations().stream().map(l -> lw.wrap(l)).collect(toList());

		//@formatter:off
		return IContractEntry.builder()
					.id(contractEntry.getId())
					.objectName(contractEntry.getObjectName())
					.contractId(contractEntry.getContract().getId())
					.pricelistEntryId(contractEntry instanceof ProductOfferingContractEntry ? ((ProductOfferingContractEntry)contractEntry).getProductOffering().getId() : null)
					.locations(locations)
				.build();
		//@formatter:on
	}

	@Override
	public ContractEntry unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(ContractEntry.class, iEntity.getId());
	}

	private static final long serialVersionUID = -5248212290367409218L;

}