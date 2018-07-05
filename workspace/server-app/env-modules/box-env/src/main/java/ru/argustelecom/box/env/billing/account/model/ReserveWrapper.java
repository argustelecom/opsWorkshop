package ru.argustelecom.box.env.billing.account.model;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.billing.model.IReserve;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import static com.google.common.base.Preconditions.checkNotNull;

@Named(value = IReserve.WRAPPER_NAME)
public class ReserveWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Override
	public IReserve wrap(Identifiable entity) {
		checkNotNull(entity);
		Reserve reserve = (Reserve) entity;
		//@formatter:off
		return IReserve.builder()
					.id(reserve.getId())
					.objectName(reserve.getObjectName())
					.personalAccountId(reserve.getPersonalAccount().getId())
					.amount(reserve.getAmount().getAmount())
					.reserveDate(reserve.getReserveDate())
				.build();
		//@formatter:on
	}

	@Override
	public Reserve unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(Reserve.class, iEntity.getId());
	}

	private static final long serialVersionUID = -7625178557729927001L;

}