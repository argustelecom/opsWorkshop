package ru.argustelecom.box.env.saldo.export.queue;

import ru.argustelecom.box.env.saldo.export.model.SaldoExportIssue;
import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.api.context.EntityReference;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;

public class SaldoExportContext extends Context {

	private static final long serialVersionUID = -4765014104840315590L;

	private EntityReference<SaldoExportIssue> saldoExportReference;

	protected SaldoExportContext(QueueEventImpl event) {
		super(event);
	}

	public SaldoExportContext(SaldoExportIssue saldoExportIssue) {
		super();
		saldoExportReference = new EntityReference<SaldoExportIssue>(saldoExportIssue);
	}

	public SaldoExportIssue getSaldoExportIssue() {
		return saldoExportReference.get();
	}

}
