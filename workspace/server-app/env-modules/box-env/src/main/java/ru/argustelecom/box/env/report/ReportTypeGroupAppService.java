package ru.argustelecom.box.env.report;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.report.model.ReportTypeGroup;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class ReportTypeGroupAppService implements Serializable {

	private static final long serialVersionUID = 2907877529733443160L;

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private ReportTypeGroupRepository rtgRep;

	@Inject
	private ReportTypeRepository rtRep;

	public ReportTypeGroup createReportTypeGroup(@NotNull String name, String keyword) {
		return rtgRep.createReportTypeGroup(name, keyword);
	}

	public void changeReportTypeGroup(@NotNull Long reportTypeGroupId, @NotNull String newName, String newKeyword) {
		ReportTypeGroup group = em.find(ReportTypeGroup.class, reportTypeGroupId);
		rtgRep.changeName(group, newName);
		rtgRep.changeKeyword(group, newKeyword);
	}

	public void removeResourceTypeGroup(@NotNull Long reportTypeGroupId) {
		rtgRep.remove(em.find(ReportTypeGroup.class, reportTypeGroupId));
	}

	public ReportType createReportType(@NotNull String name, String description, Long parentId) {
		ReportTypeGroup parent = em.find(ReportTypeGroup.class, parentId);
		return rtRep.createReportType(name, description, parent);
	}

	public void changeReportType(@NotNull Long reportTypeId, Long reportTypeGroupId, @NotNull String newName,
			String newDescription) {
		ReportType type = em.find(ReportType.class, reportTypeId);
		ReportTypeGroup group = reportTypeGroupId != null ? em.find(ReportTypeGroup.class, reportTypeGroupId) : null;
		rtRep.changeReportType(type, newName, newDescription, group);
	}

	public void removeResourceType(@NotNull Long reportTypeId) {
		rtRep.remove(em.find(ReportType.class, reportTypeId));
	}

	public ReportType findReportType(Long reportTypeId) {
		return em.find(ReportType.class, reportTypeId);
	}

	public ReportTypeGroup findReportTypeGroup(Long reportTypeGroupId) {
		return em.find(ReportTypeGroup.class, reportTypeGroupId);
	}

	public List<ReportType> findReportTypesBy(Long reportTypeGroupId) {
		ReportTypeGroup group = em.find(ReportTypeGroup.class, reportTypeGroupId);
		return rtRep.find(group);
	}

	public List<ReportTypeGroup> findGroups() {
		return rtgRep.findGroups();
	}

	public List<ReportTypeGroup> findRootGroups() {
		return rtgRep.findRootGroups();
	}

}
