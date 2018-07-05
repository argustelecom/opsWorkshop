package ru.argustelecom.box.env.saldo.export;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.saldo.export.model.SaldoExportParam;

public abstract class SaldoExportParamEmailsEditDialogModel implements Serializable {

	private static final long serialVersionUID = -4857908150895413407L;

	@PersistenceContext
	protected EntityManager em;

	protected SaldoExportParam param;

	protected Set<String> newEmails = new HashSet<>();
	private String newEmail;

	public void execute() {
		clean();
	}

	public void add() {
		newEmails.add(newEmail);
		clean();
	}

	public void cancel() {
		clean();
		newEmails.clear();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void clean() {
		newEmail = null;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public void setParam(SaldoExportParam param) {
		this.param = param;
	}

	public Set<String> getNewEmails() {
		return newEmails;
	}

	public String getNewEmail() {
		return newEmail;
	}

	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}

}