package ru.argustelecom.box.env.mediation;

import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCardViewModel.VIEW_ID;
import static ru.argustelecom.box.env.billing.invoice.model.JobDataType.UNSUITABLE;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.invoice.ChargeJobAppService;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.FilterAggData;
import ru.argustelecom.box.env.commodity.CommodityTypeAppService;
import ru.argustelecom.box.env.commodity.ServiceAppService;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionAppService;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.mediation.UnsuitableCallsListViewModel.UnsuitableCallsContext;
import ru.argustelecom.box.env.telephony.tariff.TariffAppService;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.page.PresentationModel;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@PresentationModel
@Named("unsuitableCallsRecycleDm")
public class UnsuitableCallsRecycleDialogModel implements Serializable {

	private static final long serialVersionUID = 9170336077663435925L;

	@Inject
	private ChargeJobAppService chargeJobAs;

	@Inject
	private ServiceAppService serviceAs;

	@Inject
	private CommodityTypeAppService commodityTypeAs;

	@Inject
	private TelephonyOptionAppService telephonyOptionAs;

	@Inject
	private TariffAppService tariffAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Getter
	private ChargeJobCreationDto newJob;

	@Getter
	private UnsuitableCallsListViewModel.UnsuitableCallsContext context;

	private List<BusinessObjectDto<AbstractTariff>> possibleTariffs;

	public void onDialogOpen(UnsuitableCallsListViewModel.UnsuitableCallsContext context) {
		this.context = context;
		newJob = new ChargeJobCreationDto();
		RequestContext.getCurrentInstance().execute("PF('converterRecycleVar').show()");
	}

	public void cancel() {
		newJob = null;
	}

	public String create() {
		//@formatter:off
		FilterAggData filter = FilterAggData.builder()
					.dateFrom(newJob.getDateFrom())
					.dateTo(newJob.getDateTo())
					.tariffId(newJob.getTariff() != null ? newJob.getTariff().getId() : null)
					.serviceId(newJob.getService() != null ? newJob.getService().getId() : null)
					.processingStage(context.getStage().name())
				.build();
		//@formatter:on

		ChargeJob chargeJob = chargeJobAs.create(UNSUITABLE, filter);
		chargeJobAs.doRechargeJob(chargeJob);
		return outcomeConstructor.construct(VIEW_ID, IdentifiableOutcomeParam.of("chargeJob", chargeJob));
	}

	public List<BusinessObjectDto<Service>> getPossibleServices() {
		if (newJob.getServiceType() == null) {
			return Collections.emptyList();
		}

		List<BusinessObjectDto<Service>> services = businessObjectDtoTr
				.translate(serviceAs.findByType(newJob.getServiceType().getId()));

		return services.stream().filter(service -> !telephonyOptionAs.find(service.getId()).isEmpty())
				.collect(toList());
	}

	public List<BusinessObjectDto<ServiceType>> getPossibleServiceTypes() {
		return businessObjectDtoTr.translate(commodityTypeAs.findAllServiceTypes().stream()
				.filter(serviceType -> !serviceType.getOptionTypes().isEmpty()).collect(toList()));
	}

	public List<BusinessObjectDto<AbstractTariff>> getPossibleTariffs() {
		if (possibleTariffs == null) {
			possibleTariffs = businessObjectDtoTr.translate(tariffAs.findNonFormalizationTariffs());
		}
		return possibleTariffs;
	}
}
