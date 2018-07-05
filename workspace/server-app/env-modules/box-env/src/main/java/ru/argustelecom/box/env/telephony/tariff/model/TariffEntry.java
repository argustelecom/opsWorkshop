package ru.argustelecom.box.env.telephony.tariff.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableList;
import static javax.persistence.AccessType.FIELD;
import static javax.persistence.AccessType.PROPERTY;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffEntry.TariffEntryStatus.ACTIVE;
import static ru.argustelecom.box.inf.utils.Preconditions.checkCollectionState;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.telephony.tariff.HasPrefixes;
import ru.argustelecom.box.env.telephony.tariff.TariffEntryService;
import ru.argustelecom.box.inf.hibernate.types.IntArrayType;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

/**
 * <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6294812">Класс трафика</a>
 */
@Entity
@Table(schema = "system")
@Access(FIELD)
@TypeDef(name = "int-array", typeClass = IntArrayType.class)
@NoArgsConstructor(access = PROTECTED)
public class TariffEntry extends BusinessObject implements HasPrefixes {

	/**
	 * Стоимость тарифицируемой единицы времени
	 */
	@Getter
	@Setter
	@AttributeOverride(name = "amount", column = @Column(name = "charge_per_unit"))
	private Money chargePerUnit;

	/**
	 * Зона телефонной нумерации
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "zone_id")
	private TelephonyZone zone;

	/**
	 * Статус класса трафика
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TariffEntryStatus status;

	/**
	 * Список префиксов
	 */
	@Column(name = "prefix", nullable = false, columnDefinition = "integer[]")
	@Type(type = "int-array")
	private List<Integer> prefixes;

	@Getter
	@Setter
	@Version
	private Long version;

	/**
	 * История модификации данного класса трафика
	 */
	@OneToMany(fetch = LAZY)
	@JoinColumn(name = "tariff_entry_id")
	private List<TariffEntryHistory> modificationHistory = new ArrayList<>();

	@Transient
	private List<Integer> cachedPrefixes;

	@Transient
	private List<TariffEntryHistory> cachedModificationHistory;

	public TariffEntry(Long id, AbstractTariff tariff) {
		super(id);
		checkNotNull(tariff).addEntry(this);
		setStatus(ACTIVE);
	}

	@Column(name = "name", length = 255, nullable = false)
	@Access(PROPERTY)
	@Override
	public String getObjectName() {
		return super.getObjectName();
	}

	/**
	 * Изменение полей сущности производится через {@linkplain TariffEntryService#update}
	 */
	public void update(TelephonyZone zone, String name, List<Integer> prefixes, Money chargePerUnit) {
		this.zone = checkNotNull(zone);
		setObjectName(checkNotNull(name));
		checkState(!checkCollectionState(prefixes, "prefixes").isEmpty()
				&& newHashSet(prefixes).size() == prefixes.size());
		this.chargePerUnit = checkNotNull(chargePerUnit);
		this.prefixes = prefixes;

		evictCachedPrefixes();
		evictCachedTariffEntryHistory();
	}

	public boolean equals(TelephonyZone zone, String name, List<Integer> prefixes, Money chargePerUnit) {
		//@formatter:off
		return new EqualsBuilder()
				.append(getZone().getId(), zone.getId())
				.append(getObjectName(), name)
				.append(getPrefixes(), prefixes)
				.append(getChargePerUnit().toString(), chargePerUnit.toString())
				.isEquals();
		//@formatter:on
	}

	public List<Integer> getPrefixes() {
		if (cachedPrefixes == null) {
			cachedPrefixes = unmodifiableList(prefixes);
		}
		return cachedPrefixes;
	}

	public List<TariffEntryHistory> getModificationHistory() {
		if (cachedModificationHistory == null) {
			cachedModificationHistory = unmodifiableList(modificationHistory);
		}
		return cachedModificationHistory;
	}

	public boolean addModificationHistory(TariffEntryHistory history) {
		boolean contains = modificationHistory.contains(checkNotNull(history));
		if (!contains) {
			modificationHistory.add(history);
			evictCachedTariffEntryHistory();
		}
		return !contains;
	}

	protected void evictCachedPrefixes() {
		cachedPrefixes = null;
	}

	protected void evictCachedTariffEntryHistory() {
		cachedModificationHistory = null;
	}

	public static class TariffEntryQuery extends EntityQuery<TariffEntry> {

		private final EntityQueryStringFilter<TariffEntry> name;
		private final EntityQuerySimpleFilter<TariffEntry, Money> chargePerUnit;
		private final EntityQueryEntityFilter<TariffEntry, TelephonyZone> zone;

		public TariffEntryQuery() {
			super(TariffEntry.class);
			name = createStringFilter(TariffEntry_.objectName);
			zone = createEntityFilter(TariffEntry_.zone);
			chargePerUnit = createFilter(TariffEntry_.chargePerUnit);
		}
	}

	public enum TariffEntryStatus {
		ACTIVE, DEPRECATED
	}

	private static final long serialVersionUID = 418940535247232929L;
}
