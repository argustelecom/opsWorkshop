package ru.argustelecom.box.env.saldo.export.model;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.modelbase.BusinessObject;

/**
 * Класс для хранения параметров выгрузки реестра Сальдо.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
public class SaldoExportParam extends BusinessObject {

	private static final long serialVersionUID = 8904068490618083526L;

	public static final Long PARAM_ID = 1L;

	@Enumerated(EnumType.STRING)
	private PeriodUnit periodUnit;

	@Enumerated(EnumType.STRING)
	private CalculationType calculationType;

	@Temporal(TemporalType.TIME)
	private Date executeTime;

	private Boolean working;

	@ElementCollection
	@CollectionTable(name = "email_for_success_notification", joinColumns = @JoinColumn(name = "saldo_export_param_id"))
	@Column(name = "value")
	private List<String> emailsForSuccess = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "email_for_error_notification", joinColumns = @JoinColumn(name = "saldo_export_param_id"))
	@Column(name = "value")
	private List<String> emailsForError = new ArrayList<>();

	protected SaldoExportParam() {
	}

	public LocalDateTime getExportDate(LocalDateTime poi) {
		LocalDateTime exportDate = calculationType.getExportDate(periodUnit, poi);
		return addExecuteTime(exportDate);
	}

	public LocalDateTime getNextExportDate(LocalDateTime poi) {
		LocalDateTime nextExportDate = calculationType.getNextExportDate(periodUnit, poi);
		return addExecuteTime(nextExportDate);
	}

	public Range<LocalDateTime> getRange(LocalDateTime poi) {
		return calculationType.getRange(periodUnit, poi);
	}

	public void addEmailForSuccessMsg(String... emails) {
		Arrays.stream(emails).forEach(email -> {
			if (!emailsForSuccess.contains(email))
				emailsForSuccess.add(email);
		});
	}

	public void removeEmailForSuccessMsg(String value) {
		if (emailsForSuccess.contains(value))
			emailsForSuccess.remove(value);
	}

	public void addEmailForErrorMsg(String... emails) {
		Arrays.stream(emails).forEach(email -> {
			if (!emailsForError.contains(email))
				emailsForError.add(email);
		});
	}

	public void removeEmailForErrorMsg(String value) {
		if (emailsForError.contains(value))
			emailsForError.remove(value);
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private LocalDateTime addExecuteTime(LocalDateTime exportDate) {
		LocalTime time = executeTime instanceof Time ? ((Time) executeTime).toLocalTime()
				: new Time(executeTime.getTime()).toLocalTime();
		return exportDate.withHour(time.getHour()).withMinute(time.getMinute());
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public PeriodUnit getPeriodUnit() {
		return periodUnit;
	}

	public void setPeriodUnit(PeriodUnit periodUnit) {
		this.periodUnit = periodUnit;
	}

	public CalculationType getCalculationType() {
		return calculationType;
	}

	public void setCalculationType(CalculationType calculationType) {
		this.calculationType = calculationType;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	/**
	 * Флаг иформирующий, о том запушена ли очередь на выгрузку реестра Сальдо.
	 */
	public Boolean getWorking() {
		return working;
	}

	public void setWorking(Boolean working) {
		this.working = working;
	}

	public List<String> getEmailsForSuccess() {
		return Collections.unmodifiableList(emailsForSuccess);
	}

	public void setEmailsForSuccess(List<String> emailsForSuccess) {
		this.emailsForSuccess = emailsForSuccess;
	}

	public List<String> getEmailsForError() {
		return Collections.unmodifiableList(emailsForError);
	}

	public void setEmailsForError(List<String> emailsForError) {
		this.emailsForError = emailsForError;
	}

}