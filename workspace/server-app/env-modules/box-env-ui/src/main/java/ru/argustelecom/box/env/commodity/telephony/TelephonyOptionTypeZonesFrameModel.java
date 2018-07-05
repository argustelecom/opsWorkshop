package ru.argustelecom.box.env.commodity.telephony;

import static java.util.stream.Collectors.toList;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.commodity.CommodityTypeTreeNodeDto;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

/**
 * <b>Presentation model</b> для функционального блока {@linkplain TelephonyZone зон телефонной нумерации} для типа
 * опции. В справочнике "Типы товаров, услуг и опций".
 */
@Named(value = "telephonyOptionTypeZonesFm")
@PresentationModel
public class TelephonyOptionTypeZonesFrameModel implements Serializable {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Getter
	private List<BusinessObjectDto<TelephonyZone>> zones;

	private CommodityTypeTreeNodeDto nodeDto;

	public void preRender(CommodityTypeTreeNodeDto nodeDto) {
		if (!Objects.equals(this.nodeDto, nodeDto)) {
			this.nodeDto = nodeDto;
			initZones();
		}
	}

	public Callback<List<BusinessObjectDto<TelephonyZone>>> getCallbackAfterChanged() {
		return actualZones -> zones = actualZones;
	}

	private void initZones() {
		TelephonyOptionType telephonyOptionType = (TelephonyOptionType) initializeAndUnproxy(nodeDto.getIdentifiable());
		zones = telephonyOptionType.getZones().stream().map(businessObjectDtoTr::translate).collect(toList());
	}

	private static final long serialVersionUID = 2489263923701808499L;

}
