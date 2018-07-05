package ru.argustelecom.box.env.commodity;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static org.primefaces.context.RequestContext.getCurrentInstance;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.OptionType;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "editOptionTypeToServiceTypeDm")
@PresentationModel
public class EditOptionTypeToServiceTypeDialogModel implements Serializable {

	private static final long serialVersionUID = -6641345198330924412L;

	@Inject
	private CommodityTypeAppService commodityTypeAs;

	@Inject
	private OptionTypeAppService optionTypeAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Setter
	private Callback<List<BusinessObjectDto<ServiceType>>> callbackAfterChanged;

	@Getter
	private List<BusinessObjectDto<ServiceType>> serviceTypes;

	@Getter
	@Setter
	private List<BusinessObjectDto<ServiceType>> selectedServiceTypes;

	@Setter
	private CommodityTypeTreeNodeDto nodeDto;

	public void openDialog() {
		checkNotNull(nodeDto);

		getCurrentInstance().update("edit_option_type_to_service_type_form-edit_option_type_to_service_type_dlg");
		getCurrentInstance().execute("PF('editOptionTypeToServiceTypeDlgVar').show()");
		initSelectedServiceTypes(nodeDto);
		initServiceTypes();
	}

	public void execute() {
		optionTypeAs.changeServiceTypes(nodeDto.getId(),
				selectedServiceTypes.stream().map(BusinessObjectDto::getId).collect(toList()));
		callbackAfterChanged.execute(selectedServiceTypes);
	}

	private void initSelectedServiceTypes(CommodityTypeTreeNodeDto nodeDto) {
		OptionType optionType = (OptionType) initializeAndUnproxy(nodeDto.getIdentifiable());
		selectedServiceTypes = businessObjectDtoTr.translate(optionType.getServiceTypes());
	}

	private void initServiceTypes() {
		if (serviceTypes == null) {
			serviceTypes = businessObjectDtoTr.translate(commodityTypeAs.findAllServiceTypes());
		}
	}

}