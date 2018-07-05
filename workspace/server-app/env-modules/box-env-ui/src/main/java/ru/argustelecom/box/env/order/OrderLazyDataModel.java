package ru.argustelecom.box.env.order;

import static ru.argustelecom.box.env.order.OrderLazyDataModel.OrderSort;
import static ru.argustelecom.box.env.order.model.Order.OrderQuery;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.criteria.JoinType;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.order.model.Order_;
import ru.argustelecom.box.env.party.model.CustomerTypeInstance_;
import ru.argustelecom.box.env.party.model.CustomerType_;
import ru.argustelecom.box.env.party.model.Party_;
import ru.argustelecom.box.env.party.model.role.Customer_;
import ru.argustelecom.box.env.party.model.role.Employee_;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class OrderLazyDataModel extends EQConvertibleDtoLazyDataModel<Order, OrderListDto, OrderQuery, OrderSort> {

	@Inject
	private OrderFilterListModel orderFilterListModel;

	@Inject
	private OrderListDtoTranslator orderListDtoTranslator;

	@PostConstruct
	private void postConstruct() {
		initPaths();
	}

	private void initPaths() {
		addPath(OrderSort.id, query -> query.root().get(Order_.id));
		addPath(OrderSort.number, query -> query.root().get(Order_.number));
		addPath(OrderSort.state, query -> query.root().get(Order_.state));
		addPath(OrderSort.creationDate, query -> query.root().get(Order_.creationDate));
		addPath(OrderSort.dueDate, query -> query.root().get(Order_.dueDate));
		addPath(OrderSort.customerType,
				query -> query.root().join(Order_.customer, JoinType.LEFT).join(Customer_.typeInstance, JoinType.LEFT)
						.join(CustomerTypeInstance_.type, JoinType.LEFT).get(CustomerType_.name));
		addPath(OrderSort.priority, query -> query.root().get(Order_.priority));
		addPath(OrderSort.assignee, query -> query.root().join(Order_.assignee, JoinType.LEFT)
				.join(Employee_.party, JoinType.LEFT).get(Party_.sortName));
	}

	@Override
	protected Class<OrderSort> getSortableEnum() {
		return OrderSort.class;
	}

	@Override
	protected DefaultDtoTranslator<OrderListDto, Order> getDtoTranslator() {
		return orderListDtoTranslator;
	}

	@Override
	protected EQConvertibleDtoFilterModel<OrderQuery> getFilterModel() {
		return orderFilterListModel;
	}

	public enum OrderSort {
		id, number, state, creationDate, dueDate, customerType, priority, assignee
	}

	private static final long serialVersionUID = -3509342667425042272L;
}