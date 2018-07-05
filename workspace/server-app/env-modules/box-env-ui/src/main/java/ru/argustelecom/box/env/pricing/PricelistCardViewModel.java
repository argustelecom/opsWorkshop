package ru.argustelecom.box.env.pricing;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class PricelistCardViewModel extends ViewModel {

	private static final long serialVersionUID = 5394773346785934898L;

	private static final Logger log = Logger.getLogger(PricelistCardViewModel.class);

	public static final String VIEW_ID = "/views/env/pricing/PricelistCardView.xhtml";

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private PricelistRepository pricelistRepository;

	@Inject
	private CurrentPricelist currentPricelist;

	@Inject
	private PricelistCardViewState viewState;

	@Inject
	private PricelistAttributesDtoTranslator pricelistAttributesDtoTr;

	private AbstractPricelist pricelist;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		unitOfWork.makePermaLong();
	}

	@Override
	public void preRender() {
		super.preRender();
		refresh();
	}

	public String removePriceList() {
		pricelistRepository.removePriceList(pricelist);
		return outcomeConstructor.construct("/views/env/pricing/PricelistJournalView.xhtml");
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		pricelist = currentPricelist.getValue();
		viewState.setPricelistDto(pricelistAttributesDtoTr.translate(pricelist));
		log.debugv("postConstruct. pricelist_id={0}", pricelist.getId());
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public AbstractPricelist getPricelist() {
		return pricelist;
	}
}