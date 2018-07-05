package ru.argustelecom.box.env.billing.provision.model;

import java.util.Arrays;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Getter;
import lombok.Setter;

import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.inf.modelbase.SuperClassBuilder;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.billing.model.IRecurrentTerms;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryLogicalFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

/**
 * Справочник условий предоставления
 * <p>
 *
 */
@Entity
@Access(AccessType.FIELD)
@EntityWrapperDef(name = IRecurrentTerms.WRAPPER_NAME)
public class RecurrentTerms extends AbstractProvisionTerms implements LifecycleObject<RecurrentTermsState> {

	private static final long serialVersionUID = 3149890746569011652L;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private RecurrentTermsState state;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private PeriodType periodType;

	@Getter
	@Setter
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "amount", column = @Column(name = "charging_amount")),
			@AttributeOverride(name = "unit", column = @Column(name = "charging_unit")) })
	private PeriodDuration chargingDuration;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "lifecycle_qualifier")
	private SubscriptionLifecycleQualifier subscriptionLifecycleQualifier;

	@Getter
	@Setter
	@Column(nullable = false)
	private boolean reserveFunds;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private RoundingPolicy roundingPolicy;

	@Getter
	@Setter
	@Column(nullable = false)
	private boolean manualControl;

	protected RecurrentTerms() {
		super();
	}

	public RecurrentTerms(Long id) {
		this.id = id;
		this.state = RecurrentTermsState.FORMALIZATION;
		this.reserveFunds = false;
		this.manualControl = false;
	}

	protected RecurrentTerms(Long id, PeriodType periodType, PeriodDuration chargingDuration,
			SubscriptionLifecycleQualifier subscriptionLifecycleQualifier) {
		super(id);
		this.periodType = periodType;
		this.chargingDuration = chargingDuration;
		this.subscriptionLifecycleQualifier = subscriptionLifecycleQualifier;
		this.state = RecurrentTermsState.FORMALIZATION;
		this.reserveFunds = false;
		this.manualControl = false;
	}

	@Override
	public final boolean isRecurrent() {
		return true;
	}

	// FIXME экспериментально, не повторять в своем коде
	public static class RecurrentTermsBuilder extends SuperClassBuilder<RecurrentTerms, RecurrentTermsBuilder> {

		private PeriodType periodType;
		private PeriodDuration chargingDuration;
		private SubscriptionLifecycleQualifier lifecycleQualifier;

		public RecurrentTermsBuilder setPeriodType(PeriodType periodType) {
			this.periodType = periodType;
			return this;
		}

		public RecurrentTermsBuilder setChargingDuration(PeriodDuration chargingDuration) {
			this.chargingDuration = chargingDuration;
			return this;
		}

		public RecurrentTermsBuilder setSubscriptionLifecycleQualifier(
				SubscriptionLifecycleQualifier lifecycleQualifier) {
			this.lifecycleQualifier = lifecycleQualifier;
			return this;
		}

		@Override
		protected void validate() {
			super.validate();
			//@formatter:off
			
			validateNotNull(periodType, "Тип периода");
			validateNotNull(chargingDuration, "Длительность периода списания");
			
			validateState(
				periodType.isSupportedChargingPeriodUnit(chargingDuration.getUnit()), 
				"Единица измерения периода {0} не поддерживается текущим типом периода {1}: {2}",
				chargingDuration.getUnit(), periodType, Arrays.toString(periodType.getChargingPeriodUnits())
			);
			
			//@formatter:on
		}

		@Override
		protected RecurrentTerms buildBusinessObject() {
			return new RecurrentTerms(id, periodType, chargingDuration, lifecycleQualifier);
		}
	}

	public static class RecurrentTermsQuery<E extends RecurrentTerms> extends AbstractProvisionTermsQuery<E> {

		private EntityQuerySimpleFilter<E, SubscriptionLifecycleQualifier> lifecycleQualifier;
		private EntityQueryLogicalFilter<E> reserveFunds;
		private EntityQuerySimpleFilter<E, RecurrentTermsState> state;

		public RecurrentTermsQuery(Class<E> entityClass) {
			super(entityClass);
			lifecycleQualifier = createFilter(RecurrentTerms_.subscriptionLifecycleQualifier);
			reserveFunds = createLogicalFilter(RecurrentTerms_.reserveFunds);
			state = createFilter(RecurrentTerms_.state);
		}

		public EntityQuerySimpleFilter<E, SubscriptionLifecycleQualifier> lifecycleQualifier() {
			return lifecycleQualifier;
		}

		public EntityQueryLogicalFilter<E> reserveFunds() {
			return reserveFunds;
		}

		public EntityQuerySimpleFilter<E, RecurrentTermsState> state() {
			return state;
		}
	}
}
