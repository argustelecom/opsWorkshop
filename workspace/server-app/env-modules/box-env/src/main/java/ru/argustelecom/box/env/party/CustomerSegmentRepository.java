package ru.argustelecom.box.env.party;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.party.model.CustomerSegment.CustomerSegmentQuery;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class CustomerSegmentRepository {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public CustomerSegment createSegment(CustomerType customerType, String name, String description) {
		CustomerSegment segment = new CustomerSegment(idSequence.nextValue(CustomerSegment.class), customerType);
		segment.setObjectName(name);
		segment.setDescription(description);

		em.persist(segment);
		return segment;
	}

	public List<CustomerSegment> findAllSegments() {
		return new CustomerSegmentQuery().createTypedQuery(em).getResultList();
	}

	public List<CustomerSegment> findSegments(Customer customer) {
		return findSegments(customer.getTypeInstance().getType());
	}

	public List<CustomerSegment> findSegments(CustomerType customerType) {
		CustomerSegmentQuery query = new CustomerSegmentQuery();
		query.and(query.customerType().equal(checkNotNull(customerType)));
		return query.createTypedQuery(em).getResultList();
	}
}
