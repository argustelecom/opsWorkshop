package ru.argustelecom.box.env.contract;

import javax.inject.Named;

import ru.argustelecom.box.env.contract.dto.ContractEntryDto;
import ru.argustelecom.system.inf.page.PresentationModel;

/**
 * Модель для ФБ "Позиции договора", должна использоваться в том случае если позиции в договор или доп. соглашения
 * добавляются.
 */
@Named(value = "contractEntriesFm")
@PresentationModel
public class ContractEntriesFrameModel extends AbstractContractEntriesFrameModel {

	@Override
	public void remove(ContractEntryDto entry) {
		contractEntryAs.removeEntry(entry.getId());
		entries.remove(entry);
	}

	private static final long serialVersionUID = 4068818731445854801L;

}