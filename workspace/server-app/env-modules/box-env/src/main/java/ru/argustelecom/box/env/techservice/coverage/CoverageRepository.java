package ru.argustelecom.box.env.techservice.coverage;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.techservice.coverage.model.Coverage;
import ru.argustelecom.box.env.techservice.coverage.model.Coverage.CoverageQuery;
import ru.argustelecom.box.env.techservice.coverage.model.CoverageState;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class CoverageRepository implements Serializable {

	private static final long serialVersionUID = -4770246755213172662L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public Coverage create(@NotNull Building building, @NotNull CoverageState state, String note) {
		Coverage coverage = new Coverage(idSequence.nextValue(Coverage.class));

		coverage.setBuilding(building);
		coverage.setState(state);
		coverage.setNote(note);

		em.persist(coverage);
		return coverage;
	}

	public Coverage find(@NotNull Building building) {
		try {
			CoverageQuery query = new CoverageQuery();
			query.and(query.building().equal(building));
			return query.createTypedQuery(em).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public List<Coverage> findAll() {
		return new CoverageQuery().createTypedQuery(em).getResultList();
	}

}