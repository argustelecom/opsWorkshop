package ru.argustelecom.box.env.lifecycle.impl;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.getProxiedClass;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.val;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;
import ru.argustelecom.box.env.lifecycle.api.history.LifecycleHistoryService;
import ru.argustelecom.box.env.lifecycle.api.history.model.Initiator;
import ru.argustelecom.box.env.lifecycle.api.history.model.InitiatorType;
import ru.argustelecom.box.env.lifecycle.api.history.model.LifecycleHistoryItem;
import ru.argustelecom.box.env.lifecycle.api.history.model.LifecycleHistoryItem.LifecycleHistoryItemQuery;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class LifecycleHistoryRepository implements LifecycleHistoryService {

	private static final String QUEUE_INITIATOR = "Очередь обработки";
	private static final String DEFAULT_LIFECYCLE_KEYWORD = "DEFAULT";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequenceService;

	@Override
	public List<LifecycleHistoryItem> getHistory(LifecycleObject<?> businessObject) {
		val query = new LifecycleHistoryItemQuery();
		//@formatter:off
		return query
			.and(query.lifecycleObjectId().equal(businessObject.getId()))
			.and(query.lifecycleObjectEntity().equal(getProxiedClass(businessObject).getSimpleName()))
			.getResultList(em);
		//@formatter:on
	}

	public LifecycleHistoryItem createRoutingHistory(LifecycleObject<?> businessObject,
			LifecycleState<?> initialState, Date transitionTime) {
		return save(null, businessObject, initialState, transitionTime);
	}

	public LifecycleHistoryItem saveRoutingHistory(Lifecycle<?, ?> lifecycle, LifecycleObject<?> businessObject,
			LifecycleState<?> initialState) {
		checkRequiredArgument(lifecycle, "lifecycle");
		return save(lifecycle, businessObject, initialState, new Date());
	}

	private LifecycleHistoryItem save(Lifecycle<?, ?> lifecycle, LifecycleObject<?> businessObject,
			LifecycleState<?> initialState, Date transitionTime) {
		checkRequiredArgument(businessObject, "businessObject");
		checkRequiredArgument(initialState, "initialState");
		checkRequiredArgument(transitionTime, "transitionTime");

		val objectId = businessObject.getId();
		val objectEntity = getProxiedClass(businessObject).getSimpleName();

		val isKeywordPresent = lifecycle != null && lifecycle.getKeyword() != null;
		val lifecycleKeyword = isKeywordPresent ? lifecycle.getKeyword().toString() : DEFAULT_LIFECYCLE_KEYWORD;

		val fromState = initialState.getName();
		val toState = businessObject.getState().getName();
		val initiator = determineInitiator();

		return createItem(objectId, objectEntity, lifecycleKeyword, fromState, toState, transitionTime, initiator);
	}

	private Initiator determineInitiator() {
		val principal = EmployeePrincipal.instance();
		if (principal != null) {
			val employee = em.find(Employee.class, principal.getEmployeeId());
			checkState(employee != null);
			return Initiator.of(employee.getId(), InitiatorType.USER, employee.getObjectName());
		}
		return Initiator.of(InitiatorType.QUEUE, QUEUE_INITIATOR);
	}

	private LifecycleHistoryItem createItem(Long lifecycleObjectId, String lifecycleObjectEntity, String lifecycle,
			String fromState, String toState, Date transitionTime, Initiator initiator) {

		//@formatter:off
		LifecycleHistoryItem item = LifecycleHistoryItem.builder()
			.id(idSequenceService.nextValue(LifecycleHistoryItem.class))
			.lifecycleObjectId(lifecycleObjectId)
			.lifecycleObjectEntity(lifecycleObjectEntity)
			.lifecycle(lifecycle)
			.fromState(fromState)
			.toState(toState)
			.transitionTime(transitionTime)
			.initiator(initiator)
		.build();
		//@formatter:on

		em.persist(item);
		return item;
	}
}