package ru.argustelecom.box.env.billing.invoice.model;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.asList;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Builder;
import lombok.Getter;

import ru.argustelecom.box.env.billing.account.model.Reserve;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanPeriod;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanTimeline;
import ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlanMapper;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.hibernate.types.JsonbType;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.billing.model.ILongTermInvoice;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

@Entity
@Access(AccessType.FIELD)
@EntityWrapperDef(name = ILongTermInvoice.WRAPPER_NAME)
@TypeDef(name = "jsonb", defaultForType = JsonNode.class, typeClass = JsonbType.class)
public class LongTermInvoice extends RegularInvoice {

	private static final long serialVersionUID = -531978806481541842L;

	/**
	 * Привилегия (Доверительный или тестовый период) по которой открыт текущий инвойс. Не должна никогда меняться
	 * напрямую, т.к. это может привести к плохим последствиям при восстановлении плана инвойса. Изменение этого
	 * атрибута должно выполняться только посредством применения нового InvoicePlan.
	 * 
	 * @see #applyPlan(InvoicePlan)
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Privilege.class)
	@JoinColumn(name = "privilege_id")
	private Privilege privilege;

	/**
	 * Подписка, для очередной тарификации которой создан этот инвойс. Изменение этого атрибута должно выполняться
	 * только посредством применения нового InvoicePlan.
	 * 
	 * @see #applyPlan(InvoicePlan)
	 */
	@Getter
	@ManyToOne
	private Subscription subscription;

	/**
	 * Размер скидки. Изменение этого атрибута должно выполняться только посредством применения нового InvoicePlan.
	 * 
	 * @see #applyPlan(InvoicePlan)
	 */
	@Getter
	@Embedded
	@AttributeOverride(name = "amount", column = @Column(name = "discount_value"))
	private Money discountValue;

	//@formatter:off
	/**
	 * Ссылки на примененные скидки для текущего инвойса. Коллекция никогда не должна меняться напрямую (хоть и есть
	 * соответствующие методы), т.к. это может привести к плохим последствиям при восстановлении плана инвойса.
	 * Добавление и удаление примененных должно выполняться только посредством применения нового InvoicePlan.
	 * 
	 * @see #applyPlan(InvoicePlan)
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(
		schema = "system",
		name = "invoice_discounts",
		joinColumns = @JoinColumn(name = "invoice_id"),
		inverseJoinColumns = @JoinColumn(name = "discount_id")
	)//@formatter:on
	private List<Discount> discounts = new ArrayList<>();

	/**
	 * Внутренний атрибут, в котором сохранены детали расчета текущего инвойса. Используется при восстановлении
	 * InvoicePlan для отображения в различных детализациях. Допустимы значения null, если для текущего инвойса по каким
	 * либо причинам нет сохраненного ранее плана. Если значение не null, то без варинатов, это должно быть корректное
	 * значение установленного формата
	 */
	@Column(name = "plan_raw")
	@Type(type = "jsonb")
	private JsonNode planRaw;

	/**
	 * Восстановленный InvoicePlan. Восстанавливается только в том случае, если это возможно
	 */
	@Transient
	private transient InvoicePlan plan;

	/**
	 * Конструктор для JPA
	 */
	protected LongTermInvoice() {
	}

	/**
	 * Если поддерживается резервирование средств, то переданный объект Reserve ассоциируется с текущим инвойсом
	 *
	 * @return true, если резервирование поддерживается
	 */
	public boolean attachReserve(Reserve reserve) {
		Boolean reserveSupported = isReserveSupported();
		if (reserveSupported) {
			doAttachReserve(reserve);
		}
		return reserveSupported;
	}

	/**
	 * True, если резервирование поддерживается текущими условиями предоставления
	 * UsageInvoiceSettings - ignored
	 */
	public boolean isReserveSupported() {
		return subscription.getProvisionTerms().isReserveFunds();
	}

	/**
	 * Основной конструктор, предназначен для инстанцирования инвойса через LongTermInvoiceBuilder
	 * 
	 * @param id
	 *            - идентификатор инвойса, должен быть определен извне
	 * @param subscription
	 *            - подписка, для которой создается текущий инвойс
	 */
	@Builder
	protected LongTermInvoice(Long id, Subscription subscription, InvoicePlan plan) {
		super(id, subscription.getPersonalAccount());
		this.subscription = subscription;
		applyPlan(plan);
	}

	/**
	 * Политика округления, использованная при расчете текущего инвойса
	 */
	public RoundingPolicy getRoundingPolicy() {
		return subscription.getProvisionTerms().getRoundingPolicy();
	}

	/**
	 * Readonly представление скидок, примененных к текущему инвойсу
	 */
	public List<Discount> getDiscounts() {
		return Collections.unmodifiableList(discounts);
	}

	/**
	 * UNSAFE!!!
	 * <p>
	 * Введен для поддержки {@linkplain InvoicePlanModifier#attachTo(LongTermInvoice)} и
	 * {@linkplain InvoicePlanModifier#detachFrom(LongTermInvoice)}. Пожалуйста, не используй этот метод в прикладном
	 * коде, иначе, когда начнутся проблемы на продуктиве при восстановлении плана, я тебя найду и сломаю тебе руку.
	 */
	public void unsafeSetPrivilege(Privilege privilege) {
		this.privilege = privilege;
	}

	/**
	 * UNSAFE!!!
	 * <p>
	 * Введен для поддержки {@linkplain InvoicePlanModifier#attachTo(LongTermInvoice)}. Пожалуйста, не используй этот
	 * метод в прикладном коде, иначе, когда начнутся проблемы на продуктиве при восстановлении плана, я тебя найду и
	 * сломаю тебе руку.
	 */
	public void unsafeAddDiscount(Discount discount) {
		discounts.add(discount);
	}

	/**
	 * UNSAFE!!!
	 * <p>
	 * Введен для поддержки {@linkplain InvoicePlanModifier#detachFrom(LongTermInvoice)}. Пожалуйста, не используй этот
	 * метод в прикладном коде, иначе, когда начнутся проблемы на продуктиве при восстановлении плана, я тебя найду и
	 * сломаю тебе руку.
	 */
	public void unsafeRemoveDiscount(Discount discount) {
		discounts.remove(discount);
	}

	/**
	 * Итоговая стоимость инвойса (базовая стоимость - стоимость скидки)
	 */
	@Override
	public Money getTotalPrice() {
		return discountValue != null && !discountValue.isZero() ? getPrice().subtract(discountValue) : getPrice();
	}

	/**
	 * Возвращает план, по которому был создан текущий инвойс. Для получения плана используется внутреннее сохраненное
	 * состояние детализации, т.е. план не расчитывается снова, а восстанавливается уже единожды расчитанное ранее
	 * состояние
	 */
	public InvoicePlan getPlan() {
		if (plan == null) {
			plan = restorePlan();
		}
		return plan;
	}

	public InvoicePlanTimeline getTimeline() {
		switch (getState()) {
		case CANCELLED:
		case CLOSED:
			return InvoicePlanTimeline.PAST;
		case ACTIVE:
			return InvoicePlanTimeline.PRESENT;
		default:
			return InvoicePlanTimeline.FUTURE;
		}
	}

	/**
	 * Применяет указанный план к текущему инвойсу. Применение плана меняет весь инвойс:
	 * <ul>
	 * <li>Обновляется ссылки на привилегию и скидки
	 * <li>Изменяются планируемые даты инвойса
	 * <li>Изменяются суммы инвойса (базовая и величина скидки)
	 * <li>Обновляется сумма зарезервированных средств (если резервирование поддерживается для текущего инвойса)
	 * <li>Примененный план сохраняется во внутреннем состоянии текущего инвойса
	 * </ul>
	 * 
	 * @param plan
	 *            - новый план, который необходимо применить к инвойсу
	 */
	public void applyPlan(InvoicePlan plan) {
		checkRequiredArgument(plan, "invoicePlan");
		checkState(this.inState(asList(InvoiceState.CREATED, InvoiceState.ACTIVE)));

		this.privilege = null;
		this.discounts.clear();

		setStartDate(plan.plannedPeriod().startDate());
		setEndDate(plan.plannedPeriod().endDate());

		setPrice(plan.summary().cost());
		this.discountValue = plan.summary().deltaCost().abs();
		updateReserve();

		if (plan.summary().modifier() != null) {
			plan.summary().modifier().attachTo(this);
		}

		// @formatter:off
		plan.details().stream()
			.filter(p -> p.modifier() != null)
			.map(InvoicePlanPeriod::modifier)
			.forEach(m -> m.attachTo(this));
		// @formatter:on

		storePlan(plan);
	}

	/**
	 * Позволяет получить по идентификатору модификатора периода или стоимости сам модификатор. Предназначен для
	 * механизма восстановления плана.
	 * <p>
	 * Считается, что ссылка на привилегию и коллекция подписок меняется только посредством применения InvoicePlan. В
	 * таком случае, во внутреннем состоянии нет необходимости хранить что-то больше простого идентификатора. Однако,
	 * т.к. не получается цивилизованным способом "защитить" изменение привилегии и скидок извне, то возможно когда
	 * нибудь случится рассинхронизация сохраненного внутреннего состояния и привилегий. Поэтому, необходимо еще
	 * подумать над этим моментом и, возможно, изменить механизм присвоения привилегий инвойсу
	 * 
	 * @param modifierId
	 *            - идентификатор модификатора периода или стоимости
	 * 
	 * @return найденный модификатор
	 */
	public InvoicePlanModifier resolveModifier(Long modifierId) {
		checkRequiredArgument(modifierId, "modifierId");

		// Может это привилегия?
		if (privilege != null && Objects.equals(privilege.getId(), modifierId)) {
			return privilege;
		}

		// Если это не привилегия, то может быть скидка?
		return discounts.stream().filter(d -> Objects.equals(d.getId(), modifierId)).findFirst().orElse(null);
	}

	@Override
	public void onStateChanged(InvoiceState from, InvoiceState to) {
		if (to == InvoiceState.CLOSED) {
			setClosingDate(new Date());
		}
	}

	/**
	 * Сохраняет указанный план во внутреннем состоянии текущего инвойса
	 */
	private void storePlan(InvoicePlan plan) {
		ObjectNode planNode = JsonNodeFactory.instance.objectNode();
		InvoicePlanMapper mapper = new InvoicePlanMapper(this);
		mapper.saveInvoicePlan(plan, planNode);

		this.planRaw = planNode;
		this.plan = plan;
	}

	/**
	 * Восстанавливает план из внутрненнего состояния текущего ивнойса. Для восстановления используются в том числе и
	 * публичные параметры текущего инвойса, такие как планируемая дата начала и окончания и т.д.
	 */
	private InvoicePlan restorePlan() {
		InvoicePlan restoredPlan = null;
		if (planRaw != null) {
			checkState(planRaw.isObject());
			InvoicePlanMapper mapper = new InvoicePlanMapper(this);
			restoredPlan = mapper.loadInvoicePlan((ObjectNode) planRaw);
		}
		return restoredPlan;
	}

	// ***************************************************************************************************************

	public static class LongTermInvoiceQuery extends RegularInvoiceQuery<LongTermInvoice> {

		private EntityQueryEntityFilter<LongTermInvoice, Subscription> subscription;
		private EntityQueryEntityFilter<LongTermInvoice, Privilege> privilege;

		public LongTermInvoiceQuery() {
			super(LongTermInvoice.class);
			privilege = createEntityFilter(LongTermInvoice_.privilege);
			subscription = createEntityFilter(LongTermInvoice_.subscription);
		}

		public EntityQueryEntityFilter<LongTermInvoice, Subscription> subscription() {
			return subscription;
		}

		public EntityQueryEntityFilter<LongTermInvoice, Privilege> privilege() {
			return privilege;
		}

	}

}