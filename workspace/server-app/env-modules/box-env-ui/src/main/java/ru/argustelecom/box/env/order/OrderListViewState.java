package ru.argustelecom.box.env.order;

import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.ASSIGNEE;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.CREATE_FROM;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.CREATE_TO;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.CUSTOMER_TYPE;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.DUE_DATE;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.NUMBER;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.PRIORITY;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.STATE;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.env.order.model.OrderPriority;
import ru.argustelecom.box.env.order.model.OrderState;
import ru.argustelecom.box.env.task.AssigneeDto;
import ru.argustelecom.box.env.task.AssigneeDtoTranslator;
import ru.argustelecom.system.inf.page.PresentationState;

@PresentationState
@Getter
@Setter
public class OrderListViewState extends FilterViewState implements Serializable {

	private static final long serialVersionUID = 3698432744607608634L;

	@FilterMapEntry(NUMBER)
	private String number;
	@FilterMapEntry(value = ASSIGNEE, translator = AssigneeDtoTranslator.class)
	private AssigneeDto assignee;
	@FilterMapEntry(value = CUSTOMER_TYPE, translator = CustomerTypeDtoTranslator.class)
	private CustomerTypeDto customerType;
	@FilterMapEntry(STATE)
	private OrderState state;
	@FilterMapEntry(PRIORITY)
	private OrderPriority priority;
	@FilterMapEntry(CREATE_FROM)
	private Date createFrom;
	@FilterMapEntry(CREATE_TO)
	private Date createTo;
	@FilterMapEntry(DUE_DATE)
	private Date dueDate;

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public class OrderFilter {
		public static final String NUMBER = "NUMBER";
		public static final String ASSIGNEE = "ASSIGNEE";
		public static final String CUSTOMER_TYPE = "CUSTOMER_TYPE";
		public static final String STATE = "STATE";
		public static final String PRIORITY = "PRIORITY";
		public static final String CREATE_FROM = "CREATE_FROM";
		public static final String CREATE_TO = "CREATE_TO";
		public static final String DUE_DATE = "DUE_DATE";
	}

}