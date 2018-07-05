package ru.argustelecom.box.env.saldo.export;

import static ru.argustelecom.box.env.saldo.export.model.SaldoExportIssueState.FAULTED;
import static ru.argustelecom.box.env.saldo.export.model.SaldoExportIssueState.RESTORED;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.saldo.export.model.SaldoExportIssue;
import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "saldoExportOperationHistoryFM")
@PresentationModel
public class SaldoExportOperationHistoryFrameModel implements Serializable {

	private static final long serialVersionUID = 1927656977071314189L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private QueueProducer queueProducer;

	private SaldoExportIssue issue;

	public void restartExport() {
		queueProducer.restart(issue);
		issue.setState(RESTORED);
		em.merge(issue);
	}

	public boolean canReexport() {
		return issue != null && issue.getState().equals(FAULTED);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public SaldoExportIssue getIssue() {
		return issue;
	}

	public void setIssue(SaldoExportIssue issue) {
		this.issue = issue;
	}

}