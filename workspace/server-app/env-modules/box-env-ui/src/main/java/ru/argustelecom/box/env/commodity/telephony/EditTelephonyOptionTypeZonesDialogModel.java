package ru.argustelecom.box.env.commodity.telephony;

import static java.util.stream.Collectors.toList;
import static org.primefaces.context.RequestContext.getCurrentInstance;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.CommodityTypeTreeNodeDto;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.TelephonyZoneAppService;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

/**
 * Модель для диалого добавления/исключения {@linkplain TelephonyZone зон телефонной нумерации} для
 * {@linkplain TelephonyOptionType телефонного типа опции}.
 */
@Named(value = "editTelephonyOptionTypeZonesDm")
@PresentationModel
public class EditTelephonyOptionTypeZonesDialogModel implements Serializable {

	@Inject
	private TelephonyOptionTypeAppService telephonyOptionTypeAs;

	@Inject
	private TelephonyZoneAppService telephonyZoneAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Setter
	private Callback<List<BusinessObjectDto<TelephonyZone>>> callbackAfterChanged;

	@Getter
	private List<BusinessObjectDto<TelephonyZone>> zones;

	@Getter
	@Setter
	private List<BusinessObjectDto<TelephonyZone>> selectedZones;

	@Setter
	private CommodityTypeTreeNodeDto nodeDto;

	public void openDialog() {
		getCurrentInstance().update("edit_telephony_option_type_zones_form-edit_telephony_option_type_zones_dlg");
		getCurrentInstance().execute("PF('editTelephonyOptionTypeZonesDlgVar').show()");

		if (nodeDto != null) {
			initSelectedZones(nodeDto);
			initZones();
		}
	}

	public void execute() {
		telephonyOptionTypeAs.markZones(nodeDto.getId(),
				selectedZones.stream().map(BusinessObjectDto::getId).collect(toList()));
		callbackAfterChanged.execute(selectedZones);
	}

	private void initSelectedZones(CommodityTypeTreeNodeDto nodeDto) {
		TelephonyOptionType optionType = (TelephonyOptionType) initializeAndUnproxy(nodeDto.getIdentifiable());
		selectedZones = businessObjectDtoTr.translate(optionType.getZones());
	}

	private void initZones() {
		if (zones == null) {
			zones = businessObjectDtoTr.translate(telephonyZoneAs.findAll());
		}
	}

	private static final long serialVersionUID = -8987890108567590538L;

}