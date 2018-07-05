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
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = IReserve.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = IReserve.WRAPPER_NAME)
public class IReserve extends IEntity {

	public static final String TYPE_NAME = "iReserve";
	public static final String WRAPPER_NAME = "reserveWrapper";

	@XmlElement
	private Long personalAccountId;

	@XmlElement
	private BigDecimal amount;

	@XmlElement
	private Date reserveDate;

	@Builder
	public IReserve(Long id, String objectName, Long personalAccountId, BigDecimal amount, Date reserveDate) {
		super(id, objectName);
		this.personalAccountId = personalAccountId;
		this.amount = amount;
		this.reserveDate = reserveDate;
	}

	private static final long serialVersionUID = 1937971153356369512L;

}