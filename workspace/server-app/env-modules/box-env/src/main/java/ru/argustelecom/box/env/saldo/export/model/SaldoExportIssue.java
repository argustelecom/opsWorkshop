package ru.argustelecom.box.env.saldo.export.model;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.box.inf.modelbase.BusinessObject;

/**
 * Класс описывающий результаты выгрузки реестра Сальдо.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", uniqueConstraints = {
		@UniqueConstraint(name = "uc_saldo_export_issue", columnNames = { "number", "export_date" }) })
public class SaldoExportIssue extends BusinessObject {

	private static final long serialVersionUID = -6620855811571190908L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date creationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date exportDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "from_date", nullable = false)
	private Date from;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "to_date", nullable = false)
	private Date to;

	@Column(nullable = false, updatable = false)
	private Long number;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SaldoExportIssueState state;

	@Lob
	private Blob file;

	@OneToMany(targetEntity = SaldoExportEvent.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "issue_id", nullable = false)
	@OrderBy(value = "executedDate desc")
	private List<SaldoExportEvent> events = new ArrayList<>();

	protected SaldoExportIssue() {
	}

	public SaldoExportIssue(Long id) {
		super(id);
	}

	public void addEvent(SaldoExportEvent event) {
		events.add(event);
	}

	public void removeEvent(SaldoExportEvent event) {
		if (events.contains(event))
			events.remove(event);
	}

	public InputStream getBinaryStream() throws SQLException {
		return file != null ? file.getBinaryStream() : null;
	}

	public String getFileName() {
		return String.format("%s_%s.txt", "file_name", String.valueOf(getNumber()));
	}

	public SaldoExportEvent getLastEvent() {
		return getEvents() != null ? getEvents().get(0) : null;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Дата выгрузки реестра Сальдо. В случае обработки очередью служит датой, когда должно быть запущено событие на
	 * формирование выгрузки.
	 */
	public Date getExportDate() {
		return exportDate;
	}

	public void setExportDate(Date exportDate) {
		this.exportDate = exportDate;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public SaldoExportIssueState getState() {
		return state;
	}

	public void setState(SaldoExportIssueState state) {
		this.state = state;
	}

	/**
	 * Сформированная выгрузка.
	 */
	public Blob getFile() {
		return file;
	}

	public void setFile(Blob file) {
		this.file = file;
	}

	/**
	 * Список событий, которые выполнялись при формировании выгрузки. Можно рассматривать как историю выполненных
	 * действий.
	 */
	public List<SaldoExportEvent> getEvents() {
		return Collections.unmodifiableList(events);
	}
}