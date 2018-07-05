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

import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.order.OrderRepository;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.report.ReportItem;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ContractExtensionAttributesFrameModel implements Serializable, HasExport {

	private static final long serialVersionUID = 6543341482320139892L;

	private static final Logger log = Logger.getLogger(ContractExtensionAttributesFrameModel.class);

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ContractCardGenerationAppService contractCardGenerationAs;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private OrderRepository orderRepository;

	@Inject
	private CurrentContractExtension currentContractExtension;

	@Getter
	private ContractExtension contractExtension;

	private List<Order> possibleOrders;

	@Getter
	private Function<ReportItem, StreamedContent> exportFnc;

	@PostConstruct
	protected void postConstruct() {
		ContractMessagesBundle contractMb = LocaleUtils.getMessages(ContractMessagesBundle.class);
		refresh();

		exportFnc = initExportFnc(contractCardGenerationAs, contractExtension, contractMb.cannotGenerateReport());
	}

	public String remove() {
		em.remove(contractExtension);
		return outcomeConstructor.construct(ContractCardViewModel.VIEW_ID,
				IdentifiableOutcomeParam.of("contract", contractExtension.getContract()));
	}

	public List<Order> getPossibleOrders() {
		if (possibleOrders == null) {
			possibleOrders = orderRepository.findOrders(contractExtension.getCustomer());
		}
		return possibleOrders;
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		checkNotNull(currentContractExtension.getValue(), "currentContractExtension required");
		if (currentContractExtension.changed(contractExtension)) {
			contractExtension = currentContractExtension.getValue();
			log.debugv("postConstruct. contract_extension_id={0}", contractExtension.getId());
		}
	}
}