package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static com.google.common.base.Preconditions.checkState;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Arrays.asList;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVATION_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSED;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSURE_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.FORMALIZATION;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED_FOR_DEBT;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.max;

import java.time.LocalDateTime;

import org.jboss.logging.Logger;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.inf.nls.LocaleUtils;

/**
 * Описывает сценарии планирования. Сценарии указаны по порядку в процессе убывания приоритета. Самым важным всегда
 * должен быть сценарий явного запрета на планирование. Если ты добавляешь новые сценарии, обязательно необходимо
 * пересчитать изменившиеся приоритеты
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum InvoicePlannerCase {

	// @formatter:off
	
	/**
	 * <strong>Сценарий явного запрета на планирование.</strong>
	 * <p>
	 * Планирование не имеет смысла вследствие нахождения подписки в состоянии, не предполагающем дальнейшую тарификацию
	 * (либо ЕЩЕ не предполагается тарификации, например, если подписка в состоянии оформления; либо УЖЕ не
	 * предполагается тарификации, например, если подписка закрыта).
	 * <p>
	 * Для определения этого сценария должно выполниться ЛЮБОЕ ИЗ условий:
	 * <ul>
	 * <li>Подписка находится в состоянии ожидания закрытия
	 * <li>Подписка уже закрыта. Окончательно и бесповоротно
	 * <li>Подписка находится в состоянии оформления и при этом явно запрещено планировать первичную активацию (этот
	 * кейс применяется при планировании начислений для счетов, когда мы не должны обрабатывать подписки в состоянии
	 * оформления)
	 * </ul>
	 */
	EXPLICIT_PROHIBITION(true) {
		@Override
		protected boolean isComplying(InvoicePlannerConfig config) {
			Subscription subscription = config.subscription();
			
			log.debug("-----");
			log.debugv("Testing {0} case", name());

			boolean subscriptionInTerminalState = testAssertion(
				subscription.inState(asList(CLOSURE_WAITING, CLOSED)), 
				"Subscription in terminal state"
			);
			
			boolean subscriptionIsDraft = testAssertion(
				subscription.inState(FORMALIZATION) && !config.allowPrimaryActivation(),
				"Subscription is draft"
			);
			
			return subscriptionInTerminalState || subscriptionIsDraft;
		}

		@Override
		protected LocalDateTime calculatePlannedStartDate(InvoicePlannerConfig config) {
			// Т.к. планирования не предполагается, то дата планирования очевидно не важна
			return null;
		}
	},

	/**
	 * <strong>Сценарий первичной активации подписки.</strong>
	 * <p>
	 * Подписка никогда раньше не тарифицировалась, нет ни одного инвойса, при этом подписку можно активировать.
	 * Активация может быть немедленной (подписка в оформлении и пользователь нажал на кнопку "активировать") или
	 * отложенной (дата начала действия подписки на момент ее активации была в будущем и, возможно, это будущее наступит
	 * в текущем периоде планирования).
	 * <p>
	 * ВАЖНО! Немедленная активация из состояния "Оформление" возможна только в том случае, если планировщик настроен
	 * соответствующим образом, т.е. allowPrimaryActivation == true. Это сделано потому, что при выставлении счетов не
	 * должны обрабатываться подписки в состоянии оформления, однако при регулярной обработке такие подписки должны
	 * обрабатываться. Соответственно, этим флагом можно управлять поведением планировщика.
	 * <p>
	 * Для определения этого сценария должны выполняться ЛЮБОЕ из условий
	 * <ul>
	 * <li>Подписка находится в состоянии отложенной активации ("ожидание активации")
	 * <li>Подписка находится в оформлении и первичная активация разрешена 
	 * </ul>
	 */
	PRIMARY_ACTIVATION(false) {
		@Override
		protected boolean isComplying(InvoicePlannerConfig config) {
			Subscription subscription = config.subscription();
			
			log.debug("-----");
			log.debugv("Testing {0} case", name());
			
			boolean defferedActivation = testAssertion(
				subscription.inState(ACTIVATION_WAITING),
				"Subscription in deffered activation status"
			);
			
			boolean immediateActivation = testAssertion(
				subscription.inState(FORMALIZATION) && config.allowPrimaryActivation(),
				"Subscription in immediate activation status and primary activation allowed"
			);
			
			return defferedActivation || immediateActivation;
		}

		@Override
		protected LocalDateTime calculatePlannedStartDate(InvoicePlannerConfig config) {
			// При выставлении счетов может возникнуть ситуация, при которой дата активации подписки находится слева от
			// левой границы периода списания, т.е. подписка активируется "раньше". Здесь мы можем рассмотреть три даты:
			// дату активации подписки (subscriptionStart); начальную дату периода планирования; дату начала периода
			// списания, в который попадает дата начала периода планирования (ведь период планирования пользователем
			// указывается явно, поэтому может попасть в середину периода списания).
			//
			// По правилам определения пограничного плана для счета (случай, при котором дата начала периода выставления
			// счета попадает в середину периода списания или дата окончания периода выставления счета попадает в
			// середину периода списания) будут рассматриваться только планы, пересекающиеся с периодом выставления
			// счета, при этом дополнительно анализируется схема выставления (постоплата или предоплата). В любом
			// случае, рассматриваются только приграничные состояния, поэтому не имеет смысл генерировать заведомо
			// отбрасываемые планы.
			//
			// Для этого и определяется maxOfChargingStartAndPoi. Если subscriptionStart подписки меньше чем дата начала
			// первого периода списания, который будет рассмотрен в дальнейшем, то необходимо взять дату начала этого
			// первого периода списания, в противном случае необходимо взять subscriptionStart.
			// Важно, если период планирования неопределен, т.е. не имеет левой границы, то всегда будет использоваться
			// subscriptionStart
			return config.maxOfChargingStartAndPoi(config.subscriptionStart());
		}
	},

	/**
	 * <strong>Cценарий формирования непрерывных начислений по подписке.</strong>
	 * <p>
	 * Подписка тарифицируется в настоящий момент времени, т.е. по ней уже были какие-то инвойсы на определенную сумму и
	 * необходимо продолжить непрерывную тарификацию. При этом выставленный ранее инвойс может быть в любом состоянии
	 * (как открытом в случае генерации планов для счетов, так и в закрытом в случае регулярного процессинга). Для
	 * определения этого сценария должны выполняться ВСЕ условия:
	 * <ul>
	 * <li>Подписка находится в тарифицируемом состоянии
	 * <li>Существует предыдущий инвойс
	 * </ul>
	 */
	CONTINUATION_OF_CHARGING(false) {
		@Override
		protected boolean isComplying(InvoicePlannerConfig config) {
			SubscriptionState state = config.subscription().getState();
			
			log.debug("-----");
			log.debugv("Testing {0} case", name());
			
			if (testAssertion(state.isChargeable(), "Subscription in chargeable state")) {
				InvoicePlan lastPlan = config.lastPlan();
				return testAssertion(lastPlan != null, "Last invoice plan is configured: {0}", lastPlan);
			}
			return false;
		}

		@Override
		protected LocalDateTime calculatePlannedStartDate(InvoicePlannerConfig config) {
			SubscriptionState state = config.subscription().getState();
			InvoicePlan lastPlan = config.lastPlan();

			// Никогда не должно отломаться на этих проверках, т.к. в этом случае будет явная рассогласованность между
			// определением кейса и его применением
			checkState(lastPlan != null);
			checkState(state.isChargeable());

			// Аналогично случаю с первичной активацией, необходимо отфильтровать заведомо ложные планы, для чего
			// необходимо определить максимальную дату от (1) startDate периода списания, в который попадает дата начала
			// периода планирования и (2) даты начала следующего периода списания
			LocalDateTime nextPlanStart = lastPlan.plannedPeriod().endDateTime().plus(1, MILLIS);
			return config.maxOfChargingStartAndPoi(nextPlanStart);
		}
	},

	/**
	 * <strong>Сценарий возобновления списаний после приостановки.</strong>
	 * <p>
	 * Подписка не тарифицируется в настоящий момент времени и для нее необходимо возобновить тарификацию. Возобновление
	 * возможно при выполнении условий
	 * <ul>
	 * <li>Явно указана дата предополагаемого возобновления. Эта дата должна быть передана извне, т.к. если датой
	 * предополагаемого возобновления будет указана текущая дата, то невозможно будет построить ретроспективные планы
	 * или планы на последующие периоды по предоплатной схеме.
	 * <li>Если последний план есть, то он должен быть восстановлен по закрытому инвойсу (т.е. быть isPast).
	 * <li>Если последнего плана нет, то это особый случай, когда при активации было недостаточно средств на лицевом
	 * счете и подписка перешла в состояние приостановки за неуплату без создания инвойса. В этом случае подписка 
	 * должна находиться в приостановке за неуплату
	 * </ul>
	 */
	RENEWAL_OF_CHARGIGN(false) {
		@Override
		protected boolean isComplying(InvoicePlannerConfig config) {
			Subscription subscription = config.subscription();
			LocalDateTime renewalDate = config.renewalDate();
			InvoicePlan lastPlan = config.lastPlan();
			
			log.debug("-----");
			log.debugv("Testing {0} case", name());
			
			// Если не указана дата возобновления или подписка в текущий момент в тарифицируемом состоянии, то это
			// нельзя считать возобновлением тарификации. Скорее всегод должны были среагировать либо на первичную
			// активацию, либо на кейс непрерывной тарификации
			
			boolean renewalDateDefined = testAssertion(
				renewalDate != null, 
				"Renewal date is defined"
			); 
			
			boolean subscriptionIsUnchargeable = testAssertion(
				!subscription.getState().isChargeable(), 
				"Subscription state is unchargeable"
			);
			
			if (!(renewalDateDefined && subscriptionIsUnchargeable)) {
				return false;
			}

			if (lastPlan != null) {
				
				log.debugv("Last invoice plan is configured: {0}", lastPlan);
				return testAssertion(lastPlan.isPast(), "Last invoice plan is past");
				
			} else {
				
				log.debug("Last invoice plan is undefined");
				return testAssertion(
					subscription.inState(SUSPENDED_FOR_DEBT), 
					"Subscription is suspended for debt from primary activation"
				);
				
			}
		}

		@Override
		protected LocalDateTime calculatePlannedStartDate(InvoicePlannerConfig config) {
			LocalDateTime renewalDate = config.renewalDate();
			InvoicePlan lastPlan = config.lastPlan();

			// Никогда не должно отломаться на этих проверках, т.к. в этом случае будет явная рассогласованность между
			// определением кейса и его применением
			checkState(renewalDate != null);
			checkState(!config.subscription().getState().isChargeable());

			if (lastPlan != null) {
				// Аналогично случаю с первичной активацией, только здесь необходимо определить уже максимальную из трех
				// дат: (1) startDate периода списания, в который попадает дата начала периода планирования; (2) даты
				// начала действия следующего инвойса; (3) даты возобновления тарификации
				LocalDateTime lastPlanEnd = lastPlan.plannedPeriod().endDateTime();
				LocalDateTime actualRenewal = max(renewalDate, lastPlanEnd.plus(1, MILLIS)); 
				return config.maxOfChargingStartAndPoi(actualRenewal);
			} else {
				LocalDateTime actualRenewal = max(renewalDate, config.subscriptionStart());
				return config.maxOfChargingStartAndPoi(actualRenewal);
			}
		}
	},

	/**
	 * <strong>Сценарий по-умолчанию.</strong>
	 * 
	 * Определяется всегда, если не смогли определить другой сценарий
	 */
	IMPLICIT_PROHIBITION(true) {
		@Override
		protected boolean isComplying(InvoicePlannerConfig config) {
			log.warnv("Using default {0} case", name());
			return true;
		}

		@Override
		protected LocalDateTime calculatePlannedStartDate(InvoicePlannerConfig config) {
			return null;
		}
	};

	private static final Logger log = Logger.getLogger(InvoicePlannerCase.class);

	/**
	 * Если true, то планирования не будет. На текущий момент этот флаг проставлен для сценариев *_PROHIBITION
	 */
	private boolean prohibited;

	/**
	 * Определяет, соответствуют ли настройки планировщика текущему кейсу или нет
	 * 
	 * @return true если текущий кейс соответствует настройкам планировщика
	 */
	protected abstract boolean isComplying(InvoicePlannerConfig config);

	/**
	 * Определяет дату начала планирования списаний на основании бизнес-правил
	 * 
	 * @return определенную дату начала планирования списаний
	 */
	protected abstract LocalDateTime calculatePlannedStartDate(InvoicePlannerConfig config);

	protected boolean testAssertion(boolean condition, String message, Object... args) {
		String formattedMessage = LocaleUtils.format(message, args);

		if (condition) {
			log.debugv("{0}: TRUE", formattedMessage);
		} else {
			log.debugv("{0}: FALSE", formattedMessage);
		}

		return condition;
	}
}