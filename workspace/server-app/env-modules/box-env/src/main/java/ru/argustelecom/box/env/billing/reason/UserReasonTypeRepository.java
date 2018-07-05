package ru.argustelecom.box.env.billing.reason;

import static ru.argustelecom.box.env.billing.reason.model.UserReasonType.UserReasonTypeQuery;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.reason.model.UserReasonType;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class UserReasonTypeRepository implements Serializable {

	private static final long serialVersionUID = 5981963979108674381L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public UserReasonType createUserReasonType(String name) {
		UserReasonType type = new UserReasonType(idSequence.nextValue(UserReasonType.class), name);
		em.persist(type);
		return type;
	}

	public List<UserReasonType> getAllUserReasonTypes() {
		return new UserReasonTypeQuery().createTypedQuery(em).getResultList();
	}
}
