package ru.argustelecom.box.env.telephony.tariff;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.EditDialogModel;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("telephonyZoneEditDm")
@PresentationModel
public class TelephonyZoneEditDialogModel implements Serializable, EditDialogModel<TelephonyZoneDto> {

	private static final long serialVersionUID = 2747130128905976798L;

	@Inject
	private TelephonyZoneAppService telephonyZoneAs;

	@Inject
	private TelephonyZoneDtoTranslator telephonyZoneDtoTr;

	@Getter
	@Setter
	private TelephonyZoneDto editableObject = new TelephonyZoneDto();

	@Getter
	@Setter
	private Callback<TelephonyZoneDto> callback;

	@Override
	public void submit() {
		TelephonyZoneDto telephoneZone = null;
		if (isEditMode()) {
			telephoneZone = telephonyZoneDtoTr.translate(telephonyZoneAs.edit(editableObject.getId(),
					editableObject.getName(), editableObject.getDescription()));
		} else {
			telephoneZone = telephonyZoneDtoTr
					.translate(telephonyZoneAs.create(editableObject.getName(), editableObject.getDescription()));
		}
		callback.execute(telephoneZone);
		cancel();
	}

	@Override
	public void cancel() {
		editableObject = new TelephonyZoneDto();
	}

	@Override
	public String getHeader() {
		TariffMessagesBundle messages = LocaleUtils.getMessages(TariffMessagesBundle.class);
		if (isEditMode()) {
			return messages.telephonyZoneEditing();
		} else {
			return messages.telephonyZoneCreation();
		}
	}

	@Override
	public boolean isEditMode() {
		return getEditableObject().getId() != null;
	}

}
