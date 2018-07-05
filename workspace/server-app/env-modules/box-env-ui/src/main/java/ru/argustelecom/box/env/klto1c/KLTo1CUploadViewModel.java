package ru.argustelecom.box.env.klto1c;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.UploadedFile;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.billing.klto1c.KLto1CLexer;
import ru.argustelecom.box.env.billing.klto1c.KLto1CParser;
import ru.argustelecom.box.env.billing.klto1c.model.ExportData;
import ru.argustelecom.box.env.billing.klto1c.model.PaymentOrder;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "klTo1cVM")
@PresentationModel
public class KLTo1CUploadViewModel extends ViewModel {

	private static final long serialVersionUID = -4588169729474590849L;

	private UploadedFile file;

	private ExportData exportData;

	private Map<String, Long> checkingAccountCountMap = new HashMap<>();

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	public void handleFileUpload(FileUploadEvent event)
			throws IllegalAccessException, IOException, InvocationTargetException {
		file = event.getFile();
		parse();
	}

	public void removeFile() {
		exportData = null;
		checkingAccountCountMap.clear();
		file = null;
	}

	public void parse() throws InvocationTargetException, IllegalAccessException, IOException {
		KLto1CLexer lexer = new KLto1CLexer();
		lexer.init(file.getInputstream(), "cp1251");
		lexer.scan();

		KLto1CParser parser = new KLto1CParser();
		parser.initLexemes(lexer.getLexemes());
		exportData = parser.parse();

		checkingAccountCountMap = exportData.getPaymentOrders().stream()
				.collect(Collectors.groupingBy(PaymentOrder::getPayerCheckingAccount, Collectors.counting()));
	}

	public String onFlowProcess(FlowEvent event) {
		return event.getNewStep();
	}

	public List<String> getCheckingAccounts() {
		return Lists.newArrayList(checkingAccountCountMap.keySet());
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public ExportData getExportData() {
		return exportData;
	}

	public Map<String, Long> getCheckingAccountCountMap() {
		return checkingAccountCountMap;
	}

}