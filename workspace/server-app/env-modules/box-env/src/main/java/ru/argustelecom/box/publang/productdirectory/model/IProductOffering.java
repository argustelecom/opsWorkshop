package ru.argustelecom.box.publang.productdirectory.model;

import java.math.BigDecimal;

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
@XmlType(name = IProductOffering.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = IProductOffering.WRAPPER_NAME)
public class IProductOffering extends IEntity {

	public static final String TYPE_NAME = "iProductOffering";
	public static final String WRAPPER_NAME = "productOfferingWrapper";

	@XmlElement
	private Long typeId;

	@XmlElement
	private Long pricelistId;

	@XmlElement
	private int orderNum;

	@XmlElement
	private BigDecimal price;

	@Builder
	public IProductOffering(Long id, String objectName, Long typeId, Long pricelistId, int orderNum, BigDecimal price) {
		super(id, objectName);
		this.typeId = typeId;
		this.pricelistId = pricelistId;
		this.orderNum = orderNum;
		this.price = price;
	}

	private static final long serialVersionUID = -2742396207161847095L;

}