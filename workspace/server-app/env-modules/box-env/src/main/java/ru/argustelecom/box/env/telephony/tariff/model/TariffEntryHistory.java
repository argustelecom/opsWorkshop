package ru.argustelecom.box.env.telephony.tariff.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.persistence.AccessType.FIELD;
import static javax.persistence.AccessType.PROPERTY;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.hibernate.types.IntArrayType;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6294812">Слепок класса трафика</a>
 */
@Entity
@Table(schema = "system", uniqueConstraints = @UniqueConstraint(name = "uc_tariff_entry_history", columnNames = {
		"tariff_entry_id", "version" }))
@Immutable
@Access(FIELD)
@TypeDef(name = "int-array", typeClass = IntArrayType.class)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TariffEntryHistory extends BusinessObject {

	/**
	 * Стоимость тарифицируемой единицы времени
	 */
	@AttributeOverride(name = "amount", column = @Column(name = "charge_per_unit"))
	private Money chargePerUnit;

	/**
	 * Зона телефонной нумерации
	 */
	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "zone_id")
	private TelephonyZone zone;

	/**
	 * Список префиксов
	 */
	@Column(name = "prefix", nullable = false, columnDefinition = "integer[]")
	@Type(type = "int-array")
	private List<Integer> prefixes;

	/**
	 * Версия класса трафика, для которой был сделан слепок
	 */
	@Column(nullable = false)
	private Long version;

	/**
	 * Время модификации класса трафика
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;

	/**
	 * Работник, который изменил класс трафика
	 */
	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "employee_id")
	private Employee employee;

	public TariffEntryHistory(Long id, TariffEntry entry, Employee employee) {
		super(id);
		checkNotNull(entry);
		setObjectName(entry.getObjectName());
		this.chargePerUnit = entry.getChargePerUnit();
		this.zone = entry.getZone();
		this.prefixes = entry.getPrefixes();
		this.version = entry.getVersion();
		this.modified = new Date();
		this.employee = checkNotNull(employee);
		entry.addModificationHistory(this);
	}

	@Access(PROPERTY)
	@Column(name = "name", length = 255, nullable = false)
	@Override
	public String getObjectName() {
		return super.getObjectName();
	}

	public static class TariffEntryHistoryQuery extends EntityQuery<TariffEntryHistory> {

		private final EntityQueryEntityFilter<TariffEntryHistory, TelephonyZone> zone;

		public TariffEntryHistoryQuery() {
			super(TariffEntryHistory.class);
			zone = createEntityFilter(TariffEntryHistory_.zone);
		}
	}

	private static final long serialVersionUID = 8199149111496391949L;
}
