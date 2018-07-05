package ru.argustelecom.box.env.commodity;

import static java.lang.String.format;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.commodity.model.GoodsType;
import ru.argustelecom.box.env.commodity.model.OptionType;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.commodity.nls.CommodityMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@AllArgsConstructor(access = AccessLevel.MODULE)
public enum CommodityTypeRef {

	// @formatter:off
	GROUP			(CommodityTypeGroup.class, "fa fa-folder-open"),
	SERVICE_TYPE	(ServiceType.class, "fa fa-suitcase"),
	GOODS_TYPE		(GoodsType.class, "icon-box"),
	OPTION_TYPE		(OptionType.class, "fa fa-chain (alias)");
	// @formatter:on

	private Class<? extends Identifiable> clazz;
	private String iconClass;

	public static CommodityTypeRef determineType(Identifiable identifiable) {
		Identifiable unproxyType = EntityManagerUtils.initializeAndUnproxy(identifiable);
		if (unproxyType instanceof CommodityTypeGroup) {
			return GROUP;
		} else if (unproxyType instanceof ServiceType) {
			return SERVICE_TYPE;
		} else if (unproxyType instanceof GoodsType) {
			return GOODS_TYPE;
		} else if (unproxyType instanceof OptionType) {
			return OPTION_TYPE;
		} else {
			throw new SystemException(
					format("Unsupported commodity type ref: '%s'", identifiable.getClass().getSimpleName()));
		}
	}

	public String getName() {
		CommodityMessagesBundle messages = LocaleUtils.getMessages(CommodityMessagesBundle.class);

		switch (this) {
		case GROUP:
			return messages.group();
		case SERVICE_TYPE:
			return messages.serviceType();
		case GOODS_TYPE:
			return messages.goodsType();
		case OPTION_TYPE:
			return messages.optionType();
		default:
			throw new SystemException("Unsupported CommodityTypeRef");
		}
	}

	public String getCreationDlgHeader() {
		CommodityMessagesBundle messages = LocaleUtils.getMessages(CommodityMessagesBundle.class);

		switch (this) {
		case GROUP:
			return messages.groupCreation();
		case SERVICE_TYPE:
			return messages.serviceTypeCreation();
		case GOODS_TYPE:
			return messages.goodsTypeCreation();
		case OPTION_TYPE:
			return messages.optionTypeCreation();
		default:
			throw new SystemException("Unsupported CommodityTypeRef");
		}
	}

}