package ru.argustelecom.box.env.commodity;

import static java.lang.String.format;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionTypeAppService;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.type.CurrentType;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "commodityTypeCreationDm")
@PresentationModel
public class CommodityTypeCreationDlgModel implements Serializable {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private CommodityTypeTreeNodeDtoTranslator commodityTypeTreeNodeDtoTr;

	@Inject
	private CurrentType currentType;

	@Inject
	private CommodityTypeRepository commodityTypeRepository;

	@Inject
	private TelephonyOptionTypeAppService telephonyOptionTypeAs;

	@Getter
	@Setter
	private Long groupId;

	@Setter
	private Callback<CommodityTypeTreeNodeDto> callback;

	@Getter
	private CommodityTypeOrGroupCreationDto creationDto = new CommodityTypeOrGroupCreationDto();

	@Getter
	private List<BusinessObjectDto<CommodityTypeGroup>> groups;

	public void open() {
		initGroups();
		initGroup();

		RequestContext.getCurrentInstance().execute("PF('categoriesPanel').hide()");
		RequestContext.getCurrentInstance().update("commodity_type_creation_form-commodity_type_creation_dlg");
		RequestContext.getCurrentInstance().execute("PF('commodityTypeCreationDlgVar').show()");
	}

	public void create() {
		CommodityTypeTreeNodeDto nodeDto;

		//@formatter:off
		switch (creationDto.getCategory()) {
			case SERVICE_TYPE:
			nodeDto = commodityTypeTreeNodeDtoTr.translate(
					commodityTypeRepository.createServiceType(
						creationDto.getName(),
						creationDto.getKeyword(),
						creationDto.getGroup().getIdentifiable(),
						creationDto.getDescription()
					)
			);
			break;
			case GOODS_TYPE:
			nodeDto = commodityTypeTreeNodeDtoTr.translate(
					commodityTypeRepository.createGoodsType(
							creationDto.getName(),
							creationDto.getKeyword(),
							creationDto.getGroup().getIdentifiable(),
							creationDto.getDescription()
					)
			);
			break;
			case OPTION_TYPE:
			nodeDto = commodityTypeTreeNodeDtoTr.translate(
					telephonyOptionTypeAs.create(
							creationDto.getName(),
							creationDto.getKeyword(),
							creationDto.getGroup().getIdentifiable(),
							creationDto.getDescription()
					)
			);
			break;
			case GROUP:
				nodeDto = commodityTypeTreeNodeDtoTr.translate(
						commodityTypeRepository.createGroup(
							creationDto.getName(),
							creationDto.getKeyword(),
							Optional.ofNullable(creationDto.getGroup()).map(BusinessObjectDto::getIdentifiable).orElse(null)
						)
				);
			break;
		default:
			throw new SystemException(format("Can not create commodity type or group. Unsupported category: '%s'", creationDto.getCategory()));
		}
		//@formatter:on
		callback.execute(nodeDto);
		clear();
	}

	public boolean isGroupCreation() {
		return CommodityTypeRef.GROUP.equals(creationDto.getCategory());
	}

	public void clear() {
		creationDto = new CommodityTypeOrGroupCreationDto();
		callback = null;
		groupId = null;
		groups = null;
	}

	private void initGroups() {
		if (groups == null) {
			groups = businessObjectDtoTr.translate(commodityTypeRepository.findGroups());
		}
	}

	private void initGroup() {
		if (groupId != null) {
			creationDto.setGroup(getGroups().stream().filter(g -> g.getId().equals(groupId)).findFirst().orElse(null));
		}
	}

	private static final long serialVersionUID = 1L;

}