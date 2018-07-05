package ru.argustelecom.box.env.pricing.model;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.productdirectory.model.ICommonPricelist;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import static com.google.common.base.Preconditions.checkNotNull;

@Named(value = ICommonPricelist.WRAPPER_NAME)
public class CommonPricelistWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Override
	public ICommonPricelist wrap(Identifiable entity) {
		checkNotNull(entity);
		CommonPricelist commonPricelist = (CommonPricelist) entity;
		//@formatter:off
		return ICommonPricelist.builder()
					.id(commonPricelist.getId())
					.objectName(commonPricelist.getObjectName())
					.state(new IState(commonPricelist.getState().toString(), commonPricelist.getState().getName()))
					.validFrom(commonPricelist.getValidFrom())
					.validTo(commonPricelist.getValidTo())
					.taxRate(commonPricelist.getOwner().getTaxRate())
				.build();
		//@formatter:on
	}

	@Override
	public CommonPricelist unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(CommonPricelist.class, iEntity.getId());
	}

	private static final long serialVersionUID = -1975959270850380485L;

}