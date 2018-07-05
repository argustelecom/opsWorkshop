package ru.argustelecom.box.env.numerationpattern;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence.PeriodType;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class NumerationSequenceAppService implements Serializable {

	private static final long serialVersionUID = 8077859396281191417L;

	@Inject
	private NumerationSequenceRepository repository;

	@PersistenceContext
	private EntityManager em;

	public NumerationSequence createNumerationSequence(String name, Long initialValue, Long increment, Integer capacity,
			PeriodType period) {
		return repository.createNumerationSequence(name, initialValue, increment, capacity, period);
	}

	public NumerationSequence editNumerationSequence(Long id, Long initialValue, Long increment, Integer capacity,
			PeriodType period) {
		NumerationSequence numerationSequence = em.find(NumerationSequence.class, id, LockModeType.PESSIMISTIC_WRITE);
		numerationSequence.edit(initialValue, increment, capacity, period);
		return numerationSequence;
	}

	public NumerationSequence findNumerationSequenceByName(String name, boolean lock) {
		return repository.findNumerationSequenceByName(name, lock);
	}

	public void deleteNumerationSequence(Long id) {
		NumerationSequence sequence = em.getReference(NumerationSequence.class, id);
		repository.deleteNumerationSequence(sequence);
	}
}
