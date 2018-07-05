package ru.argustelecom.box.env.numerationpattern.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contract.model.AbstractContractType;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

@Entity
@Access(AccessType.FIELD)
@Getter
@Setter
public class ContractNumerationPattern extends NumerationPattern {

	protected ContractNumerationPattern() {
	}

	public ContractNumerationPattern(Long id) {
		super(id);
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "contract_type_id")
	private AbstractContractType contractType;

	public static class ContractNumerationPatternQuery
			extends AbstractNumerationPatternQuery<ContractNumerationPattern> {

		EntityQueryEntityFilter<ContractNumerationPattern, AbstractContractType> contractTypeFilter = createEntityFilter(
				ContractNumerationPattern_.contractType);

		public ContractNumerationPatternQuery() {
			super(ContractNumerationPattern.class);
		}

		public EntityQueryEntityFilter<ContractNumerationPattern, AbstractContractType> contractType() {
			return contractTypeFilter;
		}
	}

	private static final long serialVersionUID = -4210790490056322978L;
}
