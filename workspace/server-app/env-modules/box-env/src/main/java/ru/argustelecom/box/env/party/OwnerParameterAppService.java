package ru.argustelecom.box.env.party;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.model.role.OwnerParameter;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class OwnerParameterAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private OwnerParameterRepository ownerParameterRp;

	public OwnerParameter create(Long ownerId, String keyword, String name, String value) {
		checkNotNull(ownerId);

		Owner owner = em.find(Owner.class, ownerId);
		return ownerParameterRp.create(owner, keyword, name, value);
	}

	public void change(Long parameterId, String keyword,  String name, String value) {
		checkNotNull(parameterId);
		checkNotNull(keyword);
		checkNotNull(name);
		checkNotNull(value);

		OwnerParameter parameter = em.find(OwnerParameter.class, parameterId);
		parameter.setKeyword(keyword);
		parameter.setObjectName(name);
		parameter.setValue(value);
	}

	public void remove(Long ownerId, Long parameterId) {
		checkNotNull(ownerId);
		checkNotNull(parameterId);

		Owner owner = em.find(Owner.class, ownerId);
		OwnerParameter parameter = em.find(OwnerParameter.class, parameterId);

		owner.removeAdditionalParameter(parameter);
	}

	private static final long serialVersionUID = 3434127632929618695L;
}
