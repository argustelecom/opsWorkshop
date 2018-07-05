package ru.argustelecom.box.env.order;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
public class OrderListDto extends ConvertibleDto {

	private Long id;
	private String number;
	private String state;
	private Date creationDate;
	private Date dueDate;
	private String customerType;
	private String priority;
	private String assignee;

	@Builder
	public OrderListDto(Long id, String number, String state, Date creationDate, Date dueDate, String customerType,
			String priority, String assignee) {
		this.id = id;
		this.number = number;
		this.state = state;
		this.creationDate = creationDate;
		this.dueDate = dueDate;
		this.customerType = customerType;
		this.priority = priority;
		this.assignee = assignee;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return OrderListDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Order.class;
	}
}