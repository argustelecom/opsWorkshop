package ru.argustelecom.box.env.pricing.model;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.productdirectory.model.ICustomPricelist;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import static com.google.common.base.Preconditions.checkNotNull;

@Named(value = ICustomPricelist.WRAPPER_NAME)
public class CustomPricelistWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Override
	public ICustomPricelist wrap(Identifiable entity) {
		checkNotNull(entity);
		CustomPricelist customPricelist = (CustomPricelist) entity;
		//@formatter:off
		return ICustomPricelist.builder()
					.id(customPricelist.getId())
					.objectName(customPricelist.getObjectName())
					.state(new IState(customPricelist.getState().toString(), customPricelist.getState().getName()))
					.validFrom(customPricelist.getValidFrom())
					.validTo(customPricelist.getValidTo())
					.taxRate(customPricelist.getOwner().getTaxRate())
					.customerId(customPricelist.getCustomer().getId())
				.build();
		//@formatter:on
	}

	@Override
	public CustomPricelist unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(CustomPricelist.class, iEntity.getId());
	}

	private static final long serialVersionUID = -2164442912004749520L;

}