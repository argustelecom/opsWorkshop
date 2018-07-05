package ru.argustelecom.box.env;

import static ru.argustelecom.box.env.util.QueryWrapper.Predicate;

import java.io.Serializable;
import java.util.List;

import ru.argustelecom.box.env.util.QueryWrapper;
import ru.argustelecom.system.inf.modelbase.Identifiable;

public interface JPQLConvertibleDtoFilterModel<I extends Identifiable, T extends QueryWrapper<I>> extends Serializable {
	void buildPredicates(QueryWrapper<I> queryWrapper);

	List<Predicate> predicates(boolean isNew);

	default void addPredicate(Predicate predicate) {
		if (predicate != null)
			predicates(false).add(predicate);
	}

	default void applyPredicates(QueryWrapper<I> queryWrapper) {
		predicates(false).forEach(queryWrapper::and);
	}

	T getQueryWrapper(boolean isNew);
}
