package ru.argustelecom.box.env.contract;

import lombok.Getter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.primefaces.model.StreamedContent;

import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.order.OrderRepository;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.report.ReportItem;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.system.inf.page.PresentationModel;


@SuppressWarnings("Duplicates")
@PresentationModel
public class ContractAttributesFrameModel implements Serializable, HasExport {

	private static final long serialVersionUID = 3618980908868059835L;

	private static final Logger log = Logger.getLogger(ContractAttributesFrameModel.class);

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ContractCardGenerationAppService contractCardGenerationAs;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private OrderRepository orderRepository;

	@Inject
	private CurrentContract currentContract;

	@Getter
	private Contract contract;

	private List<Order> possibleOrders;

	@Getter
	private Function<ReportItem, StreamedContent> exportFnc;

	@PostConstruct
	protected void postConstruct() {
		ContractMessagesBundle contractMb = LocaleUtils.getMessages(ContractMessagesBundle.class);
		refresh();

		exportFnc = initExportFnc(contractCardGenerationAs, contract, contractMb.cannotGenerateReport());
	}

	public List<Order> getPossibleOrders() {
		if (possibleOrders == null) {
			possibleOrders = orderRepository.findOrders(contract.getCustomer());
		}
		return possibleOrders;
	}

	public String remove() {
		em.remove(contract);
		return outcomeConstructor.construct("/views/env/contract/ContractListView.xhtml");
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		checkNotNull(currentContract.getValue(), "currentContract required");
		if (currentContract.changed(contract)) {
			contract = currentContract.getValue();
			log.debugv("postConstruct. contract_id={0}", contract.getId());
		}
	}

}