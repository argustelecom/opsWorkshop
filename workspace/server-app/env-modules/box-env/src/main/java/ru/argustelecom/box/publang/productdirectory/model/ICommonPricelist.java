package ru.argustelecom.box.publang.productdirectory.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = ICommonPricelist.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = ICommonPricelist.WRAPPER_NAME)
public class ICommonPricelist extends IAbstractPricelist {

	public static final String TYPE_NAME = "iCommonPricelist";
	public static final String WRAPPER_NAME = "commonPricelistWrapper";

	@Builder
	public ICommonPricelist(Long id, String objectName, IState state, Date validFrom, Date validTo,
			BigDecimal taxRate) {
		super(id, objectName, state, validFrom, validTo, taxRate);
	}

	private static final long serialVersionUID = 3283959748242530432L;

}