package ru.argustelecom.box.env.numerationpattern;

import static com.google.common.base.Preconditions.checkState;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence.NumerationSequenceQuery;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence.PeriodType;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence_;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Repository
public class NumerationSequenceRepository {

	private final static String GET_SEQUENCE_NAMES = "NumerationSequenceRepository.getSequenceNames";

	@Inject
	private IdSequenceService idSequenceService;

	@Inject
	private NumerationPatternRepository numerationPatternRepository;

	@PersistenceContext
	private EntityManager em;

	public NumerationSequence createNumerationSequence(String name, Long initialValue, Long increment, Integer capacity,
			PeriodType period) {
		NumerationSequence numerationSequence
				= new NumerationSequence(idSequenceService.nextValue(NumerationSequence.class));

		numerationSequence.setName(name);
		numerationSequence.setInitialValue(initialValue);
		numerationSequence.setIncrement(increment);
		numerationSequence.setCapacity(capacity);
		numerationSequence.setPeriod(period);
		numerationSequence.setValidTo(period.currentValidToDate(new Date()));

		em.persist(numerationSequence);

		return numerationSequence;
	}

	@NamedQuery(name = GET_SEQUENCE_NAMES, query = "select name from NumerationSequence")
	public List<String> getSequenceNames() {
		return em.createNamedQuery(GET_SEQUENCE_NAMES, String.class).getResultList();
	}

	public void deleteNumerationSequence(NumerationSequence sequence) {
		checkState(numerationPatternRepository.canBeDeleted(sequence.getName()), "sequence can't be deleted");
		em.remove(sequence);
	}

	public NumerationSequence findNumerationSequenceByName(String seq, boolean lock) {
		NumerationSequenceQuery query = new NumerationSequenceQuery();
		query.criteriaQuery().where(query.name().equal(seq));
		try {
			return query.createTypedQuery(em).setLockMode(lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NumerationSequence> getAllSequences() {
		NumerationSequenceQuery q = new NumerationSequenceQuery();
		return q.orderBy(q.criteriaBuilder().asc(q.root().get(NumerationSequence_.name))).getResultList(em);
	}
}
