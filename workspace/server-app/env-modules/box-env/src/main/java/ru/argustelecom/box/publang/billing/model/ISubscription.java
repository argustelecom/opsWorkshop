package ru.argustelecom.box.publang.billing.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = ISubscription.TYPE_NAME, namespace = "")
@EntityWrapperDef(name = ISubscription.WRAPPER_NAME)
public class ISubscription extends IEntity {

	public static final String TYPE_NAME = "iSubscription";
	public static final String WRAPPER_NAME = "subscriptionWrapper";

	@XmlElement
	private IState state;

	@XmlElement
	private Long subjectId;

	@XmlElement
	private BigDecimal cost;

	@XmlElement
	private Long subjectCauseId;

	@XmlElement
	private Long costCauseId;

	@XmlElement
	private Long provisionTermsId;

	@XmlElement
	private Long personalAccountId;

	@XmlElement
	private Date validFrom;

	@XmlElement
	private Date validTo;

	@XmlElement
	private Date creationDate;

	@XmlElement
	private Date closeDate;

	@Builder
	public ISubscription(Long id, String objectName, IState state, Long subjectId, BigDecimal cost, Long subjectCauseId,
			Long costCauseId, Long provisionTermsId, Long personalAccountId, Date validFrom, Date validTo,
			Date creationDate, Date closeDate) {
		super(id, objectName);
		this.state = state;
		this.subjectId = subjectId;
		this.cost = cost;
		this.subjectCauseId = subjectCauseId;
		this.costCauseId = costCauseId;
		this.provisionTermsId = provisionTermsId;
		this.personalAccountId = personalAccountId;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.creationDate = creationDate;
		this.closeDate = closeDate;
	}

	public static final class State {

		public static final String FORMALIZATION = "FORMALIZATION";
		public static final String ACTIVATION_WAITING = "ACTIVATION_WAITING";
		public static final String ACTIVE = "ACTIVE";
		public static final String SUSPENSION_FOR_DEBT_WAITING = "SUSPENSION_FOR_DEBT_WAITING";
		public static final String SUSPENSION_ON_DEMAND_WAITING = "SUSPENSION_ON_DEMAND_WAITING";
		public static final String SUSPENDED_FOR_DEBT = "SUSPENDED_FOR_DEBT";
		public static final String SUSPENDED_ON_DEMAND = "SUSPENDED_ON_DEMAND";
		public static final String SUSPENDED = "SUSPENDED";
		public static final String CLOSURE_WAITING = "CLOSURE_WAITING";
		public static final String CLOSED = "CLOSED";

		private State() {
		}
	}

	private static final long serialVersionUID = 5227061108961904992L;
}