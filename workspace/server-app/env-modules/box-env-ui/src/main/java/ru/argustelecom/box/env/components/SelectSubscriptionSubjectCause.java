package ru.argustelecom.box.env.components;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import ru.argustelecom.box.env.billing.subscription.SubjectCauseDto;
import ru.argustelecom.box.env.billing.subscription.SubjectCauseDtoTranslator;
import ru.argustelecom.box.env.billing.subscription.SubjectCauseType;
import ru.argustelecom.box.env.contract.ContractRepository;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.order.OrderRepository;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.utils.CDIHelper;

@FacesComponent("selectSubscriptionSubjectCause")
public class SelectSubscriptionSubjectCause extends UIInput implements NamingContainer {

	private static final String CUSTOMER = "customer";
	private static final String TYPE = "type";
	private static final String POSSIBLE_SUBJECT_CAUSE_LIST = "possibleSubjectCauseList";

	private SubjectCauseDtoTranslator subjectCauseDtoTr;

	@Override
	public String getFamily() {
		return UINamingContainer.COMPONENT_FAMILY;
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		Customer customer = (Customer) getAttributes().get(CUSTOMER);
		SubjectCauseType subjectCauseType = (SubjectCauseType) getAttributes().get(TYPE);

		checkNotNull(customer);
		checkNotNull(subjectCauseType);

		if (subjectCauseDtoTr == null)
			subjectCauseDtoTr = CDIHelper.lookupCDIBean(SubjectCauseDtoTranslator.class);

		initPossibleSubjectCauseList(customer, subjectCauseType);

		super.encodeBegin(context);
	}

	private void initPossibleSubjectCauseList(Customer customer, SubjectCauseType type) {
		List<SubjectCauseDto> possibleSubjectCauseList = new ArrayList<>();
		switch (type) {
		case CONTRACT:
			fillContractSubjectCause(possibleSubjectCauseList, customer);
			break;
		case ORDER:
			fillOrderSubjectCause(possibleSubjectCauseList, customer);
			break;
		default:
			throw new SystemException(String.format("Unsupported subject cause type: '%s'", type));
		}
		setPossibleSubjectCauseList(possibleSubjectCauseList);
	}

	private void fillContractSubjectCause(List<SubjectCauseDto> scList, Customer customer) {
		ContractRepository contractRp = CDIHelper.lookupCDIBean(ContractRepository.class);

		contractRp.countContractsWithEntriesWithoutSubs(customer.getId());
		List<AbstractContract> contracts = contractRp.findContractsWithEntriesWithoutSubs(customer.getId());
		scList.addAll(contracts.stream().map(c -> subjectCauseDtoTr.translate(c)).collect(toList()));
	}

	private void fillOrderSubjectCause(List<SubjectCauseDto> subjectCauseListList, Customer customer) {
		OrderRepository orderRepository = CDIHelper.lookupCDIBean(OrderRepository.class);
		subjectCauseListList.addAll(orderRepository.findOrders(customer).stream()
				.map(order -> subjectCauseDtoTr.translate(order)).collect(toList()));
	}

	@SuppressWarnings("unchecked")
	public List<SubjectCauseDto> getPossibleSubjectCauseList() {
		return (List<SubjectCauseDto>) getStateHelper().get(POSSIBLE_SUBJECT_CAUSE_LIST);
	}

	public void setPossibleSubjectCauseList(List<SubjectCauseDto> possibleSubjectCauseList) {
		getStateHelper().put(POSSIBLE_SUBJECT_CAUSE_LIST, possibleSubjectCauseList);
	}

}