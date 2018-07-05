package ru.argustelecom.box.env.contract;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.Getter;
import ru.argustelecom.box.env.contract.dto.ContractEntryDto;
import ru.argustelecom.box.env.contract.dto.ContractEntryDtoTranslator;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.inf.util.Callback;

/**
 * Абстракная реализация для ФБ "Позиции договора", позиции могут быть как добавляемые, так и исключаемые. Конкретная
 * реализация с определённым типом позиций определяется в наследниках данного класса.
 */
public abstract class AbstractContractEntriesFrameModel implements Serializable {

	@Inject
	ContractEntryAppService contractEntryAs;

	@Inject
	ContractEntryDtoTranslator contractEntryDtoTr;

	@Getter
	private AbstractContract<?> contract;

	@Getter
	List<ContractEntryDto> entries;

	public void preRender(AbstractContract<?> contract) {
		if (!Objects.equals(this.contract, contract)) {
			this.contract = contract;
			initEntries();
		}
	}

	void initEntries() {
		if (entries == null && contract != null) {
			entries = contract.getEntries().stream().map(contractEntryDtoTr::translate).collect(Collectors.toList());
		}
	}

	public Callback<ContractEntryDto> getCallback() {
		return instance -> entries.add(instance);
	}

	public abstract void remove(ContractEntryDto entry);

	public boolean showAddExcludedEntryButton() {
		return false;
	}

	private static final long serialVersionUID = -2766612781037166493L;

}