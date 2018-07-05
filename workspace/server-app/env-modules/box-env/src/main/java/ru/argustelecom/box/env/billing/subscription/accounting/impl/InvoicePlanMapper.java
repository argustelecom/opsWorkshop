package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static com.google.common.base.Preconditions.checkState;
import static java.time.temporal.ChronoUnit.MILLIS;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Range;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.period.PeriodBuilderService;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanPeriod;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanTimeline;
import ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlanImpl.InvoicePlannedPeriod;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;
import ru.argustelecom.box.env.stl.period.PeriodUnit;

@RequiredArgsConstructor
public class InvoicePlanMapper {

	private static final String FIELD_ROUNDING_POLICY = "rp";
	private static final String FIELD_SUMMARY = "summary";
	private static final String FIELD_DETAILS = "details";

	private static final String FIELD_START = "start";
	private static final String FIELD_END = "end";
	private static final String FIELD_MODIFIER_ID = "modId";
	private static final String FIELD_COST = "cost";
	private static final String FIELD_COST_TOTAL = "total";

	@NonNull
	private LongTermInvoice context;

	private ChargingPeriod chargingPeriod;

	/**
	 * Сохраняет указанный план в JsonNode. При сохранении не используется Invoice и не сохраняются занчения, которые и
	 * так явно хранятся в инвойсе в реляционной структуре данных, т.е. сохраняется только детализация плана.
	 */
	public void saveInvoicePlan(InvoicePlan plan, ObjectNode rootNode) {
		checkRequiredArgument(plan, "invoicePlan");
		checkRequiredArgument(rootNode, "rootNode");

		JsonHelper.STRING.set(rootNode, FIELD_ROUNDING_POLICY, plan.roundingPolicy().name());

		ObjectNode summaryNode = rootNode.putObject(FIELD_SUMMARY);
		saveInvoicePlanPeriod(plan.summary(), summaryNode);

		if (needToSaveDetails(plan)) {
			ArrayNode detailsList = rootNode.putArray(FIELD_DETAILS);
			plan.forEach(detail -> {
				ObjectNode detailNode = detailsList.addObject();
				saveInvoicePlanPeriod(detail, detailNode);
			});
		}
	}

	/**
	 * Восстанавливает план из JsonNode. Т.к. при сохранении использовали только детализацию, то все недостающие
	 * параметры плана без всякой валидации будут определены из инвойса, переданного в конструктор этого класса. Таким
	 * образом, внутреннее состояние инвойса разрешено менять только через его план.
	 */
	public InvoicePlan loadInvoicePlan(ObjectNode rootNode) {
		checkRequiredArgument(rootNode, "rootNode");

		String storedPolicy = JsonHelper.STRING.get(rootNode, FIELD_ROUNDING_POLICY);
		RoundingPolicy roundingPolicy = storedPolicy != null ? RoundingPolicy.valueOf(storedPolicy) : roundingPolicy();

		InvoicePlanPeriodImpl summary = loadSummary(rootNode);
		List<InvoicePlanPeriodImpl> details = loadDetails(rootNode, summary);

		return new InvoicePlanImpl(chargingPeriod(), roundingPolicy, plannedPeriod(), invoiceId(), timeline(), summary,
				details);
	}

	// ***********************************************************************************************************

	private void saveInvoicePlanPeriod(InvoicePlanPeriod period, ObjectNode periodNode) {
		JsonHelper.DATE.set(periodNode, FIELD_START, period.startDate());
		JsonHelper.DATE.set(periodNode, FIELD_END, period.endDate());

		if (period.modifier() != null) {
			JsonHelper.LONG.set(periodNode, FIELD_MODIFIER_ID, period.modifier().getId());
		}

		JsonHelper.STRING.set(periodNode, FIELD_COST, period.cost().getAmount().toString());
		JsonHelper.STRING.set(periodNode, FIELD_COST_TOTAL, period.totalCost().getAmount().toString());
	}

	private boolean needToSaveDetails(InvoicePlan plan) {
		// Если деталь одна и без модификатора, то ее можно восстановить по суммари, т.к. она ничем от него отличаться
		// не будет (это > 80% всех инвойсов). Во всех остальных случаях детали нужно хранить и восстанавливать явно
		// Такая оптимизация позволит колоссально сократить размеры таблицы инвойсов
		List<InvoicePlanPeriod> details = plan.details();
		return details.size() > 1 || details.stream().anyMatch(p -> p.modifier() != null);
	}

	private InvoicePlanPeriodImpl loadSummary(ObjectNode rootNode) {
		JsonNode summaryNode = rootNode.get(FIELD_SUMMARY);
		checkState(summaryNode != null && summaryNode.isObject());
		return loadInvoicePlanPeriod((ObjectNode) summaryNode);
	}

	private List<InvoicePlanPeriodImpl> loadDetails(ObjectNode rootNode, InvoicePlanPeriodImpl summary) {
		JsonNode detailsList = rootNode.get(FIELD_DETAILS);

		if (detailsList != null) {
			checkState(detailsList.isArray());

			final List<InvoicePlanPeriodImpl> result = new ArrayList<>();
			((ArrayNode) detailsList).forEach(detail -> {
				checkState(detail.isObject());
				result.add(loadInvoicePlanPeriod((ObjectNode) detail));
			});

			result.sort(InvoicePlanPeriod.ascendingOrder());
			return result;
		}

		// @formatter:off
		InvoicePlanPeriodImpl detail = new InvoicePlanPeriodImpl(
			summary.boundaries(), 
			summary.baseUnitCount(),
			summary.baseUnitCost(), 
			summary.cost(), 
			null, // при восстановлении детальной информации по итоговой модификатора нет  
			summary.totalCost(), 
			summary.deltaCost()
		);
		// @formatter:on
		return Collections.singletonList(detail);
	}

	private InvoicePlanPeriodImpl loadInvoicePlanPeriod(ObjectNode periodNode) {
		Date startDate = JsonHelper.DATE.get(periodNode, FIELD_START);
		Date endDate = JsonHelper.DATE.get(periodNode, FIELD_END);
		Long modifierId = JsonHelper.LONG.get(periodNode, FIELD_MODIFIER_ID);
		String storedCost = JsonHelper.STRING.get(periodNode, FIELD_COST);
		String storedTotalCost = JsonHelper.STRING.get(periodNode, FIELD_COST_TOTAL);

		InvoicePlanModifier modifier = null;
		if (modifierId != null) {
			modifier = context.resolveModifier(modifierId);
			checkState(modifier != null);
		}

		Money cost = new Money(storedCost);
		Money totalCost = new Money(storedTotalCost);
		Money deltaCost = Objects.equals(cost, totalCost) ? Money.ZERO : totalCost.subtract(cost);
		Money baseUnitCost = chargingPeriod().baseUnitCost();

		Range<LocalDateTime> boundaries = Range.closed(toLocalDateTime(startDate), toLocalDateTime(endDate));

		PeriodUnit baseUnit = chargingPeriod().getType().getBaseUnit();
		Long baseUnitCount = baseUnit.between(boundaries.lowerEndpoint(), boundaries.upperEndpoint().plus(1, MILLIS));

		return new InvoicePlanPeriodImpl(boundaries, baseUnitCount, baseUnitCost, cost, modifier, totalCost, deltaCost);
	}

	private ChargingPeriod chargingPeriod() {
		if (chargingPeriod == null) {
			chargingPeriod = PeriodBuilderService.chargingOf(context);
		}
		return chargingPeriod;
	}

	private RoundingPolicy roundingPolicy() {
		return context.getRoundingPolicy();
	}

	private InvoicePlannedPeriod plannedPeriod() {
		LocalDateTime lowerBound = toLocalDateTime(context.getStartDate());
		LocalDateTime upperBound = toLocalDateTime(context.getEndDate());
		return new InvoicePlannedPeriod(Range.closed(lowerBound, upperBound));
	}

	private Long invoiceId() {
		return context.getId();
	}

	private InvoicePlanTimeline timeline() {
		return context.getTimeline();
	}
}
