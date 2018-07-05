package ru.argustelecom.box.env.pricing;

import static ru.argustelecom.box.env.pricing.PricelistLazyDataModel.PricelistSort;
import static ru.argustelecom.box.env.pricing.model.AbstractPricelist.PricelistQueryWrapper;
import static ru.argustelecom.box.env.pricing.model.AbstractPricelist.PricelistQueryWrapper.ID;
import static ru.argustelecom.box.env.pricing.model.AbstractPricelist.PricelistQueryWrapper.OBJECT_NAME;
import static ru.argustelecom.box.env.pricing.model.AbstractPricelist.PricelistQueryWrapper.OWNER;
import static ru.argustelecom.box.env.pricing.model.AbstractPricelist.PricelistQueryWrapper.SORT_NAME;
import static ru.argustelecom.box.env.pricing.model.AbstractPricelist.PricelistQueryWrapper.STATE;
import static ru.argustelecom.box.env.pricing.model.AbstractPricelist.PricelistQueryWrapper.VALID_FROM;
import static ru.argustelecom.box.env.pricing.model.AbstractPricelist.PricelistQueryWrapper.VALID_TO;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.JPQLConvertibleDtoFilterModel;
import ru.argustelecom.box.env.JPQLConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class PricelistLazyDataModel extends
		JPQLConvertibleDtoLazyDataModel<AbstractPricelist, PricelistListDto, PricelistQueryWrapper, PricelistSort> {

	@Inject
	private PricelistJournalFilterListModel pricelistJournalFilterListModel;

	@Inject
	private PricelistListDtoTranslator pricelistListDtoTranslator;

	@PostConstruct
	private void postConstruct() {
		initPaths();
	}

	private void initPaths() {
		addPath(PricelistSort.id, ID);
		addPath(PricelistSort.name, OBJECT_NAME);
		addPath(PricelistSort.state, STATE);
		addPath(PricelistSort.validFrom, VALID_FROM);
		addPath(PricelistSort.validTo, VALID_TO);
		addPath(PricelistSort.client, SORT_NAME);
		addPath(PricelistSort.owner, OWNER);
	}

	@Override
	protected Class<PricelistSort> getSortableEnum() {
		return PricelistSort.class;
	}

	@Override
	protected DefaultDtoTranslator<PricelistListDto, AbstractPricelist> getDtoTranslator() {
		return pricelistListDtoTranslator;
	}

	@Override
	protected JPQLConvertibleDtoFilterModel<AbstractPricelist, PricelistQueryWrapper> getFilterModel() {
		return pricelistJournalFilterListModel;
	}

	public enum PricelistSort {
		id, name, state, validFrom, validTo, client, owner
	}

	private static final long serialVersionUID = 7090547588547398473L;
}
