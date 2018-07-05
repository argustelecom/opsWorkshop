package ru.argustelecom.box.env.commodity.telephony.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.OptionSpec;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * Спецификация опции телефонии.
 * 
 * Представляет из себя шаблон опции услуги телефонии определенного типа, с предзаданным тарифным планом
 *
 */
@Entity
@Access(AccessType.FIELD)
@SecondaryTable(schema = "system", name = "telephony_option_spec", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
public class TelephonyOptionSpec extends OptionSpec<TelephonyOptionType> {

	private static final long serialVersionUID = -3588340427329538609L;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(table = "telephony_option_spec", name = "tariff_id", nullable = false)
	private AbstractTariff tariff;

	protected TelephonyOptionSpec() {
		super();
	}

	protected TelephonyOptionSpec(Long id) {
		super(id);
	}

	public static class TelephonyOptionSpecQuery extends OptionSpecQuery<TelephonyOptionType, TelephonyOptionSpec> {

		private EntityQueryEntityFilter<TelephonyOptionSpec, AbstractTariff> tariff;

		public TelephonyOptionSpecQuery() {
			super(TelephonyOptionSpec.class);
			tariff = createEntityFilter(TelephonyOptionSpec_.tariff);
		}

		public EntityQueryEntityFilter<TelephonyOptionSpec, AbstractTariff> tariff() {
			return tariff;
		}

	}
}
