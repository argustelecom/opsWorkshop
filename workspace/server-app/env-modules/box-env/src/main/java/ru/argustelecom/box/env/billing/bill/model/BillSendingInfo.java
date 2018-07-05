package ru.argustelecom.box.env.billing.bill.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.modelbase.Identifiable;

/**
 * Информация об отправке счета
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "bill_sending_info")
public class BillSendingInfo implements Identifiable, Serializable {

	private static final long serialVersionUID = -6570157772949504370L;

	@Setter
	private Long id;

	@Getter
	@Setter
	@MapsId
	@JoinColumn(name = "id", nullable = false)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
	private Bill bill;

	@Getter
	@Setter
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendingDate;

	@Getter
	@Setter
	@Column(nullable = false)
	private String email;

	@Override
	@Id
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return id;
	}

	protected BillSendingInfo() {
		super();
	}

	public BillSendingInfo(Bill bill, Date sendingDate, String email) {
		this.bill = bill;
		this.sendingDate = sendingDate;
		this.email = email;
	}

	public static class BillSendingInfoQuery extends EntityQuery<BillSendingInfo> {

		private EntityQueryEntityFilter<BillSendingInfo, Bill> bill = createEntityFilter(BillSendingInfo_.bill);
		private EntityQueryDateFilter<BillSendingInfo> sendingDate = createDateFilter(BillSendingInfo_.sendingDate);

		public BillSendingInfoQuery() {
			super(BillSendingInfo.class);
		}

		public EntityQueryEntityFilter<BillSendingInfo, Bill> bill() {
			return bill;
		}

		public EntityQueryDateFilter<BillSendingInfo> sendingDate() {
			return sendingDate;
		}

	}
}
