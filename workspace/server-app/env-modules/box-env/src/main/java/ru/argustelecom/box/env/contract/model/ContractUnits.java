package ru.argustelecom.box.env.contract.model;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.commodity.model.CommoditySpec;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.box.env.product.model.ProductType;
import ru.argustelecom.box.env.product.model.ProductTypeComposite;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@Stateless
public class ContractUnits implements Serializable {

	private static final long serialVersionUID = -6929725731529604200L;

	public Map<CommodityTypeGroup, List<CommoditySpec<?>>> getCommoditiesMap(
			List<ProductOfferingContractEntry> entries) {
		Map<CommodityTypeGroup, List<CommoditySpec<?>>> commoditiesMap = new HashMap<>();
		entries.forEach(EntityManagerUtils::initializeAndUnproxy);
		for (ProductOfferingContractEntry entry : entries) {
			checkState(initializeAndUnproxy(entry.getProductOffering()) instanceof ProductOffering);
			AbstractProductType productType = initializeAndUnproxy(entry.getProductOffering()).getProductType();
			if (productType instanceof ProductType) {
				((ProductType) productType).getEntries()
						.forEach(productSpecEntry -> putValue(commoditiesMap, productSpecEntry));
			} else if (productType instanceof ProductTypeComposite) {
				((ProductTypeComposite) productType).getAllEntries()
						.forEach(productSpecEntry -> putValue(commoditiesMap, productSpecEntry));
			}
		}
		return commoditiesMap;
	}

	private void putValue(Map<CommodityTypeGroup, List<CommoditySpec<?>>> commoditiesMap,
			CommoditySpec<?> commoditySpec) {
		CommodityTypeGroup category = commoditySpec.getType().getGroup();
		if (commoditiesMap.containsKey(commoditySpec.getType().getGroup())) {
			commoditiesMap.get(category).add(commoditySpec);
		} else {
			commoditiesMap.put(category, Lists.newArrayList(commoditySpec));
		}
	}

}