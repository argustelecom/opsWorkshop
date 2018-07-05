package ru.argustelecom.box.env.billing.mediation;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;

/**
 * Контейнер для агрегированных фактов использования телефонии по
 * {@link ru.argustelecom.box.env.billing.invoice.model.UsageInvoice}, полученных в результате их
 * {@linkplain AssociateUsageInvoicesWithCallsService#associate(List, ChargeJob) связывания}.
 */
@Getter
@AllArgsConstructor
@MappedSuperclass
//@formatter:off
@SqlResultSetMapping(
	name = AggregateCallsByUsageInvoiceQr.AGGREGATE_CALLS_BY_USAGE_INVOICE_QR_MAPPER,
	classes = {
		@ConstructorResult(
			targetClass = AggregateCallsByUsageInvoiceQr.class,
			columns = {
				@ColumnResult(name="invoice_id", type = Long.class),
				@ColumnResult(name="resource_number", type = String.class),
				@ColumnResult(name="telephony_zone_id", type = Long.class),
				@ColumnResult(name="tariff_id", type = Long.class),
				@ColumnResult(name="amount", type = BigDecimal.class)
			}
		)
	})
//@formatter:on
public class AggregateCallsByUsageInvoiceQr {

	public static final String AGGREGATE_CALLS_BY_USAGE_INVOICE_QR_MAPPER = "AggregateCallsByUsageInvoiceQrMapper";

	private Long invoiceId;
	private String resourceNumber;
	private Long telephonyZoneId;
	private Long tariffId;
	private BigDecimal amount;

}