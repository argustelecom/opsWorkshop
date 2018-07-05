package ru.argustelecom.box.env.billing.bill.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.document.model.Document.MAX_NUMBER_LENGTH;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryNumericFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

// TODO: нужно ли сохранить даты формирования счёта?
/**
 * Сущность описывающая изменения(пересоздание/редактирование) счёта. Можно мыслить как историю счёта или слепки старых
 * версий счёта, содержащих старую информацию.
 */
@Getter
@Setter
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "bill_history", uniqueConstraints = @UniqueConstraint(name = "uc_bill_history", columnNames = {
		"bill_id", "version" }))
public class BillHistoryItem extends BusinessObject {
	/**
	 * Счёт.
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "bill_id", nullable = false, updatable = false)
	private Bill bill;

	/**
	 * Дата формирования счёта.
	 */
	@Temporal(TemporalType.DATE)
	@Column(nullable = false, updatable = false)
	private Date billDate;

	/**
	 * Дата изменения счёта, повлекшее создания текущей версии счёта.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	private Date changedDate;

	/**
	 * Старое значение номера счёта.
	 */
	@Column(length = MAX_NUMBER_LENGTH, nullable = false, updatable = false)
	private String number;

	/**
	 * Старое значение сырых данных счёта.
	 */
	@Embedded
	private AggDataContainer aggDataContainer;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "bill_raw_data_id")
	private BillRawData billRawData;

	/**
	 * Работник внёсший изменения.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	/**
	 * Значение версии счёта.
	 */
	@Column(nullable = false, updatable = false)
	private Long version;

	protected BillHistoryItem() {
	}

	public BillHistoryItem(Long id) {
		super(id);
	}

	@Builder
	public BillHistoryItem(Long id, Bill bill, Employee employee) {
		super(id);

		checkNotNull(bill);
		checkNotNull(employee);
		checkNotNull(bill.getDocumentDate());
		checkNotNull(bill.getDocumentNumber());
		checkNotNull(bill.getBillRawData());
		checkNotNull(bill.getAggDataContainer());
		checkNotNull(bill.getVersion());

		this.bill = bill;
		this.billDate = bill.getDocumentDate();
		this.changedDate = new Date();
		this.number = bill.getDocumentNumber();
		this.billRawData = bill.getBillRawData();
		this.aggDataContainer = AggDataContainer.of(bill.getAggDataContainer().getDataHolder());
		this.employee = employee;
		this.version = bill.getVersion();
	}

	public static class BillHistoryQuery extends EntityQuery<BillHistoryItem> {

		EntityQueryEntityFilter<BillHistoryItem, Bill> bill = createEntityFilter(BillHistoryItem_.bill);
		EntityQueryDateFilter<BillHistoryItem> billDate = createDateFilter(BillHistoryItem_.billDate);
		EntityQueryDateFilter<BillHistoryItem> changedDate = createDateFilter(BillHistoryItem_.changedDate);
		EntityQueryStringFilter<BillHistoryItem> number = createStringFilter(BillHistoryItem_.number);
		EntityQueryEntityFilter<BillHistoryItem, Employee> employee = createEntityFilter(BillHistoryItem_.employee);
		EntityQueryNumericFilter<BillHistoryItem, Long> version = createNumericFilter(BillHistoryItem_.version);

		public BillHistoryQuery() {
			super(BillHistoryItem.class);
		}

		public EntityQueryEntityFilter<BillHistoryItem, Bill> bill() {
			return bill;
		}

		public EntityQueryDateFilter<BillHistoryItem> billDate() {
			return billDate;
		}

		public EntityQueryDateFilter<BillHistoryItem> changedDate() {
			return changedDate;
		}

		public EntityQueryStringFilter<BillHistoryItem> number() {
			return number;
		}

		public EntityQueryEntityFilter<BillHistoryItem, Employee> employee() {
			return employee;
		}

		public EntityQueryNumericFilter<BillHistoryItem, Long> version() {
			return version;
		}

	}

	private static final long serialVersionUID = -1365516731980156247L;

}