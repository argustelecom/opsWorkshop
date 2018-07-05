package ru.argustelecom.box.env.commodity.telephony.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import ru.argustelecom.box.env.commodity.model.OptionType;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;

/**
 * Опции свойственные только модулю телефонии.
 *
 * <p>
 * <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6717656">Описание в Confluence</a>
 * </p>
 */
@Entity
@Access(AccessType.FIELD)
public class TelephonyOptionType extends OptionType {

	/**
	 * Коллекция зон телефонной нумерации, для которых опция открывает доступ.
	 */
	//@formatter:off
	@ManyToMany(targetEntity = TelephonyZone.class)
	@JoinTable(schema = "system", name = "telephony_option_type_telephony_zone",
			joinColumns = @JoinColumn(name = "option_type_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "zone_id", referencedColumnName = "id"))
	//@formatter:on
	private List<TelephonyZone> zones = new ArrayList<>();

	protected TelephonyOptionType() {
	}

	public TelephonyOptionType(Long id) {
		super(id);
	}

	/**
	 * Возвращает не изменяемый список зон телефонной нумерации для данного типа опции.
	 */
	public List<TelephonyZone> getZones() {
		return Collections.unmodifiableList(zones);
	}

	/**
	 * Добавляет зону телефонной нумерации для типа опции.
	 */
	public void addZone(TelephonyZone zone) {
		if (zone != null && !zones.contains(zone)) {
			zones.add(zone);
		}
	}

	/**
	 * Удаляет зону телефонной нумерации у типа опции.
	 */
	public void removeZone(TelephonyZone zone) {
		if (zone != null && zones.contains(zone)) {
			zones.remove(zone);
		}
	}

	/**
	 * Удаляет все зоны телефонной нумерации привязанные к типу опции.
	 */
	public void removeZones() {
		zones.clear();
	}

	public static class TelephonyOptionTypeQuery<T extends TelephonyOptionType> extends OptionTypeQuery<T> {

		public TelephonyOptionTypeQuery(Class<T> entityClass) {
			super(entityClass);
		}

	}

	private static final long serialVersionUID = 5210045060007269798L;

}