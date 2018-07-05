package ru.argustelecom.box.env.contract.dto;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.val;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.billing.bill.PersonalAccountDto;
import ru.argustelecom.box.env.billing.bill.PersonalAccountDtoTranslator;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionRepository;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.contract.ContractEntryRepository;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.service.ServiceDto;
import ru.argustelecom.box.env.task.TelephonyOptionDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ContractEntryDtoTranslator {

	@Inject
	private ContractEntryRepository contractEntryRp;

	@Inject
	private BusinessObjectDtoTranslator businessObjectTr;

	@Inject
	private PersonalAccountDtoTranslator personalAccountDtoTr;

	@Inject
	private TelephonyOptionDtoTranslator telephonyOptionDtoTr;

	@Inject
	private TelephonyOptionRepository telephonyOptionRp;

	public ContractEntryDto translate(ContractEntry contractEntry) {
		boolean isProductOfferingEntry = initializeAndUnproxy(contractEntry) instanceof ProductOfferingContractEntry;
		//@formatter:off
		ContractEntryDto result = ContractEntryDto.builder()
										.id(contractEntry.getId())
										.subjectName(contractEntry.getContractItem().getObjectName())
										.subjectFullName(contractEntry.getObjectName())
										.personalAccount(translatePersonalAccount(contractEntry))
										.addresses(translateLocations(contractEntry))
									.build();
		//@formatter:on

		if (isProductOfferingEntry) {
			fillProductOfferingEntryFields((ProductOfferingContractEntry) contractEntry, result);
		} else {
			fillOptionEntryFields(contractEntry, result);
		}

		return result;
	}

	private void fillOptionEntryFields(ContractEntry contractEntry, ContractEntryDto result) {
		List<TelephonyZone> zones = ((TelephonyOptionType) contractEntry.getOptions().get(0).getType()).getZones();
		Service service = contractEntry.getOptions().get(0).getService();

		result.setOptionContractEntry(true);
		result.setZones(businessObjectTr.translate(zones));
		result.setService(businessObjectTr.translate(service));
		result.setServiceContract(businessObjectTr.translate(service.getSubject().getContract()));
	}

	private void fillProductOfferingEntryFields(ProductOfferingContractEntry contractEntry, ContractEntryDto result) {
		result.setPricelist(translatePricelist(contractEntry));
		result.setServices(translateServices(contractEntry));
	}

	private BusinessObjectDto<AbstractPricelist> translatePricelist(ProductOfferingContractEntry contractEntry) {
		return businessObjectTr.translate(contractEntry.getProductOffering().getPricelist());
	}

	private PersonalAccountDto translatePersonalAccount(ContractEntry contractEntry) {
		if (contractEntry.getPersonalAccount() == null) {
			return null;
		}
		return personalAccountDtoTr.translate(contractEntry.getPersonalAccount());
	}

	private List<String> translateLocations(ContractEntry contractEntry) {
		return contractEntry.getLocations().stream().map(Location::getFullName).collect(Collectors.toList());
	}

	private List<ServiceDto> translateServices(ProductOfferingContractEntry contractEntry) {
		return contractEntryRp.findServicesBySubject(contractEntry).stream().map(service -> {
			val options = telephonyOptionDtoTr.translate(telephonyOptionRp.find(service, contractEntry));
			return new ServiceDto(service.getId(), service.getObjectName(), service.getState(), options);
		}).sorted(Comparator.comparing(ServiceDto::getName)).collect(Collectors.toList());
	}

}