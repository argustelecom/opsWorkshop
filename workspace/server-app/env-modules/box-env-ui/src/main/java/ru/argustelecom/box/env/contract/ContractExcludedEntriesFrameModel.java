package ru.argustelecom.box.env.contract;

import static java.util.stream.Collectors.toList;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.contract.dto.ContractEntryDto;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.system.inf.page.PresentationModel;

/**
 * Модель для ФБ "Позиции договора", должна использоваться в том случае если позиции в доп. соглашении исключаются.
 */
@Named(value = "contractExcludedEntriesFm")
@PresentationModel
public class ContractExcludedEntriesFrameModel extends AbstractContractEntriesFrameModel {

	private static final long serialVersionUID = -1826731903917766058L;

	@Inject
	private ContractEntryAppService contractEntryAs;

	@Override
	public void initEntries() {
		if (entries == null && getContract() != null) {
			entries = ((ContractExtension) getContract()).getExcludedEntries().stream()
					.map(contractEntryDtoTr::translate).collect(toList());
		}
	}

	@Override
	public void remove(ContractEntryDto entry) {
		contractEntryAs.removeExcludedEntry(getContract().getId(), entry.getId());
		entries.remove(entry);
	}

	@Override
	public boolean showAddExcludedEntryButton() {
		return !contractEntryAs.findEntriesThatCanBeExcluded(getContract().getId()).isEmpty();
	}

}