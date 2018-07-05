package ru.argustelecom.box.env.commodity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "commodityTypeAttrFm")
@PresentationModel
public class CommodityTypeAttributesFrameModel implements Serializable {

	private static final String EMPTY_ICON = "fa fa-question";

	@Inject
	private CommodityTypeAppService commodityTypeAs;

	@Inject
	private CommodityTypeGroupAppService commodityTypeGroupAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private CommodityTypeAttrDtoTranslator attrDtoTr;

	private Callback<BusinessObjectDto<CommodityTypeGroup>> callbackAfterGroupChanged;

	private CommodityTypeTreeNodeDto nodeDto;

	private List<BusinessObjectDto<CommodityTypeGroup>> groups;

	@Getter
	private CommodityTypeAttrDto attrDto;

	public void preRender(CommodityTypeTreeNodeDto nodeDto,
			Callback<BusinessObjectDto<CommodityTypeGroup>> callbackAfterGroupChanged) {
		if (!Objects.equals(this.nodeDto, nodeDto)) {
			this.nodeDto = nodeDto;
			if (nodeDto != null) {
				attrDto = attrDtoTr.translate(nodeDto.getIdentifiable());
			}
		}

		if (!Objects.equals(this.callbackAfterGroupChanged, callbackAfterGroupChanged)) {
			this.callbackAfterGroupChanged = callbackAfterGroupChanged;
		}
	}

	public String getIcon() {
		return attrDto != null ? attrDto.getType().getIconClass() : EMPTY_ICON;
	}

	public List<BusinessObjectDto<CommodityTypeGroup>> getGroups() {
		if (groups == null) {
			groups = businessObjectDtoTr.translate(commodityTypeGroupAs.findAll());
		}
		return groups;
	}

	public void onNameChange() {
		if (!attrDto.isGroup()) {
			commodityTypeAs.changeName(attrDto.getId(), attrDto.getName());
		} else {
			commodityTypeGroupAs.changeName(attrDto.getId(), attrDto.getName());
		}
		nodeDto.setName(attrDto.getName());
	}

	public void onDescriptionChange() {
		commodityTypeAs.changeDescription(attrDto.getId(), attrDto.getDescription());
	}

	public void onGroupChange() {
		if (!attrDto.isGroup()) {
			commodityTypeAs.changeGroup(attrDto.getId(), attrDto.getParent().getId());
		} else {
			Long newParentId = Optional.ofNullable(attrDto.getParent()).map(BusinessObjectDto::getId).orElse(null);
			commodityTypeGroupAs.changeParent(attrDto.getId(), newParentId);
		}
		callbackAfterGroupChanged.execute(attrDto.getParent());
	}

	public void onKeywordChange() {
		commodityTypeGroupAs.changeKeyword(attrDto.getId(), attrDto.getKeyword());
	}

	private static final long serialVersionUID = 4930138757584491766L;

}