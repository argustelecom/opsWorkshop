package ru.argustelecom.box.env.report;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.report.model.ReportTypeGroup;
import ru.argustelecom.box.env.report.model.ReportTypeGroup.ReportTypeGroupQuery;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class ReportTypeGroupRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService iss;

	public ReportTypeGroup createReportTypeGroup(String name, String keyword) {
		checkNotNull(name);

		ReportTypeGroup group = new ReportTypeGroup(iss.nextValue(ReportTypeGroup.class));
		group.setObjectName(name);
		group.setKeyword(keyword);

		em.persist(group);

		return group;
	}

	public ReportTypeGroup createReportTypeGroup(String name, String keyword, ReportTypeGroup parent) {
		checkNotNull(parent);

		ReportTypeGroup group = createReportTypeGroup(name, keyword);
		group.changeParent(parent);

		return group;
	}

	public void changeName(ReportTypeGroup group, String newName) {
		checkNotNull(group);
		checkNotNull(newName);

		group.setObjectName(newName);
	}

	public void changeKeyword(ReportTypeGroup group, String newKeyword) {
		checkNotNull(group);

		group.setKeyword(newKeyword);
	}

	public ReportTypeGroup findGroupByKeyword(String keyword) {
		ReportTypeGroupQuery query = new ReportTypeGroupQuery();
		query.and(query.keyword().equal(keyword));
		return query.getSingleResult(em);
	}

	public List<ReportTypeGroup> findRootGroups() {
		ReportTypeGroupQuery query = new ReportTypeGroupQuery();
		query.and(query.parent().isNull());
		return query.getResultList(em);
	}

	public List<ReportTypeGroup> findGroups() {
		return new ReportTypeGroupQuery().getResultList(em);
	}

	public List<ReportTypeGroup> findChildrenGroups(ReportTypeGroup parentGroup) {
		ReportTypeGroupQuery query = new ReportTypeGroupQuery();
		query.and(query.parent().equal(parentGroup));
		return query.getResultList(em);
	}

	public void remove(ReportTypeGroup group) {
		checkNotNull(group);
		em.remove(group);
	}

	private static final long serialVersionUID = -264879439896730066L;
}
