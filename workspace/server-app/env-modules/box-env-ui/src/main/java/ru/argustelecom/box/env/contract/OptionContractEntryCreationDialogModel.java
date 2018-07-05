package ru.argustelecom.box.env.contract;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.CommodityRepository;
import ru.argustelecom.box.env.commodity.model.OptionType;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.contract.dto.ContractEntryDto;
import ru.argustelecom.box.env.contract.dto.ContractEntryDtoTranslator;
import ru.argustelecom.box.env.contract.dto.OptionContractEntryCreationDto;
import ru.argustelecom.box.env.contract.dto.OptionEntryServiceDto;
import ru.argustelecom.box.env.contract.dto.OptionEntryServiceDtoTranslator;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.OptionContractEntry;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.TariffAppService;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CustomTariff;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "optionContractEntryCreationDm")
@PresentationModel
public class OptionContractEntryCreationDialogModel implements Serializable {

	@Inject
	private ContractEntryAppService contractEntryAs;

	@Inject
	private TariffAppService tariffAs;

	@Inject
	private CommodityRepository commodityRp;

	@Inject
	private ContractEntryDtoTranslator contractEntryDtoTr;

	@Inject
	private OptionEntryServiceDtoTranslator optionEntryServiceDtoTr;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Setter
	private AbstractContract<?> contract;

	@Setter
	private Callback<ContractEntryDto> callback;

	@Getter
	private OptionContractEntryCreationDto creationDto;

	@Getter
	private List<OptionEntryServiceDto> services;

	@Getter
	private List<BusinessObjectDto<OptionType>> optionTypes;

	@Getter
	private List<BusinessObjectDto<AbstractTariff>> tariffs;

	public void openDialog() {
		creationDto = new OptionContractEntryCreationDto();
		initServices();
		initTariffs();

		RequestContext.getCurrentInstance().execute("PF('entryTypesPanel').hide()");
		RequestContext.getCurrentInstance().update("option_contract_entry_creation_form");
		RequestContext.getCurrentInstance().execute("PF('optionContractEntryCreationDlgVar').show()");
	}

	public void onServiceSelected() {
		initOptions();
	}

	public void submit() {
		//@formatter:off
		OptionContractEntry entry = contractEntryAs.createOptionEntry(
											contract.getId(),
											creationDto.getService().getId(),
											creationDto.getOptionType().getId(),
											creationDto.getTariff().getId()
									);
		//@formatter:on
		callback.execute(contractEntryDtoTr.translate(entry));
	}

	public void cancel() {
		callback = null;
		creationDto = null;
		services = null;
		optionTypes = null;
		tariffs = null;
	}

	private void initServices() {
		List<Service> customerServices = commodityRp.findBy(contract.getCustomer());
		List<Service> customerServicesWithOptionTypes = customerServices.stream()
				.filter(service -> !service.getType().getOptionTypes().isEmpty()).collect(Collectors.toList());
		services = optionEntryServiceDtoTr.translate(customerServicesWithOptionTypes);
	}

	private void initOptions() {
		optionTypes = businessObjectDtoTr
				.translate(((Service) creationDto.getService().getIdentifiable()).getType().getOptionTypes());
	}

	private void initTariffs() {
		List<AbstractTariff> activeTariffs = tariffAs.findActiveTariffs();
		activeTariffs.removeIf(otherCustomersTariff());
		tariffs = businessObjectDtoTr.translate(activeTariffs);
	}

	private Predicate<AbstractTariff> otherCustomersTariff() {
		return tariff -> tariff instanceof CustomTariff
				&& !((CustomTariff) tariff).getCustomer().equals(contract.getCustomer());
	}

	private static final long serialVersionUID = 8625527279307587461L;

}