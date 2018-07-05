package ru.argustelecom.box.env.billing.reason.testdata;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.base.Preconditions;

import ru.argustelecom.box.env.billing.reason.UserReasonTypeRepository;
import ru.argustelecom.box.env.billing.reason.model.UserReasonType;
import ru.argustelecom.box.env.billing.reason.model.UserReasonType.UserReasonTypeQuery;

public class UserReasonTypeTestDataUtils implements Serializable {
	
	private static final long serialVersionUID = -1365606271715008536L;

	@Inject
	private UserReasonTypeRepository userReasonTypeRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	public UserReasonType findOrCreateTestUserReasonType(String name) {
		UserReasonTypeQuery query = new UserReasonTypeQuery();
		UserReasonType userReasonType = query.and(query.name().equal(name)).getFirstResult(em);
		if (userReasonType == null) {
			userReasonType = userReasonTypeRepository.createUserReasonType(name);
		}
		Preconditions.checkState(userReasonType != null);
		return userReasonType;

	}


}
