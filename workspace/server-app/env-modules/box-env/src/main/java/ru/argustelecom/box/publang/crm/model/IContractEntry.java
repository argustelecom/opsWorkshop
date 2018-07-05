package ru.argustelecom.box.publang.crm.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.ILocation;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.productdirectory.model.IProductOffering;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = IProductOffering.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = IContractEntry.WRAPPER_NAME)
public class IContractEntry extends IEntity {

	public static final String TYPE_NAME = "iContractEntry";
	public static final String WRAPPER_NAME = "contractEntryWrapper";

	@XmlElement
	private Long contractId;

	@XmlElement
	private Long pricelistEntryId;

	@XmlElement
	private List<ILocation> locations;

	@Builder
	public IContractEntry(Long id, String objectName, Long contractId, Long pricelistEntryId,
			List<ILocation> locations) {
		super(id, objectName);
		this.contractId = contractId;
		this.pricelistEntryId = pricelistEntryId;
		this.locations = locations;
	}

	private static final long serialVersionUID = -2776130470883799977L;

}