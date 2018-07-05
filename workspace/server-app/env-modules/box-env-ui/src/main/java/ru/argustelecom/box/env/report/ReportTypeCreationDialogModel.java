package ru.argustelecom.box.env.report;

import static java.util.Optional.ofNullable;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.report.model.ReportTypeGroup;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("reportTypeCreationDm")
@PresentationModel
public class ReportTypeCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 8889687570060584570L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ReportTypeRepository rtRep;

	@Inject
	private ReportTypeGroupRepository rtgRep;

	@Inject
	private ReportTypeDtoTranslator reportTypeDtoTr;

	@Inject
	private ReportTypeGroupDtoTranslator reportTypeGroupDtoTr;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String desc;

	@Getter
	@Setter
	private ReportTypeGroupDto group;

	@Setter
	private Callback<ReportTypeDto> reportTypeCallback;

	@Getter
	private List<ReportTypeGroupDto> groups;

	@PostConstruct
	private void postConstruct() {
		groups = rtgRep.findGroups().stream().map(g -> reportTypeGroupDtoTr.translate(g)).collect(Collectors.toList());
	}

	public void create() {
		ReportType type = rtRep.createReportType(name, desc,
				ofNullable(group).map(group -> em.find(ReportTypeGroup.class, group.getId())).orElse(null));
		reportTypeCallback.execute(reportTypeDtoTr.translate(type));

		name = desc = null;
	}

}
