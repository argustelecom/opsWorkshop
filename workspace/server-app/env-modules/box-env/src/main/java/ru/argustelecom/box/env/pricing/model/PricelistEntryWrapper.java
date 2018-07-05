package ru.argustelecom.box.env.pricing.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.productdirectory.model.IProductOffering;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Named(value = IProductOffering.WRAPPER_NAME)
public class PricelistEntryWrapper implements EntityWrapper {

	private static final long serialVersionUID = -6017779350342129257L;

	@PersistenceContext
	private transient EntityManager em;

	@Override
	public IProductOffering wrap(Identifiable entity) {
		checkNotNull(entity);
		ProductOffering pricelistProductEntry = (ProductOffering) entity;
		//@formatter:off
		return IProductOffering.builder()
					.id(pricelistProductEntry.getId())
					.objectName(pricelistProductEntry.getObjectName())
					.typeId(pricelistProductEntry.getProductType().getId())
					.pricelistId(pricelistProductEntry.getPricelist().getId())
					.orderNum(pricelistProductEntry.getOrderNum())
					.price(pricelistProductEntry.getPrice().getAmount())
				.build();
		//@formatter:on
	}

	@Override
	public ProductOffering unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(ProductOffering.class, iEntity.getId());
	}
}