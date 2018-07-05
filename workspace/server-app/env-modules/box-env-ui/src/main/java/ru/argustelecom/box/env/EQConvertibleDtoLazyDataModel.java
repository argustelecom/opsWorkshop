package ru.argustelecom.box.env;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;

import org.primefaces.model.SortMeta;

import lombok.Setter;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel.LazyDataModelPathCallback;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.modelbase.Identifiable;

public abstract class EQConvertibleDtoLazyDataModel<I extends Identifiable, C extends ConvertibleDto, EQ extends EntityQuery, E extends Enum<E>>
		extends BaseConvertibleDtoLazyDataModel<I, C, E, LazyDataModelPathCallback<I>> {

	private static final long serialVersionUID = 1528941247655231406L;

	@PersistenceContext
	private EntityManager em;

	@Override
	@SuppressWarnings("unchecked")
	protected void sortData(List<SortMeta> multiSortMeta) {
		EQ entityQuery = getFilterModel().getEntityQuery(false);
		List<Order> orders = new ArrayList<>();
		if (multiSortMeta != null && !multiSortMeta.isEmpty()) {
			Order o;
			for (SortMeta sm : multiSortMeta) {
				String sortField = sm.getSortField();
				if (!isNullOrEmpty(sortField)) {
					Path<?> pathSortField = getPathValue(sortField).getPath(entityQuery);
					switch (sm.getSortOrder()) {
					case ASCENDING:
						o = entityQuery.criteriaBuilder().asc(pathSortField);
						orders.add(o);
						break;
					case DESCENDING:
						o = entityQuery.criteriaBuilder().desc(pathSortField);
						orders.add(o);
						break;
					case UNSORTED:
					default:
						break;
					}
				}
			}
		}
		entityQuery.criteriaQuery().orderBy(orders);
	}

	@Override
	protected void prepare() {
		EQConvertibleDtoFilterModel<EQ> filterModel = getFilterModel();
		EQ entityQuery = filterModel.getEntityQuery(true);
		filterModel.predicates(true);
		filterModel.buildPredicates(entityQuery);
		filterModel.applyPredicates(entityQuery);
	}

	@Override
	protected int rows() {
		return getFilterModel().getEntityQuery(false).calcRowsCount(em).intValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected TypedQuery<I> getTypedQuery() {
		return getFilterModel().getEntityQuery(false).createTypedQuery(em);
	}

	protected abstract EQConvertibleDtoFilterModel<EQ> getFilterModel();

	@FunctionalInterface
	public interface LazyDataModelPathCallback<I extends Identifiable> {
		Path<?> getPath(EntityQuery<I> entityQuery);
	}
}
