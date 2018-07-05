package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.collect.Maps.newHashMap;
import static java.nio.charset.Charset.forName;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.system.inf.chrono.DateUtils.DATETIME_DEFAULT_PATTERN;
import static ru.argustelecom.system.inf.reportengine.OutputType.CSV;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named(value = "tariffEntryFm")
public class TariffEntryFrameModel implements Serializable {

	private static final long serialVersionUID = -3480572125342613984L;

	private static final String DELIMITER = ", ";

	public static final Charset DEFAULT_CHARSET = forName("UTF-8");

	@Inject
	private TariffEntryAppService tariffEntryAs;

	@Inject
	private TariffCardViewState tariffCardVs;

	@Inject
	private TariffDtoTranslator tariffDtoTr;

	@Inject
	private TariffAppService tariffAs;

	@Getter
	private List<TariffEntryDto> entries;

	@Getter
	private List<TariffEntryDto> onlyCustomEntries;

	@Getter
	private List<TariffEntryDto> selectedEntries;

	@Getter
	@Setter
	private List<TariffEntryDto> filteredEntries;

	@Getter
	@Setter
	private TariffEntryDto selectedEntry;

	@Getter
	private List<String> crossingTariffs;

	@Getter
	private TariffDto tariff;

	@Getter
	private String prefixes;

	@Getter
	private boolean onlyCustom;

	@Getter
	private Map<String, Object> filters = newHashMap();

	private SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_DEFAULT_PATTERN);

	public void preRender(TariffDto tariff) {
		if (!Objects.equals(this.tariff, tariff)) {
			this.tariff = tariff;
			this.entries = tariff.getEntries();
			crossingTariffs = Collections.emptyList();
			prefixes = StringUtils.EMPTY;

			updateCustomEntries();
		}
	}

	public Callback<TariffEntryDto> getCallback() {
		return tariffEntryDto -> {
			try {
				TariffEntryDto oldEntry = entries.stream().filter(entry -> entry.getId().equals(tariffEntryDto.getId()))
						.findFirst().get();
				entries.set(entries.indexOf(oldEntry), tariffEntryDto);
			} catch (NoSuchElementException ignored) {
				entries.add(tariffEntryDto);
			}

			setSelectedEntries(Collections.singletonList(tariffEntryDto));
			updateCustomEntries();
		};
	}

	public Callback<List<TariffEntryDto>> getImportCallback() {
		return entries -> this.entries.addAll(entries);
	}

	public StreamedContent export() {
		String fileName = String.format("%s %s%s", formatter.format(new Date()), tariff.getName(), CSV.getExtension());
		return new ByteArrayContent(tariffEntryAs.export(CSV, tariff.getId()), CSV.getMimeType(), fileName,
				DEFAULT_CHARSET.name());
	}

	public void remove() {
		selectedEntries.forEach(entry -> {
			tariffEntryAs.remove(tariff.getId(), entry.getId());
			entries.remove(entry);
		});

		updateCustomEntries();
	}

	public void onConfirmDialogOpen() {
		if (selectedEntries != null) {
			if (tariff.getIdentifiable() instanceof CommonTariff) {
				initCrossingTariffs();
			} else {
				initCrossingPrefixes();
			}
		}

		RequestContext.getCurrentInstance()
				.update("tariff_entry_deletion_confirm_form-tariff_entry_deletion_confirm_dlg");
		RequestContext.getCurrentInstance().execute("PF('tariffEntryDeletionConfirmDlgVar').show()");
	}

	public void resetConfirmDialogContext() {
		crossingTariffs = Collections.emptyList();
		prefixes = StringUtils.EMPTY;
	}

	public void setSelectedEntries(List<TariffEntryDto> selectedEntries) {
		this.selectedEntries = selectedEntries;
		if (!selectedEntries.isEmpty()) {
			tariffCardVs.setTariffEntryDto(selectedEntries.iterator().next());
		}
	}

	public void setOnlyCustom(boolean onlyCustom) {
		if (!Objects.equals(this.onlyCustom, onlyCustom)) {
			if (onlyCustom) {
				entries = onlyCustomEntries;
			} else {
				entries = tariff.getEntries();
			}

			this.onlyCustom = onlyCustom;
		}
	}

	private void updateCustomEntries() {
		onlyCustomEntries = tariff.getEntries().stream().filter(entry -> entry.getVersion() > 0).collect(toList());
	}

	private void initCrossingTariffs() {
		List<TariffDto> tariffs = tariffAs.findAllCustomTariffsByCommonTariff(tariff.getId()).stream()
				.map(tariffDtoTr::translate).collect(toList());

		crossingTariffs = new ArrayList<>();

		for (TariffDto tariff : tariffs) {
			if (!crossingTariffs.contains(tariff.getName())) {
				crossingTariffs.add(tariff.getName());
			}
		}
	}

	private void initCrossingPrefixes() {
		Function<List<TariffEntryDto>, List<Integer>> getPrefixes = entries -> entries.stream()
				.flatMap(entry -> entry.getPrefixes().stream()).collect(toList());

		List<Integer> parentPrefixes = getPrefixes.apply(tariff.getParentEntries());
		List<Integer> crossingPrefixes = getPrefixes.apply(selectedEntries);

		crossingPrefixes.retainAll(parentPrefixes);

		prefixes = parseToStringWithDelimiter(crossingPrefixes, DELIMITER);
	}

	static String parseToStringWithDelimiter(Collection<?> collection, String delimiter) {
		StringBuilder stringBuilder = new StringBuilder();

		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {
			stringBuilder.append(iterator.next());

			if (iterator.hasNext()) {
				stringBuilder.append(delimiter);
			}
		}

		return stringBuilder.toString();
	}
}
