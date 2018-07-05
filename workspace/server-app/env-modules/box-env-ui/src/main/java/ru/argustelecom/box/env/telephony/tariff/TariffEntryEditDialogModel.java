package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.collect.Lists.newArrayList;
import static java.nio.charset.Charset.forName;
import static java.util.Arrays.fill;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryEditDialogModel.TariffEntryCreationCase.COMMON_CROSSING_CORRECT;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryEditDialogModel.TariffEntryCreationCase.COMMON_CROSSING_ERROR;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryEditDialogModel.TariffEntryCreationCase.COMMON_CROSSING_ERROR_EXCLUDE_COMMON;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryEditDialogModel.TariffEntryCreationCase.COMMON_CROSSING_WITH_WARNING;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryEditDialogModel.TariffEntryCreationCase.COMMON_WITHOUT_CROSSING_CORRECT;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryEditDialogModel.TariffEntryCreationCase.COMMON_WITHOUT_CROSSING_ERROR;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryEditDialogModel.TariffEntryCreationCase.CUSTOM_CORRECT;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryEditDialogModel.TariffEntryCreationCase.CUSTOM_ERROR;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryExportCsvService.DEFAULT_CSV_SEPARATOR;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryFrameModel.parseToStringWithDelimiter;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryImportResult.PREFIX_PATTERN;
import static ru.argustelecom.box.inf.nls.LocaleUtils.format;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.context.RequestContext;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffEntryMessageBundle;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffMessagesBundle;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.validation.ValidationIssue;
import ru.argustelecom.system.inf.validation.ValidationResult;

@Named(value = "tariffEntryEditDm")
@PresentationModel
public class TariffEntryEditDialogModel implements Serializable {

	private static final String REPORT_MIME_TYPE = "text/csv";
	private static final int REPORT_ROW_NUMBER = 5;
	private static final Charset DEFAULT_CHARSET = forName("UTF-8");
	private static final String DELIMITER = ", ";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TariffEntryAppService tariffEntryAs;

	@Inject
	private TelephoneZoneAppService telephoneZoneAs;

	@Inject
	private TariffEntryExportCsvService tariffExportCsvSvc;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private TariffEntryDtoTranslator tariffEntryDtoTr;

	@Inject
	private TariffCardViewState tariffCardVs;

	@Inject
	private TariffDtoTranslator tariffDtoTr;

	@Inject
	private TariffAppService tariffAs;

	@Getter
	@Setter
	private TariffEntryEditDto editDto;

	@Setter
	private Long tariffId;

	@Setter
	private Callback<TariffEntryDto> callback;

	@Getter
	private List<TariffDto> crossingTariffs;

	@Getter
	private List<TariffDto> correctTariffs;

	@Getter
	private List<TariffDto> incorrectTariffs;

	@Getter
	private ValidationResult<TariffEntryQueryResult> validationResult;

	@Getter
	private List<TariffEntryQueryResult> validationResultToExport;

	@Getter
	private TariffEntryCreationCase creationCase;

	private List<BusinessObjectDto<TelephonyZone>> telephoneZones;

	private TariffEntryDto editableTariffEntry;

	public void onDialogOpen() {
		RequestContext.getCurrentInstance().update("tariff_entry_edit_form-tariff_entry_edit_dlg");
		RequestContext.getCurrentInstance().execute("PF('tariffEntryEditDlgVar').show()");
		if (!isEditableMode())
			editDto = new TariffEntryEditDto();
	}

	public boolean isEditableMode() {
		return editDto != null && editDto.getId() != null;
	}

	public void submit() {
		Function<List<Integer>, Boolean> hasDuplicatesPrefixes = prefixes -> prefixes.stream().distinct()
				.collect(toList()).size() < prefixes.size();

		if (hasDuplicatesPrefixes.apply(editDto.getParsedPrefixes())) {
			showDuplicatePrefixesError();
		} else {
			initValidationData();

			if (isEditableMode()) {
				editWithValidation();
			} else {
				createWithValidation();
			}
		}
	}

	public void submitAfterResolveCollisions() {
		if (!isEditableMode()) {
			callback.execute(tariffEntryDtoTr.translate(create()));
		} else {
			change();
			callback.execute(tariffEntryDtoTr.translate(em.find(TariffEntry.class, editableTariffEntry.getId())));
		}
		reset();
	}

	public void reset() {
		editDto = null;
		callback = null;
		editableTariffEntry = null;
		validationResult = null;
		validationResultToExport = null;
		correctTariffs = null;
		incorrectTariffs = null;
		creationCase = null;
	}

	public List<BusinessObjectDto<TelephonyZone>> getTelephoneZones() {
		if (telephoneZones == null) {
			telephoneZones = businessObjectDtoTr.translate(telephoneZoneAs.findAll());
		}

		return telephoneZones;
	}

	public void setEditableTariffEntry(TariffEntryDto editableTariffEntry) {
		if (!Objects.equals(this.editableTariffEntry, editableTariffEntry)) {
			this.editableTariffEntry = editableTariffEntry;
			//@formatter:off
			editDto =
					TariffEntryEditDto.builder()
							.id(editableTariffEntry.getId())
							.name(editableTariffEntry.getName())
							.prefix(editableTariffEntry.getPrefixAsString())
							.cost(editableTariffEntry.getChargePerUnit())
							.zone(editableTariffEntry.getZone())
						.build();
			//@formatter:on
		}
	}

	public StreamedContent download() throws IOException {
		return exportValidationResult(validationResultToExport);
	}

	public StreamedContent downloadAll(Map<String, List<TariffEntryQueryResult>> validation) throws IOException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ZipOutputStream zipStream = new ZipOutputStream(outputStream)) {

			for (String errorMsg : validation.keySet()) {
				ZipEntry zipEntry = new ZipEntry(format("{0}_{1}{2}", new Date(), errorMsg, ".csv"));
				zipStream.putNextEntry(zipEntry);

				//@formatter:off
				zipStream.write(tariffExportCsvSvc.generate(
						generateRows(validation.get(errorMsg)),
						DEFAULT_CSV_SEPARATOR,
						DEFAULT_CHARSET));
				zipStream.closeEntry();
				//@formatter:on
			}

			zipStream.finish();
			return new ByteArrayContent(outputStream.toByteArray(), "zip",
					format("{0}_{1}{2}", new Date(), validation.keySet(), ".zip"));
		}
	}

	public void validatePrefixes(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value != null && !PREFIX_PATTERN.matcher(value.toString()).matches()) {
			throw new ValidatorException(
					new FacesMessage(SEVERITY_ERROR, getMessages(OverallMessagesBundle.class).error(),
							getMessages(TariffEntryMessageBundle.class).prefixesLengthInvalid()));
		}
	}

	private void editWithValidation() {
		if (incorrectTariffs.isEmpty()) {
			RequestContext.getCurrentInstance()
					.execute("PF('tariffEntryTableVar').filter(); PF('tariffEntryEditDlgVar').hide()");

			change();
			callback.execute(tariffEntryDtoTr.translate(em.find(TariffEntry.class, editableTariffEntry.getId())));
			reset();
		} else {
			showCrossingPrefixesError();
		}
	}

	private void createWithValidation() {
		initDialogContext();

		if (creationCase.equals(COMMON_WITHOUT_CROSSING_CORRECT)) {
			RequestContext.getCurrentInstance().execute("PF('tariffEntryEditDlgVar').hide()");

			callback.execute(tariffEntryDtoTr.translate(create()));
			reset();
		} else if (creationCase.equals(COMMON_WITHOUT_CROSSING_ERROR) || creationCase.equals(CUSTOM_ERROR)) {
			showCrossingPrefixesError();
		} else if (creationCase.equals(COMMON_CROSSING_CORRECT) || creationCase.equals(COMMON_CROSSING_WITH_WARNING)
				|| creationCase.equals(COMMON_CROSSING_ERROR) || creationCase.equals(CUSTOM_CORRECT)
				|| creationCase.equals(COMMON_CROSSING_ERROR_EXCLUDE_COMMON)) {
			RequestContext.getCurrentInstance().execute("PF('tariffEntryEditDlgVar').hide()");
			RequestContext.getCurrentInstance()
					.update("tariff_entry_creation_confirm_form-tariff_entry_creation_confirm_dlg");
			RequestContext.getCurrentInstance().execute("PF('tariffEntryCreationConfirmDlgVar').show()");
		}
	}

	private StreamedContent exportValidationResult(List<TariffEntryQueryResult> entries) {
		//@formatter:off
		return new ByteArrayContent(
					tariffExportCsvSvc.generate(generateRows(entries), DEFAULT_CSV_SEPARATOR, DEFAULT_CHARSET),
					REPORT_MIME_TYPE,
					format("{0}_{1}.{2}", new Date(), "tariff_add_error", "csv"),
					DEFAULT_CHARSET.name()
				);
		//@formatter:on
	}

	private void initDialogContext() {
		if (tariffCardVs.getTariffDto().getIdentifiable() instanceof CommonTariff) {
			List<Integer> tariffPrefixes = tariffCardVs.getTariffDto().getEntries().stream()
					.flatMap(entry -> entry.getPrefixes().stream()).collect(toList());

			tariffPrefixes.retainAll(editDto.getParsedPrefixes());

			if (!crossingTariffs.isEmpty() && incorrectTariffs.isEmpty()) {
				creationCase = COMMON_CROSSING_CORRECT;
			} else if (!crossingTariffs.isEmpty() && !correctTariffs.isEmpty() && !incorrectTariffs.isEmpty()) {
				creationCase = tariffPrefixes.isEmpty() ? COMMON_CROSSING_WITH_WARNING : COMMON_CROSSING_ERROR;
			} else if (!crossingTariffs.isEmpty() && correctTariffs.isEmpty() && !incorrectTariffs.isEmpty()) {
				creationCase = tariffPrefixes.isEmpty() ? COMMON_CROSSING_ERROR_EXCLUDE_COMMON : COMMON_CROSSING_ERROR;
			} else if (crossingTariffs.isEmpty() && incorrectTariffs.isEmpty()) {
				creationCase = COMMON_WITHOUT_CROSSING_CORRECT;
			} else if (crossingTariffs.isEmpty() && !incorrectTariffs.isEmpty()) {
				creationCase = COMMON_WITHOUT_CROSSING_ERROR;
			}
		} else {
			if (!incorrectTariffs.isEmpty()) {
				creationCase = CUSTOM_ERROR;
			} else {
				creationCase = CUSTOM_CORRECT;
			}
		}
	}

	private void initValidationData() {
		crossingTariffs = tariffCardVs.getTariffDto().getIdentifiable() instanceof CommonTariff
				? tariffAs.findAllCustomTariffsByCommonTariff(tariffCardVs.getTariffDto().getId()).stream()
						.map(tariffDtoTr::translate).collect(Collectors.toList())
				: Collections.emptyList();

		validationResult = isEditableMode()
				? tariffEntryAs.validate(
						tariffEntryAs.findByPrefixesExclude(tariffId, editDto.getId(), editDto.getParsedPrefixes()),
						editDto.getZone().getId(), editDto.getName(), editDto.getCost())
				: tariffEntryAs.validate(tariffEntryAs.findByPrefixes(tariffId, editDto.getParsedPrefixes()),
						editDto.getZone().getId(), editDto.getName(), editDto.getCost());

		validationResultToExport = validationResult.getIssues().stream().map(ValidationIssue::getSource)
				.collect(toList());

		incorrectTariffs = findIncorrect();
		correctTariffs = findCorrects();
	}

	private void showCrossingPrefixesError() {
		OverallMessagesBundle overallMessages = getMessages(OverallMessagesBundle.class);
		TariffMessagesBundle tariffMessages = getMessages(TariffMessagesBundle.class);

		Set<ValidationIssue<TariffEntryQueryResult>> allErrors = Stream
				.concat(validationResult.getErrors().stream(), validationResult.getWarnings().stream())
				.collect(Collectors.toSet());

		Set<String> entryNames = new HashSet<>(
				allErrors.stream().map(issue -> issue.getSource().getName()).collect(Collectors.toSet()));

		Notification.error(overallMessages.error(),
				isEditableMode() ? tariffMessages.updateCrossingError(parseToStringWithDelimiter(entryNames, DELIMITER))
						: tariffMessages.createCrossingError(parseToStringWithDelimiter(entryNames, DELIMITER)));
	}

	private void showDuplicatePrefixesError() {
		TariffMessagesBundle tariffMessages = getMessages(TariffMessagesBundle.class);
		OverallMessagesBundle overallMessages = getMessages(OverallMessagesBundle.class);

		Notification.error(overallMessages.error(), tariffMessages.inputRepeatPrefixesError());
	}

	private List<String[]> generateRows(List<TariffEntryQueryResult> entries) {
		TariffEntryMessageBundle messages = getMessages(TariffEntryMessageBundle.class);

		Function<String, String[]> createHeaderRow = header -> {
			String[] row = new String[REPORT_ROW_NUMBER];
			fill(row, EMPTY);
			row[0] = header;
			return row;
		};

		List<String[]> rows = newArrayList();
		rows.add(createHeaderRow.apply(messages.createReportHeader()));
		//@formatter:off
		rows.add(new String[] {
				EMPTY,
				editDto.getName(),
				editDto.getPrefix(),
				editDto.getCost().toString(),
				editDto.getZone().getObjectName()
		});

		//@formatter:on
		rows.add(createHeaderRow.apply(messages.tariffEntry()));
		//@formatter:off
		entries.stream()
				.map(entry -> new String[] {
						entry.getTariffName(),
						entry.getName(),
						entry.getPrefixesAsString(),
						entry.getChargePerUnit().toString(),
						entry.getZoneName()
				})
				.collect(toCollection(() -> rows));
		//@formatter:on

		return rows;
	}

	private List<TariffDto> findIncorrect() {

		Set<ValidationIssue<TariffEntryQueryResult>> allErrors = Stream
				.concat(validationResult.getErrors().stream(), validationResult.getWarnings().stream())
				.collect(Collectors.toSet());

		Set<Long> incorrectIds = allErrors.stream().map(issue -> issue.getSource().getTariffId()).collect(toSet());

		return incorrectIds.stream().map(tariffId -> tariffDtoTr.translate(tariffAs.findById(tariffId)))
				.collect(Collectors.toList());
	}

	private List<TariffDto> findCorrects() {
		List<Long> incorrectIds = incorrectTariffs.stream().map(TariffDto::getId).collect(Collectors.toList());
		return crossingTariffs.stream().filter(tariffDto -> !incorrectIds.contains(tariffDto.getId()))
				.collect(Collectors.toList());
	}

	private TariffEntry create() {
		//@formatter:off
		return tariffEntryAs.create(
				tariffId,
				editDto.getZone().getId(),
				editDto.getName(),
				editDto.getParsedPrefixes(),
				editDto.getCost(),
				incorrectTariffs.stream().map(TariffDto::getId).collect(Collectors.toList()
				));
		//@formatter:on
	}

	private void change() {
		//@formatter:off
		tariffEntryAs.update(
				tariffId,
				editDto.getId(),
				editDto.getZone().getId(),
				editDto.getName(),
				editDto.getParsedPrefixes(),
				editDto.getCost()
		);
		//@formatter:on
	}

	/**
	 * Возможные сценарии при создании класса трафика, нужны для правильного отображения контекста в диалогах
	 * подтверждения.
	 */
	public enum TariffEntryCreationCase {
		/**
		 * Ошибка валидации при добавлении класса трафика в индивидуальный тарифный план. Выводим Notification с ошибкой
		 */
		CUSTOM_ERROR,

		/**
		 * Добавляемый в индивидуальный тарифный план класс трафика успешно провалидирован. Показываем диалог
		 * подтверждения
		 */
		CUSTOM_CORRECT,

		/**
		 * Обнаружены индивидуальные тарифные планы, созданные на основе публичного, класс трафика успешно
		 * провалидирован Показываем диалог подтверждения, отображаем все тарифные планы в который будет добавлен класс
		 * трафика
		 */
		COMMON_CROSSING_CORRECT,

		/**
		 * Обнаружены индивидуальные тарифные планы, созданные на основе публичного, некоторые из которых уже содержат
		 * добавляемый класс трафика. Показываем диалог подтверждения, отображаем тарифные планы, добавление в которые
		 * невозможно, а также тарифные планы в которые будет добавлен класс трафика
		 */
		COMMON_CROSSING_WITH_WARNING,

		/**
		 * Обнаружены индивидуальные тарифные планы, созданные на основе публичного, все без исключений содержат
		 * добавляемый класс трафика (включая текущий ТП). Добавление невозможно, показываем диалог с ошибкой и
		 * возможностью выгрузки информации валидации
		 */
		COMMON_CROSSING_ERROR,

		/**
		 * Добавляемый в публичный тарифный план, на основании которого не было создано ни одного индивидуального
		 * тарифа, класс трафика успешно провалидирован. Добавляем класс трафика без каких-либо диалогов
		 */
		COMMON_WITHOUT_CROSSING_CORRECT,

		/**
		 * Ошибка валидации при добавлении класса трафика в публичный тарифный план, на основе которого не было создано
		 * ни одного индивидуального тарифа. Выводим Notification с ошибкой
		 */
		COMMON_WITHOUT_CROSSING_ERROR,

		/**
		 * Обнаружены индивидуальные тарифные планы, созданные на основе публичного, все без исключений содержат
		 * добавляемый класс трафика (кроме текущего ТП). Добавляем класс трафика только в текущий тарифный план
		 */
		COMMON_CROSSING_ERROR_EXCLUDE_COMMON

	}

	private static final long serialVersionUID = -871774985627115579L;
}
