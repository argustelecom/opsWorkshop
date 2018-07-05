package ru.argustelecom.box.env.telephony.tariff;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CustomTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TariffState;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationState;

import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.CUSTOMER;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.CUSTOMER_TYPE;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.MODE;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.NAME;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.STATE;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.VALID_FROM;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffFilter.VALID_TO;
import static ru.argustelecom.box.env.telephony.tariff.TariffListViewState.TariffListMode.ALL;

@PresentationState
@Getter
@Setter
@Named(value = "tariffListVs")
public class TariffListViewState extends FilterViewState implements Serializable{

	private static final long serialVersionUID = -6523304189756745761L;

	@FilterMapEntry(NAME)
	private String name;
	@FilterMapEntry(VALID_FROM)
	private Date validFrom;
	@FilterMapEntry(VALID_TO)
	private Date validTo;
	@FilterMapEntry(STATE)
	private TariffState state;
	@FilterMapEntry(value = CUSTOMER_TYPE, translator = CustomerTypeDtoTranslator.class)
	private CustomerTypeDto customerType;
	@FilterMapEntry(value = CUSTOMER)
	private BusinessObjectDto<? extends Customer> customer;
	@FilterMapEntry(value = MODE)
	private String serializableMode;
	private TariffListMode mode;

	public TariffListViewState() {
		super((field, value) -> {
			if (MODE.equals(field.getAnnotation(FilterMapEntry.class).value())) {
				TariffListMode.valueOf(value.toString());
			}
		});
	}

	public TariffListMode getMode() {
		if (mode == null) {
			mode = ALL;
			serializableMode = mode.name();
		}
		return mode;
	}

	public void setMode(TariffListMode mode) {
		if (this.mode != mode) {
			this.mode = mode;
			this.serializableMode = mode.name();
			setCustomerType(null);
			setCustomer(null);
		}
	}

	public class TariffFilter {
		public static final String NAME = "NAME";
		public static final String VALID_FROM = "VALID_FROM";
		public static final String VALID_TO = "VALID_TO";
		public static final String STATE = "STATE";
		public static final String CUSTOMER_TYPE = "CUSTOMER_TYPE";
		public static final String CUSTOMER = "CUSTOMER";
		public static final String MODE = "MODE";
	}

	public enum TariffListMode {

		//@formatter:off
		ALL 	(null, new String[]{ CommonTariff.class.getSimpleName(), CustomTariff.class.getSimpleName() }),
		COMMON 	("ProductManagment_CommonTariffEdit", new String[]{ CommonTariff.class.getSimpleName() }),
		CUSTOM 	("ProductManagment_CustomTariffEdit", new String[]{ CustomTariff.class.getSimpleName() });
		//@formatter:on

		@Getter
		private String permissionId;

		@Getter
		private String[] dtypes;

		TariffListMode(String permissionId, String[] dtypes) {
			this.permissionId = permissionId;
			this.dtypes = dtypes;
		}

		public static List<TariffListMode> getModesForCreation() {
			return Arrays.asList(COMMON, CUSTOM);
		}

		public static TariffListMode determineMode(Class<? extends AbstractTariff> clazz) {
			if (clazz.isAssignableFrom(CommonTariff.class)) {
				return COMMON;
			} else if (clazz.isAssignableFrom(CustomTariff.class)) {
				return CUSTOM;
			} else {
				throw new SystemException("Unsupported PricelistJournalMode");
			}
		}

		public String getName() {
			TariffMessagesBundle messages = LocaleUtils.getMessages(TariffMessagesBundle.class);

			switch (this) {
				case ALL:
					return messages.all();
				case COMMON:
					return messages.common();
				case CUSTOM:
					return messages.custom();
				default:
					throw new SystemException("Unsupported TariffListMode");
			}
		}

	}
}
