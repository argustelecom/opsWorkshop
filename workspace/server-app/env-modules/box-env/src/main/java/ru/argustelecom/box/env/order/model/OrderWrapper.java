package ru.argustelecom.box.env.order.model;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.address.model.LocationWrapper;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.crm.model.IOrder;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import static com.google.common.base.Preconditions.checkNotNull;

@Named(value = IOrder.WRAPPER_NAME)
public class OrderWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private LocationWrapper lw;

	@Override
	public IOrder wrap(Identifiable entity) {
		checkNotNull(entity);
		Order order = (Order) entity;

		boolean connectionAddressExist = order.getConnectionAddress() != null;

		//@formatter:off
		return IOrder.builder()
					.id(order.getId())
					.objectName(order.getObjectName())
					.number(order.getNumber())
					.creationDate(order.getCreationDate())
					.dueDate(order.getDueDate())
					.closeDate(order.getCloseDate())
					.state(new IState(order.getState().toString(), order.getState().getName()))
					.priority(order.getPriority().name())
					.assigneeId(order.getAssignee().getId())
					.customerId(order.getCustomer().getId())
					.connectionAddress(connectionAddressExist ? lw.wrap(order.getConnectionAddress()) : null)
				.build();
		//@formatter:on
	}

	@Override
	public Order unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(Order.class, iEntity.getId());
	}

	private static final long serialVersionUID = 1314948694726515402L;

}