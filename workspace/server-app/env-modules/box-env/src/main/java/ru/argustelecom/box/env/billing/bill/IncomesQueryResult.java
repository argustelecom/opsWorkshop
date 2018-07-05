package ru.argustelecom.box.env.billing.bill;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Контейнер для выборки сырых данных по поступлениям.
 */
@Getter
@AllArgsConstructor
@MappedSuperclass
//@formatter:off
@SqlResultSetMapping(
	name = IncomesQueryResult.INCOMES_QUERY_RESULT_MAPPER,
	classes = {
		@ConstructorResult(
			targetClass = IncomesQueryResult.class,
			columns = {
				@ColumnResult(name="personal_account_id", type = Long.class),
				@ColumnResult(name="transaction_id", type = Long.class),
				@ColumnResult(name="date", type = Date.class),
				@ColumnResult(name="amount", type = BigDecimal.class)
			}
		)
	})
//@formatter:on
public class IncomesQueryResult {

	public static final String INCOMES_QUERY_RESULT_MAPPER = "IncomesQueryResultMapper";

	private Long personalAccountId;
	private Long transactionId;
	private Date date;
	private BigDecimal amount;

}