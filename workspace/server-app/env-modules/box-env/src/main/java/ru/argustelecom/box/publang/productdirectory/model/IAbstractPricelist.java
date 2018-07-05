package ru.argustelecom.box.publang.productdirectory.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = IAbstractPricelist.TYPE_NAME, namespace = "")
public abstract class IAbstractPricelist extends IEntity {

	public static final String TYPE_NAME = "iAbstractPricelist";

	@XmlElement
	private IState state;

	@XmlElement
	private Date validFrom;

	@XmlElement
	private Date validTo;

	@XmlElement
	private BigDecimal taxRate;

	public IAbstractPricelist(Long id, String objectName, IState state, Date validFrom, Date validTo,
			BigDecimal taxRate) {
		super(id, objectName);
		this.state = state;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.taxRate = taxRate;
	}

	private static final long serialVersionUID = 2981997177122163239L;

}