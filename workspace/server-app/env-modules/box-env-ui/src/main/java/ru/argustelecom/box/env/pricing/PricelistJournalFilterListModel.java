package ru.argustelecom.box.env.pricing;

import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistJournalMode;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.CUSTOMER;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.MODE;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.NAME;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.OWNER;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.SEGMENT;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.STATE;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.VALID_FROM;
import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistFilter.VALID_TO;

import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.BaseJPQLConvertibleDtoFilterModel;
import ru.argustelecom.box.env.customer.CustomerDto;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist.PricelistQueryWrapper;
import ru.argustelecom.box.env.util.QueryWrapper;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class PricelistJournalFilterListModel
		extends BaseJPQLConvertibleDtoFilterModel<AbstractPricelist, PricelistQueryWrapper> {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private PricelistJournalViewState pricelistJournalViewState;

	private PricelistQueryWrapper pricelistQueryWrapper;

	@SuppressWarnings({ "unchecked", "ConstantConditions", "ConfusingArgumentToVarargsMethod" })
	@Override
	public void buildPredicates(QueryWrapper<AbstractPricelist> queryWrapper) {
		Map<String, Object> filterMap = pricelistJournalViewState.getFilterMap();
		for (Map.Entry<String, Object> filterEntry : filterMap.entrySet()) {
			if (filterEntry != null)
				switch (filterEntry.getKey()) {
				case NAME:
					addPredicate(queryWrapper.like(PricelistQueryWrapper.OBJECT_NAME,
							String.format("%%%s%%", filterEntry.getValue())));
					break;
				case VALID_FROM:
					addPredicate(
							queryWrapper.greaterOrEqualsThen(PricelistQueryWrapper.VALID_FROM, filterEntry.getValue()));
					break;
				case VALID_TO:
					addPredicate(queryWrapper.lessOrEqualsThen(PricelistQueryWrapper.VALID_TO, filterEntry.getValue()));
					break;
				case STATE:
					addPredicate(queryWrapper.equals(PricelistQueryWrapper.STATE, filterEntry.getValue()));
					break;
				case SEGMENT:
					addPredicate(queryWrapper.isMember(PricelistQueryWrapper.SEGMENT,
							((CustomerSegmentDto) filterEntry.getValue()).getIdentifiable(em)));
					break;
				case CUSTOMER:
					addPredicate(queryWrapper.equals(PricelistQueryWrapper.CUSTOMER,
							((CustomerDto) filterEntry.getValue()).getIdentifiable(em)));
					break;
				case MODE:
					addPredicate(queryWrapper.in(PricelistQueryWrapper.DTYPE,
							PricelistJournalMode.valueOf(filterEntry.getValue().toString()).getDtypes()));
					break;
				case OWNER:
					addPredicate(queryWrapper.equals(PricelistQueryWrapper.OWNER,
							((BusinessObjectDto<Owner>) filterEntry.getValue()).getIdentifiable(em)));
					break;
				default:
					break;
				}
		}
	}

	@Override
	public PricelistQueryWrapper getQueryWrapper(boolean isNew) {
		if (isNew) {
			pricelistQueryWrapper = new PricelistQueryWrapper();
		}
		return pricelistQueryWrapper;
	}

	private static final long serialVersionUID = 7643938117066681620L;
}
