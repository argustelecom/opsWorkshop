package ru.argustelecom.box.env.billing.bill;

import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.ChargesType;

/**
 * Враппер результата выборки ({@link BillGroupsDefaultSearchService}) данных для счётов, по этому врапперу будет
 * собираться {@link ru.argustelecom.box.env.billing.bill.model.BillGroup}. <br/>
 * Специально вынесен и сделан как <b>@MappedSuperClass</b> для того, чтобы можно было повесить
 * <b>@SqlResultSetMapping</b>. Если оставить маппинг над {@link BillGroupsDefaultSearchService}, то упадут
 * <b>@NamedQuery</b> при запуске сервера из за отсутствия данного маппинга.
 */
@Getter
@MappedSuperclass
//@formatter:off
@SqlResultSetMapping(
	name = BillGroupData.BILL_GROUP_DATA_MAPPER,
	classes = {
		@ConstructorResult(
			targetClass = BillGroupData.class,
			columns = {
				@ColumnResult(name="subject_id", type = Long.class),
				@ColumnResult(name="contract_id", type = Long.class),
				@ColumnResult(name="personal_account_id", type = Long.class),
				@ColumnResult(name="provider_id", type = Long.class),
				@ColumnResult(name="broker_id", type = Long.class),
				@ColumnResult(name="customer_id", type = Long.class),
				@ColumnResult(name="start", type = Date.class),
				@ColumnResult(name="end", type = Date.class)
			}
		)
	})
//@formatter:on
public class BillGroupData {

	static final String BILL_GROUP_DATA_MAPPER = "BillGroupDataMapper";

	private Long subjectId;
	private Long contractId;
	private Long personalAccountId;
	private Long providerId;
	private Long brokerId;
	private Long customerId;
	private Date start;
	private Date end;
	@Setter
	private ChargesType chargesType;

	public BillGroupData(Long subjectId, Long contractId, Long personalAccountId, Long providerId, Long brokerId,
			Long customerId, Date start, Date end) {
		super();
		this.subjectId = subjectId;
		this.contractId = contractId;
		this.personalAccountId = personalAccountId;
		this.providerId = providerId;
		this.brokerId = brokerId;
		this.customerId = customerId;
		this.start = start;
		this.end = end;
	}

	PersonalAccountBillGroupKey getPersonalAccountBillGroupKey() {
		return new PersonalAccountBillGroupKey(personalAccountId, providerId, brokerId);
	}

	/**
	 * Ключ уникальности для формирования данных по счёту. По данному ключу будет проводиться групировка
	 * {@linkplain BillGroupData выбранных данных} в {@linkplain ru.argustelecom.box.env.billing.bill.model.BillGroup
	 * группу} для формирования счёта.
	 */
	@Getter
	@AllArgsConstructor
	@EqualsAndHashCode(of = { "personalAccountId", "providerId", "brokerId" })
	class PersonalAccountBillGroupKey {

		private Long personalAccountId;
		private Long providerId;
		private Long brokerId;

	}

}