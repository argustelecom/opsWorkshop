package ru.argustelecom.box.env.commodity;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.commodity.model.OptionType;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static org.primefaces.context.RequestContext.getCurrentInstance;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

@Named(value = "editServiceTypeToOptionTypeDm")
@PresentationModel
public class EditServiceTypeToOptionTypeDialogModel implements Serializable {

	private static final long serialVersionUID = -6641345198330924412L;

	@Inject
	private OptionTypeAppService optionTypeAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Setter
	private Callback<List<BusinessObjectDto<OptionType>>> callbackAfterChanged;

	@Getter
	private List<BusinessObjectDto<OptionType>> optionTypes;

	@Getter
	@Setter
	private List<BusinessObjectDto<OptionType>> selectedOptionTypes;

	@Setter
	private CommodityTypeTreeNodeDto nodeDto;

	public void openDialog() {
		checkNotNull(nodeDto);

		getCurrentInstance().update("edit_service_type_to_option_type_form-edit_service_type_to_option_type_dlg");
		getCurrentInstance().execute("PF('editServiceTypeToOptionTypeDlgVar').show()");
		initSelectedOptionTypes(nodeDto);
		initOptionTypes();
	}

	public void execute() {
		optionTypeAs.changeOptionTypes(nodeDto.getId(),
				selectedOptionTypes.stream().map(BusinessObjectDto::getId).collect(toList()));
		callbackAfterChanged.execute(selectedOptionTypes);
	}

	private void initSelectedOptionTypes(CommodityTypeTreeNodeDto nodeDto) {
		ServiceType serviceType = (ServiceType) initializeAndUnproxy(nodeDto.getIdentifiable());
		selectedOptionTypes = businessObjectDtoTr.translate(serviceType.getOptionTypes());
	}

	private void initOptionTypes() {
		optionTypes = businessObjectDtoTr.translate(optionTypeAs.findAll());
	}

}