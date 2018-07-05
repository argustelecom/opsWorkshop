package ru.argustelecom.box.env.order;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ru.argustelecom.box.env.contract.ContractRepository;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class OrderContractsFrameModel implements Serializable {

	private static final long serialVersionUID = 4018526267668389279L;

	@Inject
	private ContractRepository contractRepository;

	private Order order;

	private List<Contract> contractsWhichMayHaveExtensions;

	public void preRender(Order order) {
		this.order = order;
	}

	public List<Contract> getContractsWhichMayHaveExtensions() {
		if (contractsWhichMayHaveExtensions == null)
			contractsWhichMayHaveExtensions = contractRepository.findContracts(order.getCustomer()).stream()
					.filter(Contract::canCreateExtension).collect(Collectors.toList());
		return contractsWhichMayHaveExtensions;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Order getOrder() {
		return order;
	}

}