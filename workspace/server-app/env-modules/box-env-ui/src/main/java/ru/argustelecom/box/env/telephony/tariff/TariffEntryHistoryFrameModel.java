package ru.argustelecom.box.env.telephony.tariff;

import lombok.Getter;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named(value = "tariffEntryHistoryFm")
public class TariffEntryHistoryFrameModel implements Serializable {

	private static final long serialVersionUID = 3890499428068242986L;

	@Inject
	private TariffEntryHistoryDtoTranslator tariffEntryHistoryDtoTr;

	@Getter
	private TariffEntryDto tariffEntry;

	@Getter
	private List<TariffEntryHistoryDto> history;

	@Getter
	private TariffEntryHistoryDto selectedEntryHistory;

	@Getter
	private TariffEntryHistoryDto entryHistoryAfterChange;

	public void preRender(TariffEntryDto tariffEntry) {
		if (!Objects.equals(this.tariffEntry, tariffEntry)) {
			this.tariffEntry = tariffEntry;
			history = tariffEntry.getHistory();
		}
	}

	public void setSelectedEntryHistory(TariffEntryHistoryDto selectedEntryHistory) {
		entryHistoryAfterChange = null;
		this.selectedEntryHistory = selectedEntryHistory;
		for (TariffEntryHistoryDto entryHistory : history) {
			long delta = entryHistory.getVersion() - selectedEntryHistory.getVersion();
			if (delta == 1) {
				entryHistoryAfterChange = entryHistory;
			}
		}

		if (entryHistoryAfterChange == null) {
			entryHistoryAfterChange = TariffEntryHistoryDto.builder()
					.name(tariffEntry.getName())
					.prefixes(tariffEntry.getPrefixes())
					.chargePerUnit(tariffEntry.getChargePerUnit())
					.zone(tariffEntry.getZone())
					.build();
		}
	}

	public boolean isValuesNotEquals(Object before, Object after) {
		return !Objects.equals(before, after);
	}

}
