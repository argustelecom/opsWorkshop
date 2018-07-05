package ru.argustelecom.box.env.saldo.imp;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static ru.argustelecom.box.env.saldo.imp.RegisterImportViewModel.Step.EDIT;
import static ru.argustelecom.box.env.saldo.imp.RegisterImportViewModel.Step.IMPORT_FINISHED;
import static ru.argustelecom.box.env.saldo.imp.RegisterImportViewModel.Step.PARSE;
import static ru.argustelecom.box.env.saldo.imp.RegisterImportViewModel.Step.UPLOAD;
import static ru.argustelecom.box.env.saldo.imp.model.ResultType.REQUIRED_CORRECTION;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.UploadedFile;

import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.saldo.imp.model.Container;
import ru.argustelecom.box.env.saldo.imp.model.ED108Register;
import ru.argustelecom.box.env.saldo.imp.model.Register;
import ru.argustelecom.box.env.saldo.imp.model.RegisterContext;
import ru.argustelecom.box.env.saldo.imp.model.RegisterException;
import ru.argustelecom.box.env.saldo.imp.model.RegisterItem;
import ru.argustelecom.box.env.saldo.imp.model.SaldoRegister;
import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;
import ru.argustelecom.system.inf.utils.CDIHelper;

@Named(value = "registerImportVM")
@PresentationModel
public class RegisterImportViewModel extends ViewModel {

	private static final long serialVersionUID = -765917886893665857L;

	private RegisterImportService registerImportService;
	private RegisterFormat selectedRegisterFormat;
	private RegisterContext registerContext;

	private UploadedFile file;
	private String fileName;
	private InputStream registerStream;

	private List<RegisterItemAdapter> notSuitableItems = new ArrayList<>();

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	public RegisterFormat[] getRegisterFormats() {
		return RegisterFormat.values();
	}

	public RegisterFormat getSelectedRegisterFormat() {
		return selectedRegisterFormat;
	}

	public void setSelectedRegisterFormat(RegisterFormat selectedRegisterFormat) {
		this.selectedRegisterFormat = selectedRegisterFormat;
	}

	public void handleFileUpload(FileUploadEvent event) throws IOException {
		file = event.getFile();
		fileName = new String(file.getFileName().getBytes(), "UTF-8");
		registerStream = event.getFile().getInputstream();
	}

	public void removeFile() {
		file = null;
		fileName = null;
		registerStream = null;
		registerImportService = null;
		selectedRegisterFormat = null;
	}

	public String handleRegisterImportFlow(FlowEvent event) throws IOException {
		String newStepId = event.getNewStep();

		if (newStepId.equals(UPLOAD.getId())) {
			executeUploadStep();
		}

		else if (newStepId.equals(PARSE.getId())) {
			String resultParseStep = executeParseStep(event.getOldStep());
			if (!resultParseStep.isEmpty())
				return resultParseStep;
		}

		else if (newStepId.equals(EDIT.getId())) {
			String resultEditStep = executeEditStep(event.getOldStep());
			if (!resultEditStep.isEmpty())
				return resultEditStep;
		}

		else if (newStepId.equals(IMPORT_FINISHED.getId())) {
			executeImportFinishedStep();
		}

		else {
			throw new SystemException("Unsupported step");
		}

		return newStepId;
	}

	public Callback<RegisterItemAdapter> getEditCallback() {
		return itemAdapter -> {
			getRegister().moveToSuitableContainer(itemAdapter.getValue());
			notSuitableItems.remove(itemAdapter);
		};
	}

	public List<Container> getContainers() {
		return registerContext != null && registerContext.getRegister() != null
				? registerContext.getRegister().getContainers() : Collections.emptyList();
	}

	public Register getRegister() {
		return registerContext != null ? registerContext.getRegister() : null;
	}

	public List<Container> getNotEmptyContainers() {
		return getContainers().stream().filter(container -> container.getItems().size() > 0)
				.collect(Collectors.toList());
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void executeUploadStep() {
		removeFile();
	}

	private String executeParseStep(String oldStep) throws IOException {
		if (!checkFormat().isEmpty() || !checkFile().isEmpty())
			return UPLOAD.getId();

		if (UPLOAD.getId().equals(oldStep)) {
			initRegister();

			if (!runRegisterProcess())
				return UPLOAD.getId();
		}

		return EMPTY;
	}

	private String checkFile() {
		if (registerStream == null) {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			SaldoImportMessagesBundle saldoImportMessages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);

			Notification.error(overallMessages.error(), saldoImportMessages.fileIsNotSpecified());
			return UPLOAD.getId();
		}
		return EMPTY;
	}

	private String checkFormat() {
		if (selectedRegisterFormat == null) {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			SaldoImportMessagesBundle saldoImportMessages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);

			Notification.error(
					overallMessages.error(),
					saldoImportMessages.specifyFileType()
			);
			return UPLOAD.getId();
		}
		return EMPTY;
	}

	private void initRegister() {
		registerImportService = (RegisterImportService) CDIHelper.lookupCDIBean(selectedRegisterFormat.getClazz());
		switch (selectedRegisterFormat) {
		case SALDO:
			registerContext = new RegisterContext(new SaldoRegister());
			break;
		case ED108:
			registerContext = new RegisterContext(new ED108Register());
			break;
		default:
			throw new SystemException("Unsupported register format");
		}
	}

	private boolean runRegisterProcess() throws IOException {
		try {
			registerImportService.process(registerContext, registerStream);
			notSuitableItems.clear();
			initNotSuitableList();
		} catch (RegisterException sre) {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);

			Notification.error(overallMessages.error(), sre.getMessage());
			removeFile();
			return false;
		}
		return true;
	}

	private String executeEditStep(String oldStep) {
		if (IMPORT_FINISHED.getId().equals(oldStep)) {
			removeFile();
			notSuitableItems.clear();
			return UPLOAD.getId();
		}
		if (notSuitableItems.isEmpty()) {
			executeImportFinishedStep();
			return IMPORT_FINISHED.getId();
		}
		return EMPTY;
	}

	private void executeImportFinishedStep() {
		registerImportService.importing(registerContext);
	}

	private void initNotSuitableList() {
		getContainers().forEach(container -> {
			if (container.getType().equals(REQUIRED_CORRECTION)) {
				container.getItems().forEach(
						item -> notSuitableItems.add(new RegisterItemAdapter(item, container.getErrorsDescription())));
			}
		});
		Collections.sort(notSuitableItems, (o1, o2) -> o1.getError().compareTo(o2.getError()));
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public UploadedFile getFile() {
		return file;
	}

	public String getFileName() {
		return fileName;
	}

	public List<RegisterItemAdapter> getNotSuitableItems() {
		return notSuitableItems;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public enum Step {

		//@formatter:off
		UPLOAD              ("upload"),
		PARSE               ("parse"),
		EDIT                ("edit"),
		IMPORT_FINISHED     ("import_finished");
		//@formatter:on

		private String id;

		Step(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

	}

	public class RegisterItemAdapter {

		private RegisterItem value;
		private String error;

		protected RegisterItemAdapter(RegisterItem value, String error) {
			this.value = value;
			this.error = error;
		}

		public RegisterItem getValue() {
			return value;
		}

		public String getError() {
			return error;
		}

	}

}