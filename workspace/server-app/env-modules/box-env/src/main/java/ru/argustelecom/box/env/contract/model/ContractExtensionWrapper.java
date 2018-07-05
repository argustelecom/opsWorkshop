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
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.crm.model.IContractEntry;
import ru.argustelecom.box.publang.crm.model.IContractExtension;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Named(IContractExtension.WRAPPER_NAME)
public class ContractExtensionWrapper implements EntityWrapper {
	
	private static final long serialVersionUID = -6896592241970960664L;
	
	@PersistenceContext
	private EntityManager em;

	@Inject
	private ContractEntryWrapper cew;

	@Inject
	private LocationWrapper lw;

	@Override
	public IContractExtension wrap(Identifiable entity) {
		checkNotNull(entity);
		ContractExtension contractExtension = (ContractExtension) entity;
		List<IContractEntry> entries = contractExtension.getEntries().stream().map(e -> cew.wrap(e)).collect(toList());
		List<IContractEntry> excludedEntries = contractExtension.getExcludedEntries().stream().map(e -> cew.wrap(e))
				.collect(toList());

		boolean firstAddressExist = contractExtension.getFirstAddressFromEntries() != null;

		//@formatter:off
		return IContractExtension.builder()
				.id(contractExtension.getId())
				.objectName(contractExtension.getObjectName())
				.documentNumber(contractExtension.getDocumentNumber())
				.documentDate(contractExtension.getDocumentDate())
				.creationDate(contractExtension.getCreationDate())
				.entries(entries)
				.customerId(contractExtension.getCustomer().getId())
				.orderId(contractExtension.getOrder() != null ? contractExtension.getOrder().getId() : null)
				.state(new IState(contractExtension.getState().toString(), contractExtension.getState().getName()))
				.address(firstAddressExist ? lw.wrap(contractExtension.getFirstAddressFromEntries()) : null)
				.validFrom(contractExtension.getValidFrom())
				.validTo(contractExtension.getValidTo())
				.contractId(contractExtension.getContract().getId())
				.excludedEntries(excludedEntries)
				.build();
		//@formatter:on
	}

	@Override
	public ContractExtension unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(ContractExtension.class, iEntity.getId());
	}
}