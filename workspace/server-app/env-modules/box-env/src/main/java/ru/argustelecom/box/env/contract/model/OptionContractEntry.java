package ru.argustelecom.box.env.contract.model;

import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.commodity.model.Commodity;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.system.inf.exception.SystemException;

@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OptionContractEntry extends ContractEntry {

	@Override
	public Type getContractItem() {
		return getOptions().stream().findFirst().map(Commodity::getType)
				.orElseThrow(() -> new SystemException("OptionContractEntry without a option instance"));
	}

	@Override
	public String getObjectName() {
		return String.format("%s (%s)", getContractItem().getObjectName(), getContract().getDocumentNumber());
	}

	public OptionContractEntry(Long id) {
		super(id);
	}

	@Override
	public ContractEntryRdo createReportData() {
		//@formatter:off
		OptionContractEntryRdo rdo = OptionContractEntryRdo.builder()
				.id(getId())
				.name(getOptions().stream().map(Commodity::getObjectName).collect(Collectors.joining(", ")))
			.build();
		//@formatter:on

		return rdo;
	}

	private static final long serialVersionUID = -4739912404438972376L;

}