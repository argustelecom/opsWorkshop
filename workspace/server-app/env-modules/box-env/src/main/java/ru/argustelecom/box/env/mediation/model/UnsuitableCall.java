package ru.argustelecom.box.env.mediation.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import static ru.argustelecom.box.env.mediation.model.ReleaseCode.NO_ANSWER;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Table(schema = "mediation", name = "unsuitable_data_view")
public class UnsuitableCall implements Identifiable {

	@Id
	@Column(name = "call_id")
	private Long callId;

	@Column(name = "error_type")
	@Enumerated(value = EnumType.STRING)
	private MediationError errorType;

	@Column(name = "error_msg")
	private String errorMsg;

	@Column(name = "processing_stage")
	@Enumerated(value = EnumType.STRING)
	private ProcessingStage processingStage;

	@Column(name = "charge_job_id")
	private String chargeJobId;

	@Column(name = "raw_call_date")
	private Date rawCallDate;

	@Column(name = "raw_duration")
	private String rawDuration;

	@Column(name = "foreign_id")
	private String foreignId;

	@Column(name = "source")
	private String source;

	@Setter
	@Column(name = "call_date")
	private Date callDate;

	@Setter
	@Column(name = "duration")
	private Long duration;

	@Setter
	@Column(name = "cdr_unit")
	@Enumerated(EnumType.STRING)
	private PeriodUnit cdrUnit;

	@Column(name = "release_code")
	private String releaseCode;

	@Setter
	@Column(name = "call_direction")
	private String callDirection;

	@Setter
	@Column(name = "calling_number")
	private String callingNumber;

	@Setter
	@Column(name = "called_number")
	private String calledNumber;

	@Setter
	@Column(name = "outgoing_channel")
	private String outgoingChannel;

	@Setter
	@Column(name = "outgoing_trunk")
	private String outgoingTrunk;

	@Setter
	@Column(name = "incoming_channel")
	private String incomingChannel;

	@Setter
	@Column(name = "incoming_trunk")
	private String incomingTrunk;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_id")
	private Service service;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tariff_id")
	private AbstractTariff tariff;

	@Column(name = "incoming_supplier")
	private Long incomingSupplier;

	@Column(name = "outgoing_supplier")
	private Long outgoingSupplier;

	@Column(name = "identified_by")
	private String identifiedBy;

	@Override
	public Long getId() {
		return getCallId();
	}

	//FIXME см. ReleaseCode
	public ReleaseCode getReleaseCode() {
		for (ReleaseCode code : ReleaseCode.values()) {
			if (code.getNameToGetValue().equals(releaseCode)) {
				return code;
			}
		}
		return NO_ANSWER;
	}

	//FIXME см. ReleaseCode
	public void setReleaseCode(ReleaseCode code) {
		this.releaseCode = code.getNameToGetValue();
	}

	@Getter
	public static class UnsuitableCallQuery extends EntityQuery<UnsuitableCall> {
		private EntityQuerySimpleFilter<UnsuitableCall, String> foreignId;
		private EntityQueryDateFilter<UnsuitableCall> callDate;
		private EntityQuerySimpleFilter<UnsuitableCall, ProcessingStage> processingStage;
		private EntityQuerySimpleFilter<UnsuitableCall, String> callingNumber;
		private EntityQuerySimpleFilter<UnsuitableCall, String> outgoingChannel;
		private EntityQuerySimpleFilter<UnsuitableCall, String> outgoingTrunk;
		private EntityQuerySimpleFilter<UnsuitableCall, String> calledNumber;
		private EntityQuerySimpleFilter<UnsuitableCall, String> incomingChannel;
		private EntityQuerySimpleFilter<UnsuitableCall, String> incomingTrunk;
		private EntityQueryEntityFilter<UnsuitableCall, Service> service;
		private EntityQueryEntityFilter<UnsuitableCall, AbstractTariff> tariff;
		private EntityQuerySimpleFilter<UnsuitableCall, MediationError> errorType;

		public UnsuitableCallQuery() {
			super(UnsuitableCall.class);
			foreignId = createFilter(UnsuitableCall_.foreignId);
			callDate = createDateFilter(UnsuitableCall_.callDate);
			callingNumber = createFilter(UnsuitableCall_.callingNumber);
			outgoingChannel = createFilter(UnsuitableCall_.outgoingChannel);
			outgoingTrunk = createFilter(UnsuitableCall_.outgoingTrunk);
			calledNumber = createFilter(UnsuitableCall_.calledNumber);
			incomingChannel = createFilter(UnsuitableCall_.incomingChannel);
			incomingTrunk = createFilter(UnsuitableCall_.incomingTrunk);
			service = createEntityFilter(UnsuitableCall_.service);
			tariff = createEntityFilter(UnsuitableCall_.tariff);
			errorType = createFilter(UnsuitableCall_.errorType);
			processingStage = createFilter(UnsuitableCall_.processingStage);
		}
	}
}
