package ru.argustelecom.box.publang.billing.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = "mediationId", callSuper = false)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = IChargeJob.TYPE_NAME)
@EntityWrapperDef(name = IChargeJob.WRAPPER_NAME)
public class IChargeJob extends IEntity implements  Serializable {

	private static final long serialVersionUID = 8146621583708714509L;

	public static final String TYPE_NAME = "iChargeJob";
	public static final String WRAPPER_NAME = "chargeJobWrapper";

	private String mediationId;
	private String dataType;
	private IFilter filter;

	@XmlTransient
	@Override
	public Long getId() {
		return super.getId();
	}

	@XmlTransient
	@Override
	public String getObjectName() {
		return super.getObjectName();
	}

	@XmlElement(required = true)
	public String getMediationId() {
		return mediationId;
	}

	@XmlElement
	public String getDataType() {
		return dataType;
	}

	@XmlElement
	public IFilter getFilter() {
		return filter;
	}
}
