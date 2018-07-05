package ru.argustelecom.box.env.pricing;

import lombok.Getter;
import lombok.Setter;

import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistJournalMode.COMMON;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistJournalMode.CUSTOM;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.party.CustomerRepository;
import ru.argustelecom.box.env.party.CustomerSegmentRepository;
import ru.argustelecom.box.env.party.CustomerTypeRepository;
import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistJournalMode;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class PricelistCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -722005442864397419L;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private CustomerSegmentRepository customerSegmentRp;

	@Inject
	private CustomerTypeRepository customerTypeRp;

	@Inject
	private OwnerAppService ownerAs;

	@Inject
	private CustomerRepository customerRp;

	@Inject
	private PricelistRepository pricelistRp;

	@Getter
	@Setter
	private PricelistCreationDto pricelistCreationDto;
	private PricelistJournalMode mode;

	@Getter
	private List<Owner> owners;

	@Getter
	private boolean renderOwner;

	private List<CustomerSegment> segments;
	private List<CustomerType> customerTypes;

	public void onCreationDialogOpen() {
		pricelistCreationDto = new PricelistCreationDto();
		owners = ownerAs.findAll();
		renderOwner = owners.size() > 1;

		if (!renderOwner) {
			pricelistCreationDto.setOwner(owners.iterator().next());
		}

		RequestContext.getCurrentInstance().execute("PF('pricelistCreationPanelVar').hide()");
		RequestContext.getCurrentInstance().update("pricelist_creation_form");
		RequestContext.getCurrentInstance().execute("PF('pricelistCreationDlgVar').show()");
	}

	public String onCreated() {
		return outcomeConstructor.construct(PricelistCardViewModel.VIEW_ID,
				IdentifiableOutcomeParam.of("pricelist", create()));
	}

	@SuppressWarnings("Duplicates")
	public void cleanCreationParams() {
		mode = null;
		pricelistCreationDto = null;
	}

	public List<PricelistJournalMode> getModesForCreationButton() {
		return PricelistJournalMode.getModesForCreation();
	}

	public List<CustomerSegment> getSegments() {
		if (segments == null)
			segments = customerSegmentRp.findAllSegments();
		return segments;
	}

	public List<CustomerType> getCustomerTypes() {
		if (customerTypes == null)
			customerTypes = customerTypeRp.getAllCustomerTypes().stream()
					.map(EntityManagerUtils::initializeAndUnproxy).collect(Collectors.toList());
		return customerTypes;
	}

	public List<? extends Customer> completeCustomer(String customerName) {
		if (pricelistCreationDto.getCustomerType() != null)
			return customerRp.findCustomerBy(pricelistCreationDto.getCustomerType(), customerName);
		return Collections.emptyList();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private AbstractPricelist create() {
		AbstractPricelist pricelist = null;
		if (mode.equals(COMMON))
			pricelist = pricelistRp.createCommonPricelist(
					pricelistCreationDto.getName(),
					pricelistCreationDto.getValidFrom(),
					pricelistCreationDto.getValidTo(),
					pricelistCreationDto.getSegments(),
					pricelistCreationDto.getOwner());
		if (mode.equals(CUSTOM))
			pricelist = pricelistRp.createCustomPricelist(
					pricelistCreationDto.getName(),
					pricelistCreationDto.getValidFrom(),
					pricelistCreationDto.getValidTo(),
					pricelistCreationDto.getCustomer(),
					pricelistCreationDto.getOwner());
		cleanCreationParams();
		return pricelist;

	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public PricelistJournalMode getMode() {
		return mode;
	}

	public void setMode(PricelistJournalMode mode) {
		this.mode = mode;
	}

}