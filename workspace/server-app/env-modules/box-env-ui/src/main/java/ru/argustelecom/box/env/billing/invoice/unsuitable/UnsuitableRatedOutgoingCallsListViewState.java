package ru.argustelecom.box.env.billing.invoice.unsuitable;

import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.CALL_DATE_FROM;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.CALL_DATE_TO;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.RESOURCE_NUMBER;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.SERVICE;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.SUPPLIER;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.TARIFF;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.ZONE;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.system.inf.page.PresentationState;

@Getter
@Setter
@PresentationState
@Named(value = "unsuitableRatedOutgoingCallsListVs")
public class UnsuitableRatedOutgoingCallsListViewState extends FilterViewState implements Serializable {

	@FilterMapEntry(CALL_DATE_FROM)
	private Date callDateFrom;

	@FilterMapEntry(CALL_DATE_TO)
	private Date callDateTo;

	@FilterMapEntry(RESOURCE_NUMBER)
	private String resourceNumber;

	@FilterMapEntry(value = TARIFF, isBusinessObjectDto = true)
	private BusinessObjectDto<AbstractTariff> tariff;

	@FilterMapEntry(value = SERVICE, isBusinessObjectDto = true)
	private BusinessObjectDto<Service> service;

	@FilterMapEntry(value = SUPPLIER, isBusinessObjectDto = true)
	private BusinessObjectDto<PartyRole> supplier;

	@FilterMapEntry(value = ZONE, isBusinessObjectDto = true)
	private BusinessObjectDto<TelephonyZone> zone;

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class UnsuitableRatedOutgoingCallsFilter {
		public static final String ID = "ID";
		public static final String CALL_DATE_FROM = "CALL_DATE_FROM";
		public static final String CALL_DATE_TO = "CALL_DATE_TO";
		public static final String RESOURCE_NUMBER = "RESOURCE_NUMBER";
		public static final String SERVICE = "SERVICE";
		public static final String TARIFF = "TARIFF";
		public static final String SUPPLIER = "SUPPLIER";
		public static final String ZONE = "ZONE";
	}

	private static final long serialVersionUID = 6603580313003969314L;
}