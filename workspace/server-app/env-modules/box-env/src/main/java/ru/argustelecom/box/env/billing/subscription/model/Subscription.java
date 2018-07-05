package ru.argustelecom.box.env.billing.subscription.model;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.persistence.CascadeType.ALL;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.FORMALIZATION;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms_;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.billing.model.ISubscription;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryLogicalFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Подписка
 *
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "subscription")
@EntityWrapperDef(name = ISubscription.WRAPPER_NAME)
public class Subscription extends BusinessObject implements LifecycleObject<SubscriptionState>, Printable {

	@Enumerated(EnumType.STRING)
	private SubscriptionState state;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "subject_id")
	private AbstractProductType subject;

	@Getter
	@Setter
	@Embedded
	@AttributeOverride(name = "amount", column = @Column(name = "cost"))
	private Money cost;

	@Setter
	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = ALL)
	@JoinColumn(name = "subject_cause_id")
	private SubscriptionSubjectCause subjectCause;

	@Setter
	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = ALL)
	@JoinColumn(name = "cost_cause_id")
	private SubscriptionCostCause costCause;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "personal_account_id")
	private PersonalAccount personalAccount;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "provision_terms_id")
	private RecurrentTerms provisionTerms;

	@Getter
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "amount", column = @Column(name = "accounting_amount")),
			@AttributeOverride(name = "unit", column = @Column(name = "accounting_unit")) })
	private PeriodDuration accountingDuration;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date validFrom;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date closeDate;

	@Version
	private Long version;

	protected Subscription() {
	}

	@Builder
	protected Subscription(Long id, PersonalAccount personalAccount, AbstractProductType subject, Money cost,
			RecurrentTerms provisionTerms, PeriodDuration accountingDuration, Date validFrom, Date validTo) {

		super(id);

		checkRequiredArgument(personalAccount, "personalAccount");
		checkRequiredArgument(subject, "subject");
		checkRequiredArgument(cost, "cost");
		checkRequiredArgument(provisionTerms, "provisionTerms");
		checkRequiredArgument(accountingDuration, "accountingDuration");
		checkArgument(provisionTerms.isRecurrent(), "provision terms must be recurrent");

		this.personalAccount = personalAccount;
		this.subject = subject;
		this.cost = cost;
		this.provisionTerms = provisionTerms;
		this.accountingDuration = accountingDuration;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.creationDate = new Date();
		this.state = FORMALIZATION;
	}

	@Override
	public String getObjectName() {
		return subject.getObjectName();
	}

	public SubscriptionSubjectCause getSubjectCause() {
		return initializeAndUnproxy(subjectCause);
	}

	public SubscriptionCostCause getCostCause() {
		return initializeAndUnproxy(costCause);
	}

	@Override
	public SubscriptionRdo createReportData() {
		if (!(getSubjectCause() instanceof ContractSubjectCause)) {
			throw new SystemException("Other subject causes are not supported");
		}
		//@formatter:off
		return SubscriptionRdo.builder()
					.id(getId())
					.validFrom(getValidFrom())
					.validTo(getValidTo())
					.state(getState().getName())
					.contractEntry(((ContractSubjectCause) getSubjectCause()).getContractEntry().createReportData())
				.build();
		//@formatter:on
	}

	public List<Location> getLocations() {
		return getSubjectCause() instanceof ContractSubjectCause
				? ((ContractSubjectCause) getSubjectCause()).getContractEntry().getLocations()
				: Collections.emptyList();
	}

	@Override
	public SubscriptionState getState() {
		return state;
	}

	@Override
	public void setState(SubscriptionState state) {
		this.state = state;
	}

	@Override
	public Serializable getLifecycleQualifier() {
		return getProvisionTerms().getSubscriptionLifecycleQualifier();
	}

	@Override
	public void onStateChanged(SubscriptionState from, SubscriptionState to) {
		if (to == SubscriptionState.CLOSED) {
			closeDate = new Date();
		}
	}

	public static class SubscriptionQuery extends EntityQuery<Subscription> {

		private EntityQuerySimpleFilter<Subscription, SubscriptionState> state;
		private EntityQueryEntityFilter<Subscription, PersonalAccount> personalAccount;
		private EntityQueryDateFilter<Subscription> validFrom;
		private EntityQueryDateFilter<Subscription> validTo;
		private EntityQueryDateFilter<Subscription> creationDate;
		private EntityQueryDateFilter<Subscription> closeDate;

		private EntityQueryEntityFilter<Subscription, ProductOfferingContractEntry> subject;
		private EntityQueryLogicalFilter<Subscription> manualControl;

		private Join<Subscription, ContractSubjectCause> contractSubjectCauseJoin;
		private Join<Subscription, RecurrentTerms> termsJoin;

		public SubscriptionQuery() {
			super(Subscription.class);
			state = createFilter(Subscription_.state);
			personalAccount = createEntityFilter(Subscription_.personalAccount);
			validFrom = createDateFilter(Subscription_.validFrom);
			validTo = createDateFilter(Subscription_.validTo);
			creationDate = createDateFilter(Subscription_.creationDate);
			closeDate = createDateFilter(Subscription_.closeDate);
		}

		public EntityQuerySimpleFilter<Subscription, SubscriptionState> state() {
			return state;
		}

		public EntityQueryEntityFilter<Subscription, PersonalAccount> personalAccount() {
			return personalAccount;
		}

		public EntityQueryDateFilter<Subscription> validFrom() {
			return validFrom;
		}

		public EntityQueryDateFilter<Subscription> validTo() {
			return validTo;
		}

		public EntityQueryDateFilter<Subscription> creationDate() {
			return creationDate;
		}

		public EntityQueryDateFilter<Subscription> closeDate() {
			return closeDate;
		}

		public EntityQueryEntityFilter<Subscription, ProductOfferingContractEntry> subject() {
			if (subject == null) {
				subject = createEntityFilter(contractSubjectCauseJoin().get(ContractSubjectCause_.contractEntry),
						ContractSubjectCause_.contractEntry);
			}
			return subject;
		}

		public EntityQueryLogicalFilter<Subscription> manualControl() {
			if (manualControl == null) {
				manualControl = createLogicalFilter(termsJoin().get(RecurrentTerms_.manualControl),
						RecurrentTerms_.manualControl);
			}
			return manualControl;
		}

		private Join<Subscription, ContractSubjectCause> contractSubjectCauseJoin() {
			if (contractSubjectCauseJoin == null) {
				contractSubjectCauseJoin = root().join(Subscription_.subjectCause.getName());
			}
			return contractSubjectCauseJoin;
		}

		private Join<Subscription, RecurrentTerms> termsJoin() {
			if (termsJoin == null) {
				termsJoin = root().join(Subscription_.provisionTerms);
			}
			return termsJoin;
		}

	}

	private static final long serialVersionUID = -8816664330361444496L;

}
