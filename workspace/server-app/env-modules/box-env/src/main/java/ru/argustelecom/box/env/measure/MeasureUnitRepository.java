package ru.argustelecom.box.env.measure;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.measure.model.BaseMeasureUnit;
import ru.argustelecom.box.env.measure.model.DerivedMeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Repository
public class MeasureUnitRepository implements Serializable {

	private static final long serialVersionUID = 4306997033152450816L;

	private static final String FIND_ALL = "MeasureUnitRepository.findAll";
	private static final String FIND_ALL_BASE_MEASURES = "MeasureUnitRepository.findAllBaseMeasures";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public DerivedMeasureUnit createDerivedMeasureUnit(@NotNull String code, @NotNull String name, @NotNull Long factor,
			@NotNull BaseMeasureUnit group, @NotNull String symbol) {
		DerivedMeasureUnit newMeasure = new DerivedMeasureUnit(idSequence.nextValue(MeasureUnit.class));
		newMeasure.setCode(code);
		newMeasure.setName(name);
		newMeasure.setFactor(factor);
		newMeasure.setGroup(group);
		newMeasure.setSymbol(symbol);
		em.persist(newMeasure);
		return newMeasure;
	}

	@NamedQuery(name = FIND_ALL, query = "from MeasureUnit")
	public List<MeasureUnit> findAll() {
		return em.createNamedQuery(FIND_ALL, MeasureUnit.class).getResultList();
	}

	@NamedQuery(name = FIND_ALL_BASE_MEASURES, query = "from BaseMeasureUnit")
	public List<BaseMeasureUnit> findAllBaseMeasures() {
		return em.createNamedQuery(FIND_ALL_BASE_MEASURES, BaseMeasureUnit.class).getResultList();
	}

}
