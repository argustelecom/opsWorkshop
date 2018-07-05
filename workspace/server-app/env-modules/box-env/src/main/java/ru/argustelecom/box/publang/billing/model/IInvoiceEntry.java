package ru.argustelecom.box.publang.billing.model;

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
@XmlType(name = IInvoiceEntry.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = IInvoiceEntry.WRAPPER_NAME)
public class IInvoiceEntry extends IEntity {

	public static final String TYPE_NAME = "iInvoiceEntry";
	public static final String WRAPPER_NAME = "invoiceEntryWrapper";

	private static final long serialVersionUID = 26137337182550529L;

	@XmlElement
	private Long pricelistProductEntryId;

	@XmlElement
	private BigDecimal amount;

	@Builder
	public IInvoiceEntry(Long id, String objectName, Long pricelistProductEntryId, BigDecimal amount) {
		super(id, objectName);
		this.pricelistProductEntryId = pricelistProductEntryId;
		this.amount = amount;
	}
}