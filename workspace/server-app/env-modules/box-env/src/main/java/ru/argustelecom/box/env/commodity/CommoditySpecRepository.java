package ru.argustelecom.box.env.commodity;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.model.CommoditySpec;
import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.GoodsSpec;
import ru.argustelecom.box.env.commodity.model.GoodsType;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.exception.SystemException;

@Repository
public class CommoditySpecRepository implements Serializable {

	private static final long serialVersionUID = -6385559967976624999L;

	@Inject
	private TypeFactory typeFactory;

	public CommoditySpec<?> createCommoditySpec(CommodityType commodityType) {
		if (commodityType instanceof ServiceType) {
			return createServiceSpec((ServiceType) commodityType);
		} else if (commodityType instanceof GoodsType) {
			return createGoodsSpec((GoodsType) commodityType);
		} else {
			throw new SystemException(
					String.format("Can not create commodity spec. Unsupported commodity type: '%s'", commodityType));
		}
	}

	public ServiceSpec createServiceSpec(ServiceType serviceType) {
		checkNotNull(serviceType);
		return typeFactory.createInstance(serviceType, ServiceSpec.class);
	}

	public GoodsSpec createGoodsSpec(GoodsType goodsType) {
		checkNotNull(goodsType);
		return typeFactory.createInstance(goodsType, GoodsSpec.class);
	}

}