package ru.argustelecom.box.env.saldo.export;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.saldo.export.model.SaldoExportParam;

public abstract class SaldoExportEmailsFrameModel implements Serializable {

	private static final long serialVersionUID = 4834187818706648864L;

	@PersistenceContext
	protected EntityManager em;

	protected SaldoExportParam param;

	public void preRender(SaldoExportParam param) {
		if (!Objects.equals(this.param, param))
			this.param = param;
	}

	public abstract void removeEmail(String email);

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public SaldoExportParam getParam() {
		return param;
	}

}