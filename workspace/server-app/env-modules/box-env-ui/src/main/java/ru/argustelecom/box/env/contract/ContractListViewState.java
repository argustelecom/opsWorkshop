package ru.argustelecom.box.env.contract;

import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.BROKER;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.CONTRACT_TYPE;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.CUSTOMER;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.CUSTOMER_TYPE;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.NUMBER;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.PROVIDER;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.STATE;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.VALID_FROM;
import static ru.argustelecom.box.env.contract.ContractListViewState.ContractFilter.VALID_TO;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contract.dto.ContractRoleDto;
import ru.argustelecom.box.env.contract.dto.ContractRoleDtoTranslator;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerDtoTranslator;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.system.inf.page.PresentationState;

@PresentationState
@Getter
@Setter
public class ContractListViewState extends FilterViewState implements Serializable {

	private static final long serialVersionUID = -8639165889829317288L;

	@FilterMapEntry(NUMBER)
	private String number;
	@FilterMapEntry(value = CONTRACT_TYPE, translator = ContractTypeDtoTranslator.class)
	private ContractTypeDto contractType;
	@FilterMapEntry(value = CUSTOMER_TYPE, translator = CustomerTypeDtoTranslator.class)
	private CustomerTypeDto customerType;
	@FilterMapEntry(value = CUSTOMER, translator = CustomerDtoTranslator.class)
	private CustomerDto customer;
	@FilterMapEntry(STATE)
	private ContractState state;
	@FilterMapEntry(VALID_FROM)
	private Date validFrom;
	@FilterMapEntry(VALID_TO)
	private Date validTo;
	@FilterMapEntry(value = PROVIDER, translator = ContractRoleDtoTranslator.class)
	private ContractRoleDto provider;
	@FilterMapEntry(value = BROKER, translator = ContractRoleDtoTranslator.class)
	private ContractRoleDto broker;

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class ContractFilter {
		public static final String NUMBER = "NUMBER";
		public static final String CONTRACT_TYPE = "CONTRACT_TYPE";
		public static final String CUSTOMER_TYPE = "CUSTOMER_TYPE";
		public static final String CUSTOMER = "CUSTOMER";
		public static final String STATE = "STATE";
		public static final String VALID_FROM = "VALID_FROM";
		public static final String VALID_TO = "VALID_TO";
		public static final String PROVIDER = "PROVIDER";
		public static final String BROKER = "BROKER";
	}

}