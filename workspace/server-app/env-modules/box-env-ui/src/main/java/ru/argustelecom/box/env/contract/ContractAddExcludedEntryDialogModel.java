package ru.argustelecom.box.env.contract;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contract.dto.ContractEntryDto;
import ru.argustelecom.box.env.contract.dto.ContractEntryDtoTranslator;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

/**
 * Модель для диалога выбора исключаемой позиции в доп. соглашении.
 */
@Named(value = "contractAddExcludedEntryDm")
@PresentationModel
public class ContractAddExcludedEntryDialogModel implements Serializable {

	private static final long serialVersionUID = -3322235042059442704L;

	@Inject
	private ContractEntryAppService contractEntryAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private ContractEntryDtoTranslator contractEntryDtoTr;

	@Getter
	@Setter
	private ContractExtension contractExtension;

	@Setter
	private Callback<ContractEntryDto> callback;

	@Getter
	@Setter
	private BusinessObjectDto<ContractEntry> excludedEntry;

	@Getter
	private List<BusinessObjectDto<ContractEntry>> entriesForExclude;

	private ContractMessagesBundle contractMb;

	@PostConstruct
	protected void postConstruct() {
		contractMb = LocaleUtils.getMessages(ContractMessagesBundle.class);
	}

	public void onCreationDialogOpen() {
		initEntriesForExclude();

		if (entriesForExclude.isEmpty()) {
			Notification.error(contractMb.cannotAddEntry(), contractMb.noEntryToExclude());
		} else {
			RequestContext.getCurrentInstance().update("contract_excluded_entry_addition_dlg");
			RequestContext.getCurrentInstance().execute("PF('contractExcludedEntryAdditionDlgVar').show()");
		}
	}

	public void addEntry() {
		contractExtension.addExcludedEntry(excludedEntry.getIdentifiable());
		callback.execute(contractEntryDtoTr.translate(excludedEntry.getIdentifiable()));
	}

	public boolean hasEntriesForExclude() {
		if (entriesForExclude == null) {
			initEntriesForExclude();
		}
		return !entriesForExclude.isEmpty();
	}

	private void initEntriesForExclude() {
		entriesForExclude = businessObjectDtoTr
				.translate(contractEntryAs.findEntriesThatCanBeExcluded(contractExtension.getId()));
	}

}