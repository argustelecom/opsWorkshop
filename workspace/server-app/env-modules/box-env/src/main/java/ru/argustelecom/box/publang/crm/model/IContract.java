package ru.argustelecom.box.publang.crm.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.publang.base.model.ILocation;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = IContract.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = IContract.WRAPPER_NAME)
public class IContract extends IAbstractContract {

	public static final String TYPE_NAME = "iContract";
	public static final String WRAPPER_NAME = "contractWrapper";

	@XmlElement
	private List<IContractEntry> finalEntries;

	@Builder
	public IContract(Long id, String objectName, String documentNumber, Date documentDate, Date creationDate,
			List<IContractEntry> entries, Long customerId, Long orderId, IState state, ILocation address,
			Date validFrom, Date validTo, List<IContractEntry> finalEntries) {
		super(id, objectName, documentNumber, documentDate, creationDate, entries, customerId, orderId, state, address,
				validFrom, validTo);
		this.finalEntries = finalEntries;
	}

	private static final long serialVersionUID = -3962539314927466168L;

}