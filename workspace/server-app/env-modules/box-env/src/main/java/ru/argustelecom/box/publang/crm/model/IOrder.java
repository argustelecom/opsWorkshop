package ru.argustelecom.box.publang.crm.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.ILocation;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = IOrder.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = IOrder.WRAPPER_NAME)
public class IOrder extends IEntity {

	public static final String TYPE_NAME = "iOrder";
	public static final String WRAPPER_NAME = "orderWrapper";

	@XmlElement
	private String number;

	@XmlElement
	private Date creationDate;

	@XmlElement
	private Date dueDate;

	@XmlElement
	private Date closeDate;

	@XmlElement
	private IState state;

	@XmlElement
	private String priority;

	@XmlElement
	private Long assigneeId;

	@XmlElement
	private Long customerId;

	@XmlElement
	private ILocation connectionAddress;

	@Builder
	public IOrder(Long id, String objectName, String number, Date creationDate, Date dueDate, Date closeDate,
			IState state, String priority, Long assigneeId, Long customerId, ILocation connectionAddress) {
		super(id, objectName);
		this.number = number;
		this.creationDate = creationDate;
		this.dueDate = dueDate;
		this.closeDate = closeDate;
		this.state = state;
		this.priority = priority;
		this.assigneeId = assigneeId;
		this.customerId = customerId;
		this.connectionAddress = connectionAddress;
	}

	private static final long serialVersionUID = 3137873570978184014L;

}