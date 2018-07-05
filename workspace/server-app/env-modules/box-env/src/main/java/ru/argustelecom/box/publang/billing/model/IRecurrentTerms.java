package ru.argustelecom.box.publang.billing.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = IRecurrentTerms.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = IRecurrentTerms.WRAPPER_NAME)
public class IRecurrentTerms extends IEntity {

	public static final String TYPE_NAME = "iRecurrentTerms";
	public static final String WRAPPER_NAME = "recurrentTermsWrapper";

	@XmlElement
	private IState state;

	@XmlElement
	private String periodType;

	@XmlElement
	private String chargingPeriodUnit;

	@XmlElement
	private Integer amount;

	@XmlElement
	private Boolean reserveFunds;

	private RoundingPolicy roundingPolicy;

	@XmlElement
	private String subscriptionLifecycleQualifier;

	@Builder
	public IRecurrentTerms(Long id, String objectName, IState state, String periodType, String chargingPeriodUnit,
			Integer amount, Boolean reserveFunds, RoundingPolicy roundingPolicy,
			String subscriptionLifecycleQualifier) {
		super(id, objectName);
		this.state = state;
		this.periodType = periodType;
		this.chargingPeriodUnit = chargingPeriodUnit;
		this.amount = amount;
		this.reserveFunds = reserveFunds;
		this.roundingPolicy = roundingPolicy;
		this.subscriptionLifecycleQualifier = subscriptionLifecycleQualifier;
	}

	public static final class State {

		public static final String FORMALIZATION = "RecurrentTermsFormalization";
		public static final String ACTIVE = "RecurrentTermsActive";
		public static final String ARCHIVE = "RecurrentTermsArchive";

	}

	private static final long serialVersionUID = 8726430089128273839L;

}