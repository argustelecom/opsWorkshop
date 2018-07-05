package ru.argustelecom.box.env;

import static ru.argustelecom.box.env.util.QueryWrapper.Order;
import static ru.argustelecom.box.env.util.QueryWrapper.OrderBy;

import java.util.List;

import javax.persistence.TypedQuery;

import org.primefaces.model.SortMeta;

import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.util.QueryWrapper;
import ru.argustelecom.system.inf.modelbase.Identifiable;

public abstract class JPQLConvertibleDtoLazyDataModel<I extends Identifiable, C extends ConvertibleDto, QW extends QueryWrapper<I>, E extends Enum<E>>
		extends BaseConvertibleDtoLazyDataModel<I, C, E, String> {

	private static final long serialVersionUID = -6749896883831753462L;

	@Override
	protected void sortData(List<SortMeta> multiSortMeta) {
		for (SortMeta sortMeta : multiSortMeta) {
			String pathValue = getPathValue(sortMeta.getSortField());
			if (pathValue != null) {
				QW queryWrapper = getFilterModel().getQueryWrapper(false);
				switch (sortMeta.getSortOrder()) {
				case ASCENDING:
					queryWrapper.addOrderBy(new OrderBy(pathValue, Order.ASC));
					break;
				case DESCENDING:
					queryWrapper.addOrderBy(new OrderBy(pathValue, Order.DESC));
					break;
				case UNSORTED:
				default:
					break;
				}
			}
		}
	}

	@Override
	protected void prepare() {
		JPQLConvertibleDtoFilterModel<I, QW> filterModel = getFilterModel();
		QW queryWrapper = filterModel.getQueryWrapper(true);
		filterModel.predicates(true);
		filterModel.buildPredicates(queryWrapper);
		filterModel.applyPredicates(queryWrapper);
	}

	@Override
	protected int rows() {
		return getFilterModel().getQueryWrapper(false).countAll(em).intValue();
	}

	@Override
	protected TypedQuery<I> getTypedQuery() {
		return getFilterModel().getQueryWrapper(false).createTypedQuery(em);
	}

	protected abstract JPQLConvertibleDtoFilterModel<I, QW> getFilterModel();
}
