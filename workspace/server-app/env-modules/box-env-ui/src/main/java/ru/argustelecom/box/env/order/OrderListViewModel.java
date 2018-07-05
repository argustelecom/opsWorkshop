package ru.argustelecom.box.env.order;

import static ru.argustelecom.box.env.dto.DefaultDtoConverterUtils.translate;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.Getter;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.order.model.OrderPriority;
import ru.argustelecom.box.env.order.model.OrderState;
import ru.argustelecom.box.env.party.CustomerTypeRepository;
import ru.argustelecom.box.env.party.model.role.PartyRoleRepository;
import ru.argustelecom.box.env.task.AssigneeDto;
import ru.argustelecom.box.env.task.AssigneeDtoTranslator;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class OrderListViewModel extends ViewModel {

	private static final long serialVersionUID = 2360075386809422521L;

	@Inject
	private PartyRoleRepository partyRoleRepository;

	@Inject
	private CustomerTypeRepository customerTypeRepository;

	@Inject
	@Getter
	private OrderLazyDataModel lazyDm;

	@Inject
	private AssigneeDtoTranslator assigneeDtoTranslator;

	@Inject
	private CustomerTypeDtoTranslator customerTypeDtoTranslator;

	@Getter
	private List<AssigneeDto> employees;

	@Getter
	private List<CustomerTypeDto> customerTypes;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		employees = translate(assigneeDtoTranslator, partyRoleRepository.getAllEmployees());
		customerTypes = translate(customerTypeDtoTranslator, customerTypeRepository.getAllCustomerTypes());
	}

	public List<OrderState> getStates() {
		return Arrays.asList(OrderState.values());
	}

	public OrderPriority[] getPriorities() {
		return OrderPriority.values();
	}

}