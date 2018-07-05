package ru.argustelecom.box.env.telephony.tariff;

import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.BaseJPQLConvertibleDtoFilterModel;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffListMode;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.util.QueryWrapper;
import ru.argustelecom.system.inf.page.PresentationModel;

import static ru.argustelecom.box.env.telephony.tariff.TariffListFilterModel.TariffQueryWrapper;
import static ru.argustelecom.box.env.telephony.tariff.TariffListFilterModel.TariffQueryWrapper.DTYPE;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.CUSTOMER;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.MODE;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.NAME;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.STATE;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.VALID_FROM;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.VALID_TO;


@PresentationModel
public class TariffListFilterModel extends BaseJPQLConvertibleDtoFilterModel<AbstractTariff, TariffQueryWrapper> {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TariffListViewState tariffListVs;

	private TariffQueryWrapper tariffQueryWrapper;

	@Override
	public void buildPredicates(QueryWrapper<AbstractTariff> queryWrapper) {
		Map<String, Object> filterMap = tariffListVs.getFilterMap();
		for (Map.Entry<String, Object> filterEntry : filterMap.entrySet()) {
			if (filterEntry != null) {
				switch (filterEntry.getKey()) {
					case NAME:
						addPredicate(queryWrapper.like(TariffQueryWrapper.NAME,
								String.format("%%%s%%", filterEntry.getValue())));
						break;
					case VALID_FROM:
						addPredicate(
								queryWrapper.greaterOrEqualsThen(TariffQueryWrapper.VALID_FROM, filterEntry.getValue()));
						break;
					case VALID_TO:
						addPredicate(queryWrapper.lessOrEqualsThen(TariffQueryWrapper.VALID_TO, filterEntry.getValue()));
						break;
					case STATE:
						addPredicate(queryWrapper.equals(TariffQueryWrapper.STATE, filterEntry.getValue()));
						break;
					case CUSTOMER:
						addPredicate(queryWrapper.equals(TariffQueryWrapper.CUSTOMER,
								((BusinessObjectDto<? extends Customer>) filterEntry.getValue()).getIdentifiable(em)));
						break;
					case MODE:
						addPredicate(queryWrapper.in(DTYPE,
								TariffListMode.valueOf(filterEntry.getValue().toString()).getDtypes()));
						break;
					default:
						break;
				}
			}
		}
	}

	@Override
	public TariffQueryWrapper getQueryWrapper(boolean isNew) {
		if (isNew) {
			tariffQueryWrapper = new TariffQueryWrapper();
		}
		return tariffQueryWrapper;
	}

	public static class TariffQueryWrapper extends QueryWrapper<AbstractTariff> {

		public static final String ID = "tf.id";
		public static final String NAME = "tf.objectName";
		public static final String STATE = "tf.state";
		public static final String VALID_TO = "tf.validTo";
		public static final String VALID_FROM = "tf.validFrom";
		public static final String CUSTOMER = "tf.customer";
		public static final String DTYPE = "tf.class";

		private static final String SELECT = "tf";
		private static final String FROM = "AbstractTariff tf left join tf.customer";

		public TariffQueryWrapper() {
			super(AbstractTariff.class, SELECT, FROM);
		}
	}


	private static final long serialVersionUID = 139149040276659056L;
}
