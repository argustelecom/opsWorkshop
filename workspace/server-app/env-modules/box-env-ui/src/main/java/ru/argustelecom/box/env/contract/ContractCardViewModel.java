package ru.argustelecom.box.env.contract;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import lombok.Getter;
import ru.argustelecom.box.env.contract.dto.ContractEntryDto;
import ru.argustelecom.box.env.contract.dto.ContractEntryDtoTranslator;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class ContractCardViewModel extends ViewModel {

	private static final long serialVersionUID = 8390531389739870923L;

	private static final Logger log = Logger.getLogger(ContractCardViewModel.class);

	public static final String VIEW_ID = "/views/env/contract/ContractCardView.xhtml";

	@Inject
	private CurrentContract currentContract;

	@Inject
	private ContractEntryDtoTranslator contractEntryDtoTr;

	@Getter
	private Contract contract;

	private List<ContractEntryDto> entries;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		unitOfWork.makePermaLong();
	}

	private void refresh() {
		checkNotNull(currentContract.getValue(), "currentContract required");
		if (currentContract.changed(contract)) {
			contract = currentContract.getValue();
			log.debugv("postConstruct. contract_id={0}", contract.getId());
		}
	}

}