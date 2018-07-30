package ru.argustelecom.box.env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

public abstract class BaseConvertibleDtoLazyDataModel<I extends Identifiable, C extends ConvertibleDto, E extends Enum<E>, T>
		extends LazyDataModel<C> {

	private static final long serialVersionUID = 3688707538760618906L;

	@PersistenceContext
	protected EntityManager em;

	protected List<C> objects = new ArrayList<>();
	private Map<E, T> pathMap = new EnumMap<>(getSortableEnum());
	private List<SortMeta> currentSortMode;

	private int begin;

	/**
	 * Последний элемент (не включая).
	 */
	private int end;

	/**
	 * Число подгружаемых записей. Должно быть кратно pageSize.
	 */
	private int loadBatchSize = 100;

	private boolean needReload = true;

	private int oldPageSize = 0;

	public void reloadData() {
		needReload = true;
	}

	@Override
	public List<C> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<SortMeta> singleSort = new ArrayList<>();
		if (sortField != null) {
			SortMeta sm = new SortMeta();
			sm.setSortField(sortField);
			sm.setSortOrder(sortOrder);
			singleSort.add(sm);
		}
		return load(first, pageSize, singleSort, filters);
	}

	@Override
	public List<C> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {

		if (oldPageSize != pageSize && pageSize != 0) {
			loadBatchSize = Math.round(loadBatchSize / pageSize) * pageSize;
			oldPageSize = pageSize;
		}

		if (!sortModeEquals(currentSortMode, multiSortMeta)) {
			currentSortMode = multiSortMeta;
			reloadData();
		}

		if (!(first > begin && first + pageSize < end) || needReload) {
			prepare();

			if (multiSortMeta != null && !multiSortMeta.isEmpty()) {
				sortData(multiSortMeta);
			}

			if (first >= end || first + pageSize <= begin || needReload) {
				begin = first <= loadBatchSize ? 0 : first - loadBatchSize;
				objects = nextBatch(getTypedQuery(), begin, loadBatchSize * 2);
				needReload = false;
			} else if (first + pageSize == end && end < getRowCount()) {
				objects.subList(0, loadBatchSize).clear();
				begin += loadBatchSize;
				objects.addAll(nextBatch(getTypedQuery(), begin + loadBatchSize, loadBatchSize));
			} else if (first == begin && begin > 0) {
				objects.subList(loadBatchSize, objects.size()).clear();
				begin = begin < loadBatchSize ? 0 : begin - loadBatchSize;
				objects.addAll(0, nextBatch(getTypedQuery(), begin, loadBatchSize));
			}
			end = begin + loadBatchSize * 2;

			setRowCount(rows());
		}

		int localFirst = first - begin;
		int localEnd = Math.min(pageSize + localFirst, objects.size());

		return localFirst < localEnd ? new ArrayList<>(objects.subList(localFirst, localEnd))
				: (localFirst == localEnd && !objects.isEmpty() ? objects : Collections.emptyList());
	}

	@Override
	public C getRowData(String rowKey) {
		return objects.stream().filter(listDto -> listDto.toString().equals(rowKey)).findFirst().orElse(null);
	}

	@Override
	public String getRowKey(C object) {
		return object.toString();
	}

	protected List<C> nextBatch(TypedQuery<I> query, int first, int loadSize) {
		return translate(query.setFirstResult(first).setMaxResults(loadSize).getResultList());
	}

	protected void addPath(E key, T value) {
		pathMap.put(key, value);
	}

	protected T getPathValue(String sortField) {
		return pathMap.get(E.valueOf(getSortableEnum(), sortField));
	}

	protected abstract void sortData(List<SortMeta> multiSortMeta);

	protected abstract void prepare();

	protected abstract int rows();

	protected abstract TypedQuery<I> getTypedQuery();

	protected abstract Class<E> getSortableEnum();

	protected abstract DefaultDtoTranslator<C, I> getDtoTranslator();

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private List<C> translate(List<I> batch) {
		return batch.stream().map(getDtoTranslator()::translate).collect(Collectors.toList());
	}

	private boolean sortModeEquals(List<SortMeta> currentSortMeta, List<SortMeta> multiSortMeta) {
		if (currentSortMeta == multiSortMeta) {
			return true;
		}
		if (currentSortMeta == null || multiSortMeta == null) {
			return false;
		}

		Iterator<SortMeta> currentIter = currentSortMeta.iterator();
		Iterator<SortMeta> otherIter = multiSortMeta.iterator();

		while (currentIter.hasNext() && otherIter.hasNext()) {
			SortMeta currentValue = currentIter.next();
			SortMeta otherValue = otherIter.next();
			if (!(currentValue.getSortField().equals(otherValue.getSortField())
					&& currentValue.getSortOrder().equals(otherValue.getSortOrder()))) {
				return false;
			}
		}

		return !(currentIter.hasNext() || otherIter.hasNext());
	}
}