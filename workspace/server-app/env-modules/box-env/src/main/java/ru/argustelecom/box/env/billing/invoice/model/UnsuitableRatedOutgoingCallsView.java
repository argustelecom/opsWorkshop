package ru.argustelecom.box.env.billing.invoice.model;

import static javax.persistence.AccessType.FIELD;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.hibernate.annotations.Immutable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Immutable
@Table(schema = "system")
@Access(FIELD)
@Getter
@NoArgsConstructor
public class UnsuitableRatedOutgoingCallsView extends BusinessObject {

	@Temporal(TIMESTAMP)
	private Date callDate;

	private BigDecimal duration;

	@Enumerated(STRING)
	@Column(name = "rated_unit")
	private PeriodUnit ratedUnit;

	private Money amount;

	@ManyToOne(optional = false)
	@JoinColumn(name = "tariff_id")
	private AbstractTariff tariff;

	private String resourceNumber;

	@ManyToOne(optional = false)
	@JoinColumn(name = "service_id")
	private Service service;

	@ManyToOne(optional = false)
	@JoinColumn(name = "supplier_id")
	private PartyRole supplier;

	@ManyToOne(optional = false)
	@JoinColumn(name = "telephony_zone_id")
	private TelephonyZone zone;

	public static class UnsuitableRatedOutgoingCallsViewQuery extends EntityQuery<UnsuitableRatedOutgoingCallsView> {

		private final EntityQueryDateFilter<UnsuitableRatedOutgoingCallsView> callDate;
		private final EntityQueryStringFilter<UnsuitableRatedOutgoingCallsView> resourceNumber;
		private final EntityQueryEntityFilter<UnsuitableRatedOutgoingCallsView, Service> service;
		private final EntityQueryEntityFilter<UnsuitableRatedOutgoingCallsView, AbstractTariff> tariff;
		private final EntityQueryEntityFilter<UnsuitableRatedOutgoingCallsView, PartyRole> supplier;
		private final EntityQueryEntityFilter<UnsuitableRatedOutgoingCallsView, TelephonyZone> zone;

		public UnsuitableRatedOutgoingCallsViewQuery() {
			super(UnsuitableRatedOutgoingCallsView.class);
			callDate = createDateFilter(UnsuitableRatedOutgoingCallsView_.callDate);
			resourceNumber = createStringFilter(UnsuitableRatedOutgoingCallsView_.resourceNumber);
			service = createEntityFilter(UnsuitableRatedOutgoingCallsView_.service);
			tariff = createEntityFilter(UnsuitableRatedOutgoingCallsView_.tariff);
			supplier = createEntityFilter(UnsuitableRatedOutgoingCallsView_.supplier);
			zone = createEntityFilter(UnsuitableRatedOutgoingCallsView_.zone);
		}

		public EntityQueryDateFilter<UnsuitableRatedOutgoingCallsView> callDate() {
			return callDate;
		}

		public EntityQueryStringFilter<UnsuitableRatedOutgoingCallsView> resourceNumber() {
			return resourceNumber;
		}

		public EntityQueryEntityFilter<UnsuitableRatedOutgoingCallsView, Service> service() {
			return service;
		}

		public EntityQueryEntityFilter<UnsuitableRatedOutgoingCallsView, AbstractTariff> tariff() {
			return tariff;
		}

		public EntityQueryEntityFilter<UnsuitableRatedOutgoingCallsView, PartyRole> supplier() {
			return supplier;
		}

		public EntityQueryEntityFilter<UnsuitableRatedOutgoingCallsView, TelephonyZone> zone() {
			return zone;
		}
	}

	private static final long serialVersionUID = 8953865961733659210L;
}
