package ru.argustelecom.box.env.contract.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

@Entity
@Access(AccessType.FIELD)
@Getter
@Setter
public class ContractType extends AbstractContractType {

	private static final long serialVersionUID = 4877774641668504398L;

	@Enumerated(EnumType.STRING)
	private ContractCategory contractCategory;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "provider_id")
	private PartyRole provider;

	protected ContractType() {
		super();
	}

	protected ContractType(Long id) {
		super(id);
	}

	public static class ContractTypeQuery<T extends ContractType> extends AbstractContractTypeQuery<T> {

		EntityQueryEntityFilter<T, PartyRole> provider;
		EntityQuerySimpleFilter<T, ContractCategory> contractCategory;

		public ContractTypeQuery(Class<T> entityClass) {
			super(entityClass);
			provider = createEntityFilter(ContractType_.provider);
			contractCategory = createFilter(ContractType_.contractCategory);
		}

		public EntityQueryEntityFilter<T, PartyRole> provider() {
			return provider;
		}

		public EntityQuerySimpleFilter<T, ContractCategory> contractCategory() {
			return contractCategory;
		}

	}
}
