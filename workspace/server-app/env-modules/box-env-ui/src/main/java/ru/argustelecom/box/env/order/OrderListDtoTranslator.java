package ru.argustelecom.box.env.order;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class OrderListDtoTranslator implements DefaultDtoTranslator<OrderListDto, Order> {
	@Override
	public OrderListDto translate(Order order) {
		//@formatter:off
		return OrderListDto.builder()
				.id(order.getId())
				.number(order.getNumber())
				.state(order.getState().getName())
				.creationDate(order.getCreationDate())
				.dueDate(order.getDueDate())
				.customerType(order.getCustomer().getTypeInstance() != null ? order.getCustomer().getTypeInstance().getType().getObjectName() : null)
				.priority(order.getPriority().getName())
				.assignee(order.getAssignee().getObjectName())
				.build();
		//@formatter:on
	}
}
