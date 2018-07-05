package ru.argustelecom.box.env.report;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Lists;

import lombok.Getter;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.report.model.ReportParams;
import ru.argustelecom.box.env.report.model.ReportType;
import ru.argustelecom.box.env.report.model.ReportTypeGroup;
import ru.argustelecom.box.env.report.model.ReportTypeState;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named("reportTypeMapVm")
@PresentationModel
public class ReportTypeMapViewModel extends ViewModel {

	@Inject
	private ReportTypeGroupRepository reportTypeGroupRp;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private ReportTypeRepository reportTypeRp;

	@Inject
	private TypeFactory typeFactory;

	@Getter
	private BusinessObjectDto<ReportType> selectedReportType;
	@Getter
	private ReportParams reportParams;
	@Getter
	private List<BusinessObjectDto<ReportTypeGroup>> groups;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		groups = reportTypeGroupRp.findGroups().stream().map(businessObjectDtoTr::translate)
				.collect(toCollection(() -> {
					List<BusinessObjectDto<ReportTypeGroup>> accumulator = Lists.newArrayList();
					// Группа по-умолчанию (без группы)
					accumulator.add(null);
					return accumulator;
				}));
	}

	public List<BusinessObjectDto<ReportType>> getReportTypes(BusinessObjectDto<ReportTypeGroup> group) {
		List<ReportType> reportTypes = ofNullable(group).map(BusinessObjectDto::getIdentifiable)
				.map(reportTypeRp::findByGroup).orElseGet(reportTypeRp::findWithoutGroup);
		reportTypes.removeIf(type -> type.getState().equals(ReportTypeState.BLOCKED));
		return businessObjectDtoTr.translate(reportTypes);
	}

	public void setSelectedReportType(BusinessObjectDto<ReportType> selectedReportType) {
		this.selectedReportType = selectedReportType;
		reportParams = typeFactory.createInstance(selectedReportType.getIdentifiable(), ReportParams.class);
	}

	private static final long serialVersionUID = -951242493427446375L;
}
