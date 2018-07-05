package ru.argustelecom.box.publang.billing.model;

import java.math.BigDecimal;
import java.util.Currency;

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
@XmlType(name = IPersonalAccount.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = IPersonalAccount.WRAPPER_NAME)
public class IPersonalAccount extends IEntity {

	public static final String TYPE_NAME = "iPersonalAccount";
	public static final String WRAPPER_NAME = "personalAccountWrapper";

	@XmlElement
	private String number;

	@XmlElement
	private Long customerId;

	@XmlElement
	private IState state;

	@XmlElement
	private Currency currency;

	@XmlElement
	private BigDecimal threshold;

	@XmlElement
	private BigDecimal balance;

	@XmlElement
	private BigDecimal availableBalance;

	@Builder
	public IPersonalAccount(Long id, String objectName, String number, Long customerId, IState state, Currency currency,
			BigDecimal threshold, BigDecimal balance, BigDecimal availableBalance) {
		super(id, objectName);
		this.number = number;
		this.customerId = customerId;
		this.state = state;
		this.currency = currency;
		this.threshold = threshold;
		this.balance = balance;
		this.availableBalance = availableBalance;
	}

	private static final long serialVersionUID = -1619973897164275330L;

}