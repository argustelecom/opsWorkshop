package ru.argustelecom.box.env.contract.model;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationWrapper;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.crm.model.IContract;
import ru.argustelecom.box.publang.crm.model.IContractEntry;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

@Named(value = IContract.WRAPPER_NAME)
public class ContractWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ContractEntryWrapper cew;

	@Inject
	private LocationWrapper lw;

	@Override
	public IContract wrap(Identifiable entity) {
		checkNotNull(entity);
		Contract contract = (Contract) entity;
		List<IContractEntry> entries = contract.getEntries().stream().map(e -> cew.wrap(e)).collect(toList());
		List<IContractEntry> finalEntries = contract.getFinalEntries().stream().map(e -> cew.wrap(e)).collect(toList());
		Location address = contract.getFirstAddressFromEntries();
		
		//@formatter:off
		return IContract.builder()
					.id(contract.getId())
					.objectName(contract.getObjectName())
					.documentNumber(contract.getDocumentNumber())
					.documentDate(contract.getDocumentDate())
					.creationDate(contract.getCreationDate())
					.entries(entries)
					.customerId(contract.getCustomer().getId())
					.orderId(contract.getOrder() != null ? contract.getOrder().getId() : null)
					.state(new IState(contract.getState().toString(), contract.getState().getName()))
					.address(address != null ? lw.wrap(address) : null)
					.validFrom(contract.getValidFrom())
					.validTo(contract.getValidTo())
					.finalEntries(finalEntries)
				.build();
		//@formatter:on
	}

	@Override
	public Contract unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(Contract.class, iEntity.getId());
	}

	private static final long serialVersionUID = -1192316049390503020L;

}