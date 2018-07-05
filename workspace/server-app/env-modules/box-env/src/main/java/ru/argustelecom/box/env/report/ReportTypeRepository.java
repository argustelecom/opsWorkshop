package ru.argustelecom.box.env.report;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.haulmont.yarg.structure.BandOrientation.HORIZONTAL;
import static ru.argustelecom.box.env.report.model.DataLoaderType.GROOVY;
import static ru.argustelecom.box.env.report.model.ReportBandModel.ROOT_BAND_KEYWORD;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.report.model.ReportType.ReportTypeQuery;
import ru.argustelecom.box.env.report.model.ReportTypeGroup;
import ru.argustelecom.box.env.report.model.ReportTypeState;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class ReportTypeRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TypeFactory factory;

	@Inject
	private ReportBandModelRepository reportBandModelRp;

	public ReportType createReportType(String name, String description, ReportTypeGroup group) {
		checkNotNull(name);

		ReportType type = factory.createType(ReportType.class);

		type.setRootBand(reportBandModelRp.createBand(type, GROOVY, type.getRootBand(), ROOT_BAND_KEYWORD, HORIZONTAL));
		type.setName(name);
		type.setDescription(description);
		type.setGroup(group);
		type.setState(ReportTypeState.BLOCKED);

		em.persist(type);

		return type;
	}

	public ReportType createReportType(String name, String description) {
		return createReportType(name, description, null);
	}

	public void changeReportType(ReportType type, String newName, String newDescription, ReportTypeGroup newGroup) {
		checkNotNull(type);

		type.setName(newName);
		type.setDescription(newDescription);
		type.setGroup(newGroup);
	}

	public void remove(ReportType type) {
		checkNotNull(type);

		em.remove(type);
	}

	public List<ReportType> findByGroup(ReportTypeGroup group) {
		ReportTypeQuery reportTypeQuery = new ReportTypeQuery();
		reportTypeQuery.and(reportTypeQuery.group().equal(group));
		return reportTypeQuery.getResultList(em);
	}

	public List<ReportType> find(ReportTypeGroup parent) {
		ReportTypeQuery query = new ReportTypeQuery();
		query.and(query.group().equal(parent));
		return query.getResultList(em);
	}

	public List<ReportType> findAll() {
		return new ReportTypeQuery().getResultList(em);
	}

	public List<ReportType> findWithoutGroup() {
		ReportTypeQuery reportTypeQuery = new ReportTypeQuery();
		reportTypeQuery.and(reportTypeQuery.group().isNull());
		return reportTypeQuery.getResultList(em);
	}

	private static final long serialVersionUID = -7737124561812490610L;
}
