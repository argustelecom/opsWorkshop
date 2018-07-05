package ru.argustelecom.box.env.commodity.telephony.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

import static ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionState.ACTIVE;

/**
 * Экземпляр опции телефонии.
 *
 * <p>
 * <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6717460">Описание в Confluence</a>
 * </p>
 */
@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TelephonyOption extends Option<TelephonyOptionType, TelephonyOptionSpec>
		implements LifecycleObject<TelephonyOptionState> {

	/**
	 * Тарифный план, по которому данная опция предоставляется.
	 */
	@Getter
	@Setter
	@ManyToOne(optional = false)
	@JoinColumn(name = "tariff_id")
	private AbstractTariff tariff;

	/**
	 * Состояние услуги.
	 */
	@Getter(onMethod = @__({ @Override }))
	@Setter(onMethod = @__({ @Override }))
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 64)
	private TelephonyOptionState state;

	public TelephonyOption(Long id) {
		super(id);
	}

	public static class TelephonyOptionQuery<I extends TelephonyOption>
			extends OptionQuery<TelephonyOptionType, TelephonyOptionSpec, I> {

		private EntityQueryEntityFilter<I, AbstractTariff> tariff;
		private EntityQuerySimpleFilter<I, TelephonyOptionState> state;

		public TelephonyOptionQuery(Class<I> entityClass) {
			super(entityClass);
			tariff = createEntityFilter(TelephonyOption_.tariff);
			state = createFilter(TelephonyOption_.state);
		}

		public EntityQueryEntityFilter<I, AbstractTariff> tariff() {
			return tariff;
		}

		public EntityQuerySimpleFilter<I, TelephonyOptionState> state() {
			return state;
		}

	}

	private static final long serialVersionUID = -2624774983716079756L;

}