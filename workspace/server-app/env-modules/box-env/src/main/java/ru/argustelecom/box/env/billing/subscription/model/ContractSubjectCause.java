package ru.argustelecom.box.env.billing.subscription.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;

@Entity
@Access(AccessType.FIELD)
public class ContractSubjectCause extends SubscriptionSubjectCause {

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "contract_entry_id")
	private ProductOfferingContractEntry contractEntry;

	protected ContractSubjectCause() {
	}

	public ContractSubjectCause(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return String.format("Договор: %s", contractEntry.getContract().getObjectName());
	}

	private static final long serialVersionUID = 6997356871385733104L;

}