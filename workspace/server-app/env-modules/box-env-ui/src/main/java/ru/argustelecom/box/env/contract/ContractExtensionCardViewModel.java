package ru.argustelecom.box.env.contract;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import lombok.Getter;
import ru.argustelecom.box.env.contract.dto.ContractEntryDtoTranslator;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class ContractExtensionCardViewModel extends ViewModel {

	private static final long serialVersionUID = -8682154294470726564L;
	private static final Logger log = Logger.getLogger(ContractCardViewModel.class);

	public static final String VIEW_ID = "/views/env/contract/ContractExtensionCardView.xhtml";

	@Inject
	private CurrentContractExtension currentContractExtension;

	@Inject
	private ContractEntryDtoTranslator contractEntryDtoTr;

	@Getter
	private ContractExtension contractExtension;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		unitOfWork.makePermaLong();
	}

	private void refresh() {
		checkNotNull(currentContractExtension.getValue(), "currentContractExtension required");
		if (currentContractExtension.changed(contractExtension)) {
			contractExtension = currentContractExtension.getValue();
			log.debugv("postConstruct. contract_extension_id={0}", contractExtension.getId());
		}
	}

}