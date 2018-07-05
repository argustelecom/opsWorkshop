package ru.argustelecom.box.env.billing.bill;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

import lombok.Getter;

/**
 * Контейнер для выборки сырых данных по начислениям.
 */
@Getter
@MappedSuperclass
//@formatter:off
@SqlResultSetMappings({
	@SqlResultSetMapping(
			name = ChargesQueryResult.NONRECURRENT_QUERY_RESULT_MAPPER,
			classes = {
					@ConstructorResult(
							targetClass = ChargesQueryResult.class,
							columns = {
									@ColumnResult(name="invoice_id", type = Long.class),
									@ColumnResult(name="subject_id", type = Long.class),
									@ColumnResult(name="start_date", type = Date.class),
									@ColumnResult(name="end_date", type = Date.class),
									@ColumnResult(name="sum", type = BigDecimal.class),
									@ColumnResult(name="tax_rate", type = BigDecimal.class)
							}
							)
			}),
	@SqlResultSetMapping(
		name = ChargesQueryResult.USAGE_QUERY_RESULT_MAPPER,
		classes = {
			@ConstructorResult(
				targetClass = ChargesQueryResult.class,
				columns = {
					@ColumnResult(name="invoice_id", type = Long.class),
					@ColumnResult(name="subject_id", type = Long.class),
					@ColumnResult(name="start_date", type = Date.class),
					@ColumnResult(name="end_date", type = Date.class),
					@ColumnResult(name="sum", type = BigDecimal.class),
					@ColumnResult(name="tax_rate", type = BigDecimal.class),
					@ColumnResult(name="service_id", type = Long.class),
					@ColumnResult(name="provider_id", type = Long.class),
					@ColumnResult(name="without_contract", type = Boolean.class)
				}
			)
		})
})
//@formatter:on
public class ChargesQueryResult {

	public static final String NONRECURRENT_QUERY_RESULT_MAPPER = "NonRecurrentQueryResultMapper";
	public static final String USAGE_QUERY_RESULT_MAPPER = "UsageQueryResultMapper";

	private Long invoiceId;
	private Long subjectId;
	private Date startDate;
	private Date endDate;
	private BigDecimal sum;
	private BigDecimal taxRate;
	private Long serviceId;
	private Long providerId;
	private boolean withoutContract;

	public ChargesQueryResult(Long invoiceId, Long subjectId, Date startDate, Date endDate, BigDecimal sum,
			BigDecimal taxRate) {
		super();
		this.invoiceId = invoiceId;
		this.subjectId = subjectId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sum = sum;
		this.taxRate = taxRate;
	}

	public ChargesQueryResult(Long invoiceId, Long subjectId, Date startDate, Date endDate, BigDecimal sum,
			BigDecimal taxRate, Long serviceId, Long providerId, boolean withoutContract) {
		this(invoiceId, subjectId, startDate, endDate, sum, taxRate);
		this.serviceId = serviceId;
		this.providerId = providerId;
		this.withoutContract = withoutContract;
	}

}