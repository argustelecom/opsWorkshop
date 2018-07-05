package ru.argustelecom.box.env.order;

import static ru.argustelecom.box.env.order.model.Order.OrderQuery;
import static ru.argustelecom.box.env.order.model.OrderPriority.LOW;
import static ru.argustelecom.box.env.order.model.OrderState.FORMALIZATION;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.numerationpattern.NumberGenerator;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.product.ProductTypeRepository;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Repository
public class OrderRepository implements Serializable {

	private static final long serialVersionUID = -395182621076047931L;

	private static final String ALL_ORDERS = "OrderRepository.getAllOrders";

	@Inject
	private ProductTypeRepository productTypeRepository;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private NumberGenerator numberGenerator;

	public Order createOrder(@NotNull Employee assignee, @NotNull Customer customer, Location connectionAddress,
			String connectionAddressComment) {
		Order newOrder = new Order(idSequence.nextValue(Order.class));
		newOrder.setNumber(numberGenerator.generateNumber(Order.class));
		newOrder.setState(FORMALIZATION);
		newOrder.setPriority(LOW);
		newOrder.setAssignee(assignee);
		newOrder.setCustomer(customer);
		newOrder.setConnectionAddress(connectionAddress);
		newOrder.setConnectionAddressComment(connectionAddressComment);
		em.persist(newOrder);
		return newOrder;
	}

	@NamedQuery(name = ALL_ORDERS, query = "from Order")
	public List<Order> getAllOrders() {
		return em.createNamedQuery(ALL_ORDERS, Order.class).getResultList();
	}

	public List<CommodityType> getPossibleCommodityTypes(Order order) {
		List<CommodityType> allCommodityTypes = productTypeRepository.getPossibleCommodityTypes();
		allCommodityTypes.removeAll(order.getUnmodifiableRequirements());
		return allCommodityTypes;
	}

	public List<Order> findOrders(Customer customer) {
		OrderQuery query = new OrderQuery();
		return query.and(query.customer().equal(customer)).orderBy().createTypedQuery(em).getResultList();
	}

}