package ru.argustelecom.box.env.telephony.tariff;

import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "telephonyZoneDirectoryVm")
@PresentationModel
public class TelephonyZoneDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = -5093828032434151626L;

	@Inject
	private TelephonyZoneAppService telephonyZoneAs;

	@Inject
	private TelephonyZoneDtoTranslator telephonyZoneDtoTr;

	@Getter
	private List<TelephonyZoneDto> telephonyZones;

	@Getter
	@Setter
	private List<TelephonyZoneDto> selectedTelephonyZones;

	@PostConstruct
	protected void postConstruct() {
		unitOfWork.makePermaLong();
		refreshTelephonyZones();
	}

	public void remove() {
		selectedTelephonyZones.forEach(telephonyZone -> telephonyZoneAs.remove(telephonyZone.getId()));
		telephonyZones.removeAll(selectedTelephonyZones);
	}

	public void onEditDialogOpen() {
		RequestContext.getCurrentInstance().update("telephony_zone_creation_form");
		RequestContext.getCurrentInstance().execute("PF('telephonyZoneEditDlg').show();");
	}

	public Callback<TelephonyZoneDto> getCallback() {
		return telephonyZone -> {
			if (telephonyZones.contains(telephonyZone)) {
				telephonyZones.set(telephonyZones.indexOf(telephonyZone), telephonyZone);
			} else {
				telephonyZones.add(telephonyZone);
			}
			sortTelephonyZones();
		};
	}

	private void refreshTelephonyZones() {
		telephonyZones = telephonyZoneDtoTr.translate(telephonyZoneAs.findAll());
		sortTelephonyZones();
	}

	private void sortTelephonyZones() {
		telephonyZones.sort(Comparator.comparing(TelephonyZoneDto::getName));
	}
}
