package ru.argustelecom.box.env.billing.invoice.chargejob;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;

import javax.inject.Named;

import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.billing.invoice.model.JobDataType;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.system.inf.page.PresentationState;

import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobListViewState.ChargeJobFilter.DATA_TYPE;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobListViewState.ChargeJobFilter.MEDIATION_ID;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobListViewState.ChargeJobFilter.STATE;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobListViewState.ChargeJobFilter.VALID_FROM;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobListViewState.ChargeJobFilter.VALID_TO;

@PresentationState
@Getter
@Setter
@Named(value = "chargeJobListVs")
public class ChargeJobListViewState extends FilterViewState implements Serializable {

	private static final long serialVersionUID = 3835834237800070336L;

	@FilterMapEntry(MEDIATION_ID)
	private String mediationId;

	@FilterMapEntry(DATA_TYPE)
	private JobDataType dataType;

	@FilterMapEntry(STATE)
	private ChargeJobState state;

	@FilterMapEntry(VALID_FROM)
	private Date validFrom;

	@FilterMapEntry(VALID_TO)
	private Date validTo;


	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class ChargeJobFilter {
		public static final String MEDIATION_ID = "MEDIATION_ID";
		public static final String DATA_TYPE = "DATA_TYPE";
		public static final String STATE = "STATE";
		public static final String VALID_FROM = "VALID_FROM";
		public static final String VALID_TO = "VALID_TO";
	}

}