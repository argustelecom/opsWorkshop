package ru.argustelecom.box.env.pricing;

import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.CUSTOMER;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.CUSTOMER_TYPE;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.MODE;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.NAME;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.OWNER;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.SEGMENT;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.STATE;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.VALID_FROM;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.VALID_TO;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistJournalMode.ALL;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerDtoTranslator;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.CommonPricelist;
import ru.argustelecom.box.env.pricing.model.CustomPricelist;
import ru.argustelecom.box.env.pricing.model.PricelistState;
import ru.argustelecom.box.env.pricing.nls.PricelistMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationState;

@PresentationState
@Getter
@Setter
public class PricelistJournalViewState extends FilterViewState implements Serializable {

	private static final long serialVersionUID = -1991863321911006457L;

	@FilterMapEntry(NAME)
	private String name;
	@FilterMapEntry(VALID_FROM)
	private Date validFrom;
	@FilterMapEntry(VALID_TO)
	private Date validTo;
	@FilterMapEntry(STATE)
	private PricelistState state;
	@FilterMapEntry(value = SEGMENT, translator = CustomerSegmentDtoTranslator.class)
	private CustomerSegmentDto segment;
	@FilterMapEntry(value = CUSTOMER_TYPE, translator = CustomerTypeDtoTranslator.class)
	private CustomerTypeDto customerType;
	@FilterMapEntry(value = CUSTOMER, translator = CustomerDtoTranslator.class)
	private CustomerDto customer;
	@FilterMapEntry(value = MODE)
	private String serializableMode;
	private PricelistJournalMode mode;
	@FilterMapEntry(value = OWNER, isBusinessObjectDto = true)
	private BusinessObjectDto<Owner> owner;

	public PricelistJournalViewState() {
		super((field, value) -> {
			if (MODE.equals(field.getAnnotation(FilterMapEntry.class).value())) {
				PricelistJournalMode.valueOf(value.toString());
			}
		});
	}

	public PricelistJournalMode[] getModes() {
		return PricelistJournalMode.values();
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public PricelistJournalMode getMode() {
		if (mode == null) {
			mode = ALL;
			serializableMode = mode.name();
		}
		return mode;
	}

	public void setMode(PricelistJournalMode mode) {
		if (this.mode != mode) {
			this.mode = mode;
			this.serializableMode = mode.name();
			setSegment(null);
			setCustomerType(null);
			setCustomer(null);
		}
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public class PricelistFilter {
		public static final String NAME = "NAME";
		public static final String VALID_FROM = "VALID_FROM";
		public static final String VALID_TO = "VALID_TO";
		public static final String STATE = "STATE";
		public static final String SEGMENT = "SEGMENT";
		public static final String CUSTOMER_TYPE = "CUSTOMER_TYPE";
		public static final String CUSTOMER = "CUSTOMER";
		public static final String MODE = "MODE";
		public static final String OWNER = "OWNER";
	}

	public enum PricelistJournalMode {

		//@formatter:off
		ALL 	(null, new String[]{ CommonPricelist.class.getSimpleName(), CustomPricelist.class.getSimpleName() }),
		COMMON 	("ProductManagment_CommonPriceListEdit", new String[]{ CommonPricelist.class.getSimpleName() }),
		CUSTOM 	("ProductManagment_CustomPriceListEdit", new String[]{ CustomPricelist.class.getSimpleName() });
		//@formatter:on

		@Getter
		private String permissionId;

		@Getter
		private String[] dtypes;

		PricelistJournalMode(String permissionId, String[] dtypes) {
			this.permissionId = permissionId;
			this.dtypes = dtypes;
		}

		public static List<PricelistJournalMode> getModesForCreation() {
			return Arrays.asList(COMMON, CUSTOM);
		}

		public static PricelistJournalMode determineMode(Class<? extends AbstractPricelist> clazz) {
			if (clazz.isAssignableFrom(CommonPricelist.class)) {
				return COMMON;
			} else if (clazz.isAssignableFrom(CustomPricelist.class)) {
				return CUSTOM;
			} else {
				throw new SystemException("Unsupported PricelistJournalMode");
			}
		}

		public String getName() {
			PricelistMessagesBundle messages = LocaleUtils.getMessages(PricelistMessagesBundle.class);

			switch (this) {
			case ALL:
				return messages.all();
			case COMMON:
				return messages.common();
			case CUSTOM:
				return messages.custom();
			default:
				throw new SystemException("Unsupported PricelistJournalMode");
			}
		}

	}

}