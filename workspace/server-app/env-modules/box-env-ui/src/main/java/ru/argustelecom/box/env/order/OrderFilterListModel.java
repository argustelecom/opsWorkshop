package ru.argustelecom.box.env.order;

import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.ASSIGNEE;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.CREATE_FROM;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.CREATE_TO;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.CUSTOMER_TYPE;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.DUE_DATE;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.NUMBER;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.PRIORITY;
import static ru.argustelecom.box.env.order.OrderListViewState.OrderFilter.STATE;
import static ru.argustelecom.box.env.order.model.Order.OrderQuery;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.order.model.OrderPriority;
import ru.argustelecom.box.env.order.model.OrderState;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Employee;

public class OrderFilterListModel extends BaseEQConvertibleDtoFilterModel<OrderQuery> {

	@Inject
	private OrderListViewState orderListViewState;

	@Override
	public void buildPredicates(OrderQuery orderQuery) {
		Map<String, Object> filterMap = orderListViewState.getFilterMap();
		for (Map.Entry<String, Object> filterEntry : filterMap.entrySet()) {
			if (filterEntry != null) {
				switch (filterEntry.getKey()) {
				case NUMBER:
					addPredicate(orderQuery.number().equal((String) filterEntry.getValue()));
					break;
				case ASSIGNEE:
					addPredicate(orderQuery.assignee().equal((Employee) getIdentifiable(filterEntry.getValue())));
					break;
				case CUSTOMER_TYPE:
					addPredicate(orderQuery.byCustomerType((CustomerType) getIdentifiable(filterEntry.getValue())));
					break;
				case STATE:
					addPredicate(orderQuery.state().equal((OrderState) filterEntry.getValue()));
					break;
				case PRIORITY:
					addPredicate(orderQuery.priority().equal((OrderPriority) filterEntry.getValue()));
					break;
				case CREATE_FROM:
					addPredicate(orderQuery.createFrom().greaterOrEqualTo((Date) filterEntry.getValue()));
					break;
				case CREATE_TO:
					addPredicate(orderQuery.createTo().lessOrEqualTo((Date) filterEntry.getValue()));
					break;
				case DUE_DATE:
					addPredicate(orderQuery.dueDate().equal((Date) filterEntry.getValue()));
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public Supplier<OrderQuery> entityQuerySupplier() {
		return OrderQuery::new;
	}

	private static final long serialVersionUID = -4684427402126589719L;

}