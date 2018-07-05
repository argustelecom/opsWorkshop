package ru.argustelecom.box.env.contract.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class ContractExtensionType extends AbstractContractType {

	private static final long serialVersionUID = 5673447280815605119L;

	protected ContractExtensionType() {
		super();
	}

	protected ContractExtensionType(Long id) {
		super(id);
	}

	public static class ContractExtensionTypeQuery<T extends ContractExtensionType> extends AbstractContractTypeQuery<T> {

		public ContractExtensionTypeQuery(Class<T> entityClass) {
			super(entityClass);
		}
	}
}
