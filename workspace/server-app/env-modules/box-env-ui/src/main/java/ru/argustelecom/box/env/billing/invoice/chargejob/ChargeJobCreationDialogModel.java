package ru.argustelecom.box.env.billing.invoice.chargejob;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCardViewModel.VIEW_ID;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCreationDialogModel.CreationCase.CUSTOMER_WITH_SERVICE;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCreationDialogModel.CreationCase.CUSTOMER_WITH_SERVICE_AND_TARIFF;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCreationDialogModel.CreationCase.CUSTOMER_WITH_TARIFF;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCreationDialogModel.CreationCase.FULL_RECHARGING;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCreationDialogModel.CreationCase.ONLY_CUSTOMER;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCreationDialogModel.CreationCase.ONLY_SERVICE;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCreationDialogModel.CreationCase.ONLY_TARIFF;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCreationDialogModel.CreationCase.TARIFF_AND_SERVICE;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCreationDialogModel.CreationCase.casesWithCustomerWithoutService;
import static ru.argustelecom.box.env.billing.invoice.chargejob.ChargeJobCreationDialogModel.Step.findBy;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;

import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.ChargeJobAppService;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.FilterAggData;
import ru.argustelecom.box.env.billing.invoice.model.JobDataType;
import ru.argustelecom.box.env.billing.invoice.nls.ChargeJobMessagesBundle;
import ru.argustelecom.box.env.commodity.CommodityTypeAppService;
import ru.argustelecom.box.env.commodity.ServiceAppService;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionAppService;
import ru.argustelecom.box.env.commodity.telephony.model.Option;
import ru.argustelecom.box.env.contract.ContractAppService;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.CustomerAppService;
import ru.argustelecom.box.env.party.CustomerTypeAppService;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.telephony.tariff.TariffAppService;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("chargeJobCreationDm")
public class ChargeJobCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -7304392816246255907L;

	@Inject
	private CustomerTypeAppService customerTypeAs;

	@Inject
	private CustomerAppService customerAs;

	@Inject
	private CommodityTypeAppService commodityTypeAs;

	@Inject
	private TariffAppService tariffAs;

	@Inject
	private ServiceAppService serviceAs;

	@Inject
	private ChargeJobAppService chargeJobAs;

	@Inject
	private TelephonyOptionAppService telephonyOptionAs;

	@Inject
	private ContractAppService contractAs;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private ServiceChargeJobContextDtoTranslator serviceChargeJobContextDtoTr;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Getter
	private RechargeJobCreationDto newJob;

	@Getter
	private List<ServiceChargeJobContextDto> services;

	@Getter
	private boolean noServicesForRecharging = false;

	private CreationCase creationCase;

	private List<BusinessObjectDto<CustomerType>> possibleCustomerTypes;
	private List<BusinessObjectDto<AbstractTariff>> possibleTariffs;

	public void onDialogOpen() {
		newJob = new RechargeJobCreationDto();

		RequestContext.getCurrentInstance().update("charge_job_creation_form");
		RequestContext.getCurrentInstance().execute("PF('chargeJobCreationDlgVar').show()");
	}

	public String handleImportFlow(FlowEvent event) {
		switch (findBy(event.getNewStep())) {
		case SELECTION_CAUSE:
			reset();
			break;
		case INPUT_PARAMS:
			services = null;
			break;
		case FINISHED:
			initCreationCase();
			validation();
			break;
		default:
			throw new SystemException("Unsupported step");
		}
		return event.getNewStep();
	}

	public String createJobs() {
		//@formatter:off
		List<ChargeJob> jobs =  generateFilters().stream()
				.map(filter -> chargeJobAs.create(JobDataType.SUITABLE, filter))
				.collect(toList());
		//@formatter:on

		jobs.forEach(chargeJobAs::doRechargeJob);

		return jobs.size() > 1 ? StringUtils.EMPTY
				: outcomeConstructor.construct(VIEW_ID, IdentifiableOutcomeParam.of("chargeJob", jobs.get(0)));
	}

	private void initCreationCase() {
		if (newJob.getCustomer() != null) {
			if (newJob.getService() == null && newJob.getTariff() == null) {
				creationCase = ONLY_CUSTOMER;
			} else if (newJob.getService() != null && newJob.getTariff() == null) {
				creationCase = CUSTOMER_WITH_SERVICE;
			} else if (newJob.getService() == null && newJob.getTariff() != null) {
				creationCase = CUSTOMER_WITH_TARIFF;
			} else {
				creationCase = CUSTOMER_WITH_SERVICE_AND_TARIFF;
			}
		} else if (newJob.getService() != null && newJob.getTariff() != null) {
			creationCase = TARIFF_AND_SERVICE;
		} else if (newJob.getService() != null && newJob.getTariff() == null) {
			creationCase = ONLY_SERVICE;
		} else if (newJob.getService() == null && newJob.getTariff() != null) {
			creationCase = ONLY_TARIFF;
		} else {
			creationCase = FULL_RECHARGING;
		}
	}

	private void validation() {
		if (casesWithCustomerWithoutService().contains(creationCase)) {
			List<Long> serviceIds = findCustomerServices().stream().map(ServiceChargeJobContextDto::getId)
					.collect(toList());

			services = serviceIds.isEmpty() ? Collections.emptyList()
					: serviceChargeJobContextDtoTr.translate(serviceAs.findCrossingServices(serviceIds,
							newJob.getFilter().getDateFrom(), newJob.getFilter().getDateTo()));
			noServicesForRecharging = services.isEmpty();

		}

		if (creationCase.equals(ONLY_SERVICE)) {
			noServicesForRecharging = serviceAs
					.findCrossingServices(Collections.singletonList(newJob.getService().getId()),
							newJob.getFilter().getDateFrom(), newJob.getFilter().getDateTo())
					.isEmpty();
		}

		if (creationCase.equals(TARIFF_AND_SERVICE) || creationCase.equals(CUSTOMER_WITH_SERVICE_AND_TARIFF)) {
			noServicesForRecharging = serviceAs
					.findCrossingServicesWithTariff(newJob.getTariff().getId(), newJob.getFilter().getDateFrom(),
							newJob.getFilter().getDateTo(), Collections.singletonList(newJob.getService().getId()))
					.isEmpty();
		}

		if (creationCase.equals(ONLY_TARIFF)) {
			services = serviceChargeJobContextDtoTr.translate(serviceAs.findCrossingServicesByTariff(
					newJob.getTariff().getId(), newJob.getFilter().getDateFrom(), newJob.getFilter().getDateTo()));
			noServicesForRecharging = services.isEmpty();
		}
	}

	private List<FilterAggData> generateFilters() {
		return Optional.ofNullable(services).map(list -> list.stream().map(service -> {
			FilterAggData filter = newJob.getFilter();

			if (!creationCase.equals(ONLY_TARIFF)) {
				filter.setServiceId(service.getId());
			}

			return filter;
		}).collect(toList())).orElse(Collections.singletonList(newJob.getFilter()));

	}

	private List<ServiceChargeJobContextDto> findCustomerServices() {
		List<Contract> contracts = contractAs.findContracts(newJob.getCustomer().getId());
		List<ContractEntry> entries = contracts.stream().flatMap(contract -> contract.getEntries().stream())
				.collect(toList());
		List<Option> options = entries.stream().flatMap(entry -> entry.getOptions().stream()).distinct()
				.collect(toList());
		List<Service> servicesWithOptionType = options.stream().map(Option::getService).distinct().collect(toList());

		return serviceChargeJobContextDtoTr.translate(servicesWithOptionType);
	}

	public void onCancel() {
		reset();
	}

	public void reset() {
		newJob = new RechargeJobCreationDto();
		services = null;
		possibleCustomerTypes = null;
		possibleTariffs = null;
		noServicesForRecharging = false;
		creationCase = null;
	}

	public String getValidationMessageForInputParamsStep() {
		// Метод сделан умышленно. validateOneOrMore не может получить сообщение из
		// #{chargeJobBundle['box.charge_job.wizard.step.input_params.validation.msg']}, поэтому получение сообщения
		// вынесено в метод.
		ChargeJobMessagesBundle messages = LocaleUtils.getMessages(ChargeJobMessagesBundle.class);
		return messages.serviceOrTariffShouldBeEntered();
	}

	public List<? extends BusinessObjectDto<? extends Customer>> completeCustomer(String customerName) {
		if (newJob.getCustomerType() != null) {
			return businessObjectDtoTr
					.translate(customerAs.findCustomerBy(newJob.getCustomerType().getId(), customerName));
		}

		return Collections.emptyList();
	}

	public List<ServiceChargeJobContextDto> getPossibleServices() {
		if (newJob.getServiceType() == null) {
			return Collections.emptyList();
		}

		if (newJob.getCustomer() == null) {
			List<ServiceChargeJobContextDto> services = serviceChargeJobContextDtoTr
					.translate(serviceAs.findByType(newJob.getServiceType().getId()));

			return services.stream().filter(service -> !telephonyOptionAs.find(service.getId()).isEmpty())
					.collect(toList());
		}

		List<ServiceChargeJobContextDto> customerServices = serviceChargeJobContextDtoTr
				.translate(serviceAs.findBy(newJob.getCustomer().getId(), newJob.getServiceType().getId()));
		return customerServices.stream().filter(service -> !telephonyOptionAs.find(service.getId()).isEmpty())
				.collect(toList());
	}

	public List<BusinessObjectDto<ServiceType>> getPossibleServiceTypes() {
		if (newJob.getCustomer() == null) {
			return businessObjectDtoTr.translate(commodityTypeAs.findAllServiceTypes().stream()
					.filter(serviceType -> !serviceType.getOptionTypes().isEmpty()).collect(toList()));
		}

		return businessObjectDtoTr.translate(serviceAs.findBy(newJob.getCustomer().getId()).stream()
				.filter(service -> !service.getType().getOptionTypes().isEmpty()).map(Service::getType).distinct()
				.collect(toList()));
	}

	public List<BusinessObjectDto<CustomerType>> getPossibleCustomerTypes() {
		if (possibleCustomerTypes == null) {
			possibleCustomerTypes = businessObjectDtoTr.translate(customerTypeAs.findAllCustomerTypes());
		}
		return possibleCustomerTypes;
	}

	public List<BusinessObjectDto<AbstractTariff>> getPossibleTariffs() {
		if (possibleTariffs == null) {
			possibleTariffs = businessObjectDtoTr.translate(tariffAs.findNonFormalizationTariffs());
		}
		return possibleTariffs;
	}

	public enum CreationCase {
		//@formatter:off
		FULL_RECHARGING						(false, false, false),
		ONLY_CUSTOMER						(true,  false, false),
		ONLY_SERVICE						(false, true,  false),
		ONLY_TARIFF							(false, false, true),
		TARIFF_AND_SERVICE					(false, true,  true),
		CUSTOMER_WITH_TARIFF				(true,  false, true),
		CUSTOMER_WITH_SERVICE				(true,  true,  false),
		CUSTOMER_WITH_SERVICE_AND_TARIFF	(true,  true,  true);
		//@formatter:on

		private boolean hasCustomer;
		private boolean hasService;
		private boolean hasTariff;

		CreationCase(boolean hasCustomer, boolean hasService, boolean hasTariff) {
			this.hasCustomer = hasCustomer;
			this.hasService = hasService;
			this.hasTariff = hasTariff;
		}

		public static List<CreationCase> casesWithCustomerWithoutService() {
			return Arrays.stream(values()).filter(cause -> cause.hasCustomer && !cause.hasService).collect(toList());
		}
	}

	public enum Step {
		//@formatter:off
		SELECTION_CAUSE  		("selection_cause"),
		INPUT_PARAMS     		("input_params"),
		FINISHED   				("finished");
		//@formatter:on

		@Getter
		private String id;

		Step(String id) {
			this.id = id;
		}

		public static Step findBy(String id) {
			return stream(values()).filter(step -> step.getId().equals(id)).findFirst()
					.orElseThrow(SystemException::new);
		}
	}

	public enum RechargeCause {
		//@formatter:off
		SELECT_WRONG_TARIFF						("conv_stage_3"),
		ERRORS_IN_TARIFF						("rating_stage"),
		ERROR_IN_SERVICE_RESOURCE_RELATIONSHIP	("conv_stage_3"),
		ERRORS_IN_PRE_BILLING_SETTINGS			("conv_stage_2");
		//@formatter:on

		@Getter
		private String processingStage;

		RechargeCause(String processingStage) {
			this.processingStage = processingStage;
		}

		public String getName() {
			ChargeJobMessagesBundle messages = LocaleUtils.getMessages(ChargeJobMessagesBundle.class);

			switch (this) {
			case SELECT_WRONG_TARIFF:
				return messages.selectWrongTariff();
			case ERRORS_IN_TARIFF:
				return messages.errorsInTariff();
			case ERROR_IN_SERVICE_RESOURCE_RELATIONSHIP:
				return messages.incorrectResourceServiceRelationship();
			case ERRORS_IN_PRE_BILLING_SETTINGS:
				return messages.incorrectPreBillingSettings();
			default:
				throw new SystemException("Unsupported RechargeCause");
			}
		}
	}
}
