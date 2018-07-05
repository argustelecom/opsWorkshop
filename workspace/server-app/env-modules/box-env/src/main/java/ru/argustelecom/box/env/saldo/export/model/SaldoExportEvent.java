package ru.argustelecom.box.env.saldo.export.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.box.inf.modelbase.BusinessObject;

/**
 * Описывает результат некоторого этапа выгрузки реестра Сальдо.
 */
//TODO: поменять на log очереди, когда появится API
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", uniqueConstraints = {
		@UniqueConstraint(name = "uc_saldo_export_issue", columnNames = { "issue_id", "type" }) })
public class SaldoExportEvent extends BusinessObject {

	private static final long serialVersionUID = -1308263669787544421L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date executedDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SaldoExportEventType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SaldoExportEventState state;

	@Column(length = 512)
	private String description;

	protected SaldoExportEvent() {
	}

	public SaldoExportEvent(Long id) {
		super(id);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Date getExecutedDate() {
		return executedDate;
	}

	public void setExecutedDate(Date executedDate) {
		this.executedDate = executedDate;
	}

	public SaldoExportEventType getType() {
		return type;
	}

	public void setType(SaldoExportEventType type) {
		this.type = type;
	}

	public SaldoExportEventState getState() {
		return state;
	}

	public void setState(SaldoExportEventState state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}