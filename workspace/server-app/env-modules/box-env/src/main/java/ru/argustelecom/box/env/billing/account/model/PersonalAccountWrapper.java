package ru.argustelecom.box.env.billing.account.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.model.IState;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.billing.model.IPersonalAccount;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Named(value = IPersonalAccount.WRAPPER_NAME)
public class PersonalAccountWrapper implements EntityWrapper {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private PersonalAccountBalanceService personalAccountBs;

	@Override
	public IPersonalAccount wrap(Identifiable entity) {
		checkNotNull(entity);
		PersonalAccount personalAccount = (PersonalAccount) entity;
		//@formatter:off
		return IPersonalAccount.builder()
					.id(personalAccount.getId())
					.objectName(personalAccount.getObjectName())
					.number(personalAccount.getNumber())
					.customerId(personalAccount.getCustomer().getId())
					.state(new IState(personalAccount.getState().toString(), personalAccount.getState().getName()))
					.currency(personalAccount.getCurrency())
					.threshold(personalAccount.getThreshold().getAmount())
					.balance(personalAccountBs.getBalance(personalAccount).getAmount())
					.availableBalance(personalAccountBs.getAvailableBalance(personalAccount).getAmount())
				.build();
		//@formatter:on
	}

	@Override
	public PersonalAccount unwrap(IEntity iEntity) {
		checkNotNull(iEntity);
		return em.find(PersonalAccount.class, iEntity.getId());
	}

	private static final long serialVersionUID = 1383140992661752208L;

}