package ru.argustelecom.box.env.mediation;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.env.mediation.UnsuitableCallsListViewModel.UnsuitableCallsContext;
import ru.argustelecom.box.env.mediation.model.MediationError;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.page.PresentationState;

import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.DATE_FROM;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.DATE_TO;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.ERROR;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.INCOMING_CHANNEL;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.CALLED_NUMBER;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.INCOMING_TRUNK;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.FOREIGN_ID;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.OUTGOING_CHANNEL;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.CALLING_NUMBER;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.OUTGOING_TRUNK;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.SERVICE;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.TARIFF;

@PresentationState
@Getter
@Setter
@Named("unsuitableCallsListVs")
public class UnsuitableCallsListViewState extends FilterViewState implements Serializable {

	private static final long serialVersionUID = -4588215508630051921L;

	@FilterMapEntry(ERROR)
	private MediationError error;
	@FilterMapEntry(FOREIGN_ID)
	private String foreignId;
	@FilterMapEntry(DATE_FROM)
	private Date dateFrom;
	@FilterMapEntry(DATE_TO)
	private Date dateTo;
	@FilterMapEntry(CALLING_NUMBER)
	private String callingNumber;
	@FilterMapEntry(OUTGOING_CHANNEL)
	private String outgoingChannel;
	@FilterMapEntry(OUTGOING_TRUNK)
	private String outgoingTrunk;
	@FilterMapEntry(CALLED_NUMBER)
	private String calledNumber;
	@FilterMapEntry(INCOMING_CHANNEL)
	private String incomingChannel;
	@FilterMapEntry(INCOMING_TRUNK)
	private String incomingTrunk;
	@FilterMapEntry(SERVICE)
	private BusinessObjectDto<Service> service;
	@FilterMapEntry(TARIFF)
	private BusinessObjectDto<AbstractTariff> tariff;

	private UnsuitableCallsContext unsuitableCallsContext;

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class UnsuitableCallsFilter {
		public static final String ERROR = "ERROR";
		public static final String FOREIGN_ID = "FOREIGN_ID";
		public static final String DATE_FROM = "DATE_FROM";
		public static final String DATE_TO = "DATE_TO";
		public static final String CALLING_NUMBER = "CALLING_NUMBER";
		public static final String OUTGOING_CHANNEL = "OUTGOING_CHANNEL";
		public static final String OUTGOING_TRUNK = "OUTGOING_TRUNK";
		public static final String CALLED_NUMBER = "CALLED_NUMBER";
		public static final String INCOMING_CHANNEL = "INCOMING_CHANNEL";
		public static final String INCOMING_TRUNK = "INCOMING_TRUNK";
		public static final String SERVICE = "SERVICE";
		public static final String TARIFF = "TARIFF";
	}

}

