package ru.argustelecom.box.env.billing.invoice.unsuitable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.commodity.CommodityRepository;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.SupplierAppService;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.telephony.tariff.TariffAppService;
import ru.argustelecom.box.env.telephony.tariff.TelephonyZoneRepository;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TariffState;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
@Named(value = "unsuitableRatedOutgoingCallsListVm")
public class UnsuitableRatedOutgoingCallsListViewModel extends ViewModel {

	@Inject
	@Getter
	private UnsuitableRatedOutgoingCallsLazyDataModel lazyDm;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private TelephonyZoneRepository telephonyZoneRp;

	@Inject
	private CommodityRepository commodityRp;

	@Inject
	private TariffAppService tariffAs;

	@Inject
	private SupplierAppService supplierAs;

	private List<BusinessObjectDto<TelephonyZone>> zones;
	private List<BusinessObjectDto<Service>> services;
	private List<BusinessObjectDto<AbstractTariff>> tariffs;
	private List<BusinessObjectDto<PartyRole>> suppliers;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	public List<BusinessObjectDto<TelephonyZone>> getZones() {
		if (zones == null) {
			zones = businessObjectDtoTr.translate(telephonyZoneRp.findAll());
		}
		return zones;
	}

	public List<BusinessObjectDto<Service>> getServices() {
		if (services == null) {
			services = businessObjectDtoTr.translate(commodityRp.findAllServices());
		}
		return services;
	}

	public List<BusinessObjectDto<AbstractTariff>> getTariffs() {
		if (tariffs == null) {
			List<TariffState> states = Stream.of(TariffState.values())
					.filter(state -> !state.equals(TariffState.FORMALIZATION) && !state.equals(TariffState.CANCELLED))
					.collect(Collectors.toList());
			tariffs = businessObjectDtoTr.translate(tariffAs.findBy(states));
			tariffs.sort(Comparator.comparing(BusinessObjectDto::getObjectName));
		}
		return tariffs;
	}

	public List<BusinessObjectDto<PartyRole>> getSuppliers() {
		if (suppliers == null) {
			suppliers = businessObjectDtoTr.translate(supplierAs.findAllPossibleSuppliers());
		}
		return suppliers;
	}

	private static final long serialVersionUID = -1024638645971417319L;
}