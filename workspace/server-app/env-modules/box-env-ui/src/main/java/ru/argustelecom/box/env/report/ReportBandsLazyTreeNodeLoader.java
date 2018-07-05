package ru.argustelecom.box.env.report;

import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.type.model.Ordinal.comparator;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import ru.argustelecom.system.inf.dataloader.LazyTreeNodeLoader;

@RequestScoped
public class ReportBandsLazyTreeNodeLoader implements LazyTreeNodeLoader<ReportBandDto> {

	@Inject
	private ReportBandModelAppService reportBandModelAppSrv;

	@Inject
	private ReportBandDtoTranslator reportBandDtoTr;

	@Override
	public List<ReportBandDto> loadChildren(ReportBandDto parent) {
		return reportBandModelAppSrv.findChildren(parent.getId()).stream().sorted(comparator())
				.map(reportBandDtoTr::translate).collect(toList());
	}

}