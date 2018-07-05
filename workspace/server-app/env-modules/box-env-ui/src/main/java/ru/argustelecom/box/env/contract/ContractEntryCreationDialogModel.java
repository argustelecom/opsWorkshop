package ru.argustelecom.box.env.contract;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.LocationTypeRepository;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.contract.dto.ContractEntryCreationDto;
import ru.argustelecom.box.env.contract.dto.ContractEntryDto;
import ru.argustelecom.box.env.contract.dto.ContractEntryDtoTranslator;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.pricing.ProductOfferingRepository;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

/**
 * Модель для диалога создания позиции договора, для договора или доп. соглашения.
 */
@Named(value = "contractEntryCreationDm")
@PresentationModel
public class ContractEntryCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -6425484981907465968L;

	@Inject
	private ContractEntryAppService contractEntryAs;

	@Inject
	private ProductOfferingRepository productOfferingRp;

	@Inject
	private LocationTypeRepository locationTypeRp;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private ServicePropertiesEditDialogModel servicePropertiesEditDm;

	@Inject
	private ContractEntryDtoTranslator contractEntryDtoTr;

	@Setter
	private AbstractContract<?> contract;

	@Setter
	private Callback<ContractEntryDto> callback;

	@Getter
	@Setter
	private ContractEntryCreationDto entryCreationDto;

	@Getter
	private List<BusinessObjectDto<AbstractPricelist>> pricelists;

	@Getter
	private List<BusinessObjectDto<LocationType>> lodgingTypes;

	@Getter
	@Setter
	private boolean editServicesAfterEntryCreation = true;

	private ContractMessagesBundle contractMb;

	@PostConstruct
	protected void postConstruct() {
		contractMb = LocaleUtils.getMessages(ContractMessagesBundle.class);
	}

	public void onCreationDialogOpen() {
		entryCreationDto = new ContractEntryCreationDto();

		initPricelists();
		initLodgingTypes();

		if (pricelists.isEmpty()) {
			Notification.error(contractMb.cannotAddEntry(), contractMb.noActivePriceList());
		} else {
			RequestContext.getCurrentInstance().execute("PF('entryTypesPanel').hide()");
			RequestContext.getCurrentInstance().update("contract_entry_creation_form");
			RequestContext.getCurrentInstance().execute("PF('contractEntryCreationDlgVar').show()");
		}
	}

	public void createEntry() {
		//@formatter:off
		ContractEntry entry = contractEntryAs.createProductOfferingEntry(
				contract.getId(),
				entryCreationDto.getProductOffering().getId(),
				entryCreationDto.getBuilding().getId(),
				ofNullable(entryCreationDto.getLodgingType()).map(BusinessObjectDto::getId).orElse(null),
				entryCreationDto.getLodgingNumber(),
				null
		);
		//@formatter:on
		ContractEntryDto entryDto = contractEntryDtoTr.translate(entry);
		callback.execute(entryDto);

		if (editServicesAfterEntryCreation) {
			openServicePropertiesEditDlg(entryDto);
		}

		cancel();
	}

	public void cancel() {
		entryCreationDto = null;
		contract = null;
		editServicesAfterEntryCreation = true;
	}

	public List<BusinessObjectDto<ProductOffering>> getPricelistEntries() {
		BusinessObjectDto<AbstractPricelist> pricelist = entryCreationDto.getPricelist();
		if (pricelist != null) {
			List<ProductOffering> recurrentProductOfferings = productOfferingRp
					.findProductOfferings(pricelist.getIdentifiable()).stream()
					.filter(ProductOffering::isRecurrentProduct).collect(toList());
			return businessObjectDtoTr.translate(recurrentProductOfferings);
		}
		return Collections.emptyList();
	}

	public boolean pricelistSelected() {
		return entryCreationDto.getPricelist() != null;
	}

	private void openServicePropertiesEditDlg(ContractEntryDto entryDto) {
		servicePropertiesEditDm.setServices(entryDto.getServices());
		RequestContext.getCurrentInstance().update("service_spec_edit_dlg");
		RequestContext.getCurrentInstance().execute("PF('serviceSpecEditDlg').show()");
	}

	private void initLodgingTypes() {
		lodgingTypes = businessObjectDtoTr.translate(locationTypeRp.findLodgingTypes());
	}

	private void initPricelists() {
		pricelists = businessObjectDtoTr.translate(contractEntryAs.findPricelists(contract.getId()));
	}

}