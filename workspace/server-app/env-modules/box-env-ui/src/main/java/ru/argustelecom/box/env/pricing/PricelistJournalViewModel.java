package ru.argustelecom.box.env.pricing;

import static ru.argustelecom.box.env.dto.DefaultDtoConverterUtils.translate;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistJournalMode.ALL;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistJournalMode.COMMON;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistJournalMode.CUSTOM;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.Getter;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.customer.CustomerDtoTranslator;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.CustomerAppService;
import ru.argustelecom.box.env.party.CustomerSegmentRepository;
import ru.argustelecom.box.env.party.CustomerTypeRepository;
import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistJournalMode;
import ru.argustelecom.box.env.pricing.model.PricelistState;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class PricelistJournalViewModel extends ViewModel {

	@Inject
	private CustomerSegmentRepository customerSegmentRp;

	@Inject
	private CustomerTypeRepository customerTypeRp;

	@Inject
	private CustomerDtoTranslator customerDtoTr;

	@Inject
	private PricelistJournalViewState pricelistJournalViewState;

	@Inject
	private CustomerAppService customerAs;

	@Inject
	private CustomerSegmentDtoTranslator customerSegmentDtoTr;

	@Inject
	private CustomerTypeDtoTranslator customerTypeDtoTr;

	@Inject
	private OwnerAppService ownerAs;

	@Inject
	private BusinessObjectDtoTranslator ownerDtoTr;

	@Inject
	@Getter
	private PricelistLazyDataModel lazyDm;

	@Getter
	private List<CustomerSegmentDto> segments;
	@Getter
	private List<CustomerTypeDto> customerTypes;
	@Getter
	private List<BusinessObjectDto<Owner>> owners;
	@Getter
	private boolean renderOwner;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		segments = translate(customerSegmentDtoTr, customerSegmentRp.findAllSegments());
		customerTypes = translate(customerTypeDtoTr, customerTypeRp.getAllCustomerTypes());
		owners = ownerDtoTr.translate( ownerAs.findAll());
		renderOwner = owners.size() > 1;
	}

	public List<PricelistState> getStates() {
		return Arrays.asList(PricelistState.values());
	}

	public List<CustomerDto> completeCustomer(String customerName) {
		if (pricelistJournalViewState.getCustomerType() != null)
			return customerAs.findCustomerBy(pricelistJournalViewState.getCustomerType().getId(), customerName)
					.stream().map(customerDtoTr::translate).collect(Collectors.toList());
		return Collections.emptyList();
	}

	public boolean showCommonColumns() {
		PricelistJournalMode mode = pricelistJournalViewState.getMode();
		return mode.equals(ALL) || mode.equals(COMMON);
	}

	public boolean showCustomColumns() {
		PricelistJournalMode mode = pricelistJournalViewState.getMode();
		return mode.equals(ALL) || mode.equals(CUSTOM);
	}

	private static final long serialVersionUID = 2388116638878841L;

}