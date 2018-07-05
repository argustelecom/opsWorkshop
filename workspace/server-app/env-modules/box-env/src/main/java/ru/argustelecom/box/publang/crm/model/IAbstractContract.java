package ru.argustelecom.box.publang.crm.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.publang.base.model.IDocument;
import ru.argustelecom.box.publang.base.model.ILocation;
import ru.argustelecom.box.publang.base.model.IState;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = IAbstractContract.TYPE_NAME, namespace = "")
public abstract class IAbstractContract extends IDocument {

	public static final String TYPE_NAME = "iAbstractContract";

	@XmlElement
	private List<IContractEntry> entries;

	@XmlElement
	private Long customerId;

	@XmlElement
	private Long orderId;

	@XmlElement
	private IState state;

	@XmlElement
	private ILocation address;

	@XmlElement
	private Date validFrom;

	@XmlElement
	private Date validTo;

	public IAbstractContract(Long id, String objectName, String documentNumber, Date documentDate, Date creationDate,
			List<IContractEntry> entries, Long customerId, Long orderId, IState state, ILocation address,
			Date validFrom, Date validTo) {
		super(id, objectName, documentNumber, documentDate, creationDate);
		this.entries = entries;
		this.customerId = customerId;
		this.orderId = orderId;
		this.state = state;
		this.address = address;
		this.validFrom = validFrom;
		this.validTo = validTo;
	}

	private static final long serialVersionUID = -8928413404077649730L;

}