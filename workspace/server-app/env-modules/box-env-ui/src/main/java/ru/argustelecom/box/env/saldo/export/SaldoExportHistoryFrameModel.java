package ru.argustelecom.box.env.saldo.export;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ru.argustelecom.box.env.saldo.export.model.SaldoExportIssue;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("saldoExportHistoryFM")
public class SaldoExportHistoryFrameModel implements Serializable {

	private static final long serialVersionUID = -8817928547476490720L;

	@Inject
	private SaldoExportIssueRepository seir;

	private List<SaldoExportIssue> issues;

	private SaldoExportIssue selectedIssue;

	public List<SaldoExportIssue> getIssues() {
		if (issues == null)
			issues = seir.findAllIssues();
		return issues;
	}

	public void resetIssues() {
		issues = null;
	}

	public StreamedContent getStreamedContent(SaldoExportIssue issue) throws Exception {
		if (issue.getFile() == null)
			return null;

		InputStream stream = issue.getFile().getBinaryStream();
		String fileName = issue.getFileName();

		return new DefaultStreamedContent(stream, "txt", fileName);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public SaldoExportIssue getSelectedIssue() {
		return selectedIssue;
	}

	public void setSelectedIssue(SaldoExportIssue selectedIssue) {
		this.selectedIssue = selectedIssue;
	}

}