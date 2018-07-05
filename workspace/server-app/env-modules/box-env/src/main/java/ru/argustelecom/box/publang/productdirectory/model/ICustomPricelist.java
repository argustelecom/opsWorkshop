package ru.argustelecom.box.publang.productdirectory.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = ICustomPricelist.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = ICustomPricelist.WRAPPER_NAME)
public class ICustomPricelist extends IAbstractPricelist {

	public static final String TYPE_NAME = "iCustomPricelist";
	public static final String WRAPPER_NAME = "customPricelistWrapper";

	@XmlElement
	private Long customerId;

	@Builder
	public ICustomPricelist(Long id, String objectName, IState state, Date validFrom, Date validTo, BigDecimal taxRate,
			Long customerId) {
		super(id, objectName, state, validFrom, validTo, taxRate);
		this.customerId = customerId;
	}

	private static final long serialVersionUID = -590290118834189300L;

}