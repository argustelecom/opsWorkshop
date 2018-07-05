package ru.argustelecom.box.publang.billing.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = ILongTermInvoice.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = ILongTermInvoice.WRAPPER_NAME)
public class ILongTermInvoice extends IEntity {

	public static final String TYPE_NAME = "iLongTermInvoice";
	public static final String WRAPPER_NAME = "longTermInvoiceWrapper";

	@XmlElement
	private Long personalAccountId;

	@XmlElement
	private Long transactionId;

	@XmlElement
	private IState state;

	@XmlElement
	private Date startDate;

	@XmlElement
	private Date endDate;

	@XmlElement
	private Date closingDate;

	@XmlElement
	private BigDecimal totalPrice;

	@XmlElement
	private BigDecimal price;

	@XmlElement
	private BigDecimal discountValue;

	@XmlElement
	private IReserve reserve;

	@Builder
	public ILongTermInvoice(Long id, String objectName, Long personalAccountId, Long transactionId, IState state,
			Date startDate, Date endDate, Date closingDate, BigDecimal totalPrice, BigDecimal price,
			BigDecimal discountValue, IReserve reserve) {
		super(id, objectName);
		this.personalAccountId = personalAccountId;
		this.transactionId = transactionId;
		this.state = state;
		this.startDate = startDate;
		this.endDate = endDate;
		this.closingDate = closingDate;
		this.totalPrice = totalPrice;
		this.price = price;
		this.discountValue = discountValue;
		this.reserve = reserve;
	}

	private static final long serialVersionUID = -7946921538026856006L;

}