package ru.argustelecom.box.env.billing.bill;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Контейнер для выборки маппинга идентификатора счёта на email адрес клиента.
 */
@Getter
@AllArgsConstructor
@MappedSuperclass
//@formatter:off
@SqlResultSetMapping(
	name = BillEmailQueryResult.BILL_EMAIL_QUERY_RESULT_MAPPER,
	classes = {
		@ConstructorResult(
			targetClass = BillEmailQueryResult.class,
			columns = {
				@ColumnResult(name="bill_id", type = Long.class),
				@ColumnResult(name="email", type = String.class)
			}
		)
	})
//@formatter:on
public class BillEmailQueryResult {

	public static final String BILL_EMAIL_QUERY_RESULT_MAPPER = "BillEmailQueryResultMapper";

	private Long billId;
	private String email;

}