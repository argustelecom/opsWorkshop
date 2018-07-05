package ru.argustelecom.box.env.commodity;

import static java.util.stream.Collectors.toList;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.commodity.model.OptionType;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

/**
 * <b>Presentation model</b> для функционального блока {@linkplain ru.argustelecom.box.env.commodity.model.ServiceType
 * связанных типов услуг} для типа опций.
 */
@Named(value = "optionTypeServiceTypeFm")
@PresentationModel
public class OptionTypeServiceTypeFrameModel implements Serializable {

	private static final long serialVersionUID = 159241425977835315L;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Getter
	private List<BusinessObjectDto<ServiceType>> serviceTypes;

	private CommodityTypeTreeNodeDto nodeDto;

	public void preRender(CommodityTypeTreeNodeDto nodeDto) {
		this.nodeDto = nodeDto;
		initServiceTypes();
	}

	public Callback<List<BusinessObjectDto<ServiceType>>> getCallbackAfterChanged() {
		return newServiceTypes -> serviceTypes = newServiceTypes;
	}

	private void initServiceTypes() {
		OptionType optionType = (OptionType) initializeAndUnproxy(nodeDto.getIdentifiable());
		serviceTypes = optionType.getServiceTypes().stream().map(businessObjectDtoTr::translate).collect(toList());
	}

}
