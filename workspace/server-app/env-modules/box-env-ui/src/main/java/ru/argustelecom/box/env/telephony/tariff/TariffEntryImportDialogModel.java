package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.nio.charset.Charset.forName;
import static java.util.Arrays.stream;
import static java.util.Collections.nCopies;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.primefaces.context.RequestContext.getCurrentInstance;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryImportDialogModel.Step.findBy;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;
import static ru.argustelecom.system.inf.chrono.DateUtils.DATETIME_DEFAULT_PATTERN;
import static ru.argustelecom.system.inf.reportengine.OutputType.CSV;
import static ru.argustelecom.system.inf.validation.ValidationIssue.Kind.ERROR;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.ibm.icu.text.CharsetDetector;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.telephony.tariff.TariffEntryImportResult.TariffEntryImportResultMapper;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffEntryMessageBundle;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.reportengine.OutputType;
import ru.argustelecom.system.inf.validation.ValidationIssue;
import ru.argustelecom.system.inf.validation.ValidationIssue.Kind;

@Named("tariffEntryImportDm")
@PresentationModel
public class TariffEntryImportDialogModel implements Serializable {

	private static final Charset DEFAULT_CHARSET = forName("UTF-8");

	@Inject
	private TariffEntryAppService tariffEntryAs;

	@Inject
	private TariffEntryDtoTranslator tariffEntryDtoTr;

	@Getter
	private UploadedFile file;

	@Getter
	private String fileName;

	private List<String> attributes;

	@Setter
	private Callback<List<TariffEntryDto>> importCallback;

	@Getter
	private List<String[]> rawRows;

	private OutputType outputType;

	private List<TariffEntryImportResult> importEntries;

	@Setter
	private TariffDto tariff;

	private OutputType[] supportedFileType = new OutputType[] { CSV };

	private byte[] fileContent;

	private Charset fileCharset;

	@Getter
	private Map<String, List<ValidationIssue<TariffEntryImportResult>>> validationResult;

	@Getter
	private boolean canImport = true;

	@Getter
	private List<String> selectedAttributes = newArrayList();

	private SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_DEFAULT_PATTERN);

	public String handleImportFlow(FlowEvent event) {
		switch (findBy(event.getNewStep())) {
		case UPLOAD:
			reset();
			break;
		case MAP:
			readFileContent();
			break;
		case VALIDATION:
			parseAndValidate();
			break;
		case FINISHED:
			importEntries();
			break;
		default:
			throw new SystemException("Unsupported step");
		}
		return event.getNewStep();
	}

	public void reset() {
		file = null;
		fileName = null;
		fileContent = null;
		fileCharset = null;
	}

	private void readFileContent() {
		outputType = stream(supportedFileType).filter(type -> file.getFileName().endsWith(type.getExtension()))
				.findFirst().orElseThrow(SystemException::new);
		rawRows = tariffEntryAs.readRawRows(outputType, fileContent, fileCharset);
		rawRows.sort(comparing(rawRow -> rawRow.length, reverseOrder()));

		if (selectedAttributes.isEmpty() && !rawRows.isEmpty()) {
			selectedAttributes.addAll(nCopies(rawRows.get(0).length, null));
		}
		canImport = true;
	}

	private void parseAndValidate() {
		TariffEntryMessageBundle messages = getMessages(TariffEntryMessageBundle.class);

		//@formatter:off
		Function<String, Integer> retriever = attribute -> range(0, selectedAttributes.size())
				.filter(index -> attribute.equals(selectedAttributes.get(index)))
				.findFirst()
				.orElseThrow(SystemException::new);

		TariffEntryImportResultMapper mapper = new TariffEntryImportResultMapper(
				retriever.apply(messages.name()),
				retriever.apply(messages.prefixes()),
				retriever.apply(messages.chargePerUnit()),
				retriever.apply(messages.telephonyZoneShort())
		);
		//@formatter:on
		importEntries = tariffEntryAs.parse(outputType, mapper, rawRows);
		validationResult = tariffEntryAs.validate(tariff.getId(), outputType, importEntries).getIssues().stream()
				.collect(groupingBy(ValidationIssue::getMessage));
	}

	public void importEntries() {
		importCallback.execute(
				tariffEntryDtoTr.translate(tariffEntryAs.importEntries(tariff.getId(), outputType, importEntries)));
	}

	public void handleFileUpload(FileUploadEvent event) throws IOException {
		file = event.getFile();
		fileName = new String(file.getFileName().getBytes(), DEFAULT_CHARSET);
		fileContent = toByteArray(file.getInputstream());
		fileCharset = forName(new CharsetDetector().setText(fileContent).detect().getName());
	}

	public void manageAttributeSelection(Integer selectedMenuIndex) {
		//@formatter:off
		range(0, selectedAttributes.size())
				.filter(index -> index != selectedMenuIndex)
				.filter(index -> Objects.equals(selectedAttributes.get(index), selectedAttributes.get(selectedMenuIndex)))
				.findFirst()
				.ifPresent(index -> {
					selectedAttributes.set(index, null);
					getCurrentInstance().update(format("tariff_entry_import_form-map_select_one_menu_%s", index));
				});
		//@formatter:on
	}

	public List<String> getAttributes() {
		if (attributes == null) {
			TariffEntryMessageBundle messages = getMessages(TariffEntryMessageBundle.class);
			attributes = newArrayList(messages.name(), messages.prefixes(), messages.chargePerUnit(),
					messages.telephonyZoneShort());
		}
		return attributes;
	}

	public void onCancel() {
		reset();
	}

	public boolean testKind(String validationMessage, Kind kind) {
		if (validationMessage == null) {
			return false;
		}
		List<ValidationIssue<TariffEntryImportResult>> validationIssues = validationResult.get(validationMessage);
		boolean result = !validationIssues.isEmpty() && kind.equals(validationIssues.get(0).getKind());
		if (canImport && ERROR.equals(kind) && result) {
			canImport = false;
		}
		return result;
	}

	public long countDistinctSelectedAttributes() {
		return selectedAttributes.stream().filter(Objects::nonNull).distinct().count();
	}

	public StreamedContent downloadValidationReport(String validationMessage) {
		//@formatter:off
		List<TariffEntryImportResult> entries = validationResult.get(validationMessage).stream()
				.map(ValidationIssue::getSource)
				.collect(toList());
		//@formatter:on
		String reportName = format("%s %s%s", formatter.format(new Date()), validationMessage,
				outputType.getExtension());
		return new ByteArrayContent(tariffEntryAs.generateValidationReport(outputType, entries),
				outputType.getMimeType(), reportName, DEFAULT_CHARSET.name());
	}

	public void clearSelectedAttributes() {
		selectedAttributes.clear();
	}

	public enum Step {
		//@formatter:off
		UPLOAD  		("upload"),
		MAP     		("map"),
		VALIDATION   	("validation"),
		FINISHED		("finished");
		//@formatter:on

		@Getter
		private String id;

		Step(String id) {
			this.id = id;
		}

		public static Step findBy(String id) {
			return stream(values()).filter(step -> step.getId().equals(id)).findFirst()
					.orElseThrow(SystemException::new);
		}
	}

	private static final long serialVersionUID = 8426489553112468423L;
}
