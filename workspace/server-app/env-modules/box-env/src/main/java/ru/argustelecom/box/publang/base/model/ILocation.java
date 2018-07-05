package ru.argustelecom.box.publang.base.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;

@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = ILocation.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = ILocation.WRAPPER_NAME)
public class ILocation extends IEntity {

	public static final String TYPE_NAME = "iLocation";
	public static final String WRAPPER_NAME = "locationWrapper";

	@XmlElement
	private Long regionId;

	@XmlElement
	private String regionName;

	@XmlElement
	private String regionTreeName;

	@XmlElement
	private Long streetId;

	@XmlElement
	private String streetName;

	@XmlElement
	private Long buildingId;

	@XmlElement
	private String buildingName;

	@XmlElement
	private String buildingNumber;

	@XmlElement
	private String buildingCorpus;

	@XmlElement
	private String buildingWing;

	@XmlElement
	private Long lodgingId;

	@XmlElement
	private String lodgingName;

	@XmlElement
	private String fullName;

	@Builder
	public ILocation(Long id, String objectName, Long regionId, String regionName, String regionTreeName, Long streetId,
			String streetName, Long buildingId, String buildingName, String buildingNumber, String buildingCorpus,
			String buildingWing, Long lodgingId, String lodgingName, String fullName) {
		super(id, objectName);
		this.regionId = regionId;
		this.regionName = regionName;
		this.regionTreeName = regionTreeName;
		this.streetId = streetId;
		this.streetName = streetName;
		this.buildingId = buildingId;
		this.buildingName = buildingName;
		this.buildingNumber = buildingNumber;
		this.buildingCorpus = buildingCorpus;
		this.buildingWing = buildingWing;
		this.lodgingId = lodgingId;
		this.lodgingName = lodgingName;
		this.fullName = fullName;
	}

	private static final long serialVersionUID = -3274798970457355591L;

}