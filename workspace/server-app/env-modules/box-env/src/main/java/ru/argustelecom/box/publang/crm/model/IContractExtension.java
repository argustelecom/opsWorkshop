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
@XmlType(name = IContractExtension.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = IContractExtension.WRAPPER_NAME)
public class IContractExtension extends IAbstractContract {

	public static final String TYPE_NAME = "iContractExtension";
	public static final String WRAPPER_NAME = "contractExtensionWrapper";

	@XmlElement
	private Long contractId;

	@XmlElement
	private List<IContractEntry> excludedEntries;

	@Builder
	public IContractExtension(Long id, String objectName, String documentNumber, Date documentDate, Date creationDate,
			List<IContractEntry> entries, Long customerId, Long orderId, IState state, ILocation address,
			Date validFrom, Date validTo, Long contractId, List<IContractEntry> excludedEntries) {
		super(id, objectName, documentNumber, documentDate, creationDate, entries, customerId, orderId, state, address,
				validFrom, validTo);
		this.contractId = contractId;
		this.excludedEntries = excludedEntries;
	}

	private static final long serialVersionUID = -5640858715453274260L;

}