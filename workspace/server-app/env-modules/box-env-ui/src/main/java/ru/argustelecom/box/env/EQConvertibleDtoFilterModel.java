package ru.argustelecom.box.env;

import java.io.Serializable;
import java.util.List;
import java.util.function.Supplier;

import javax.persistence.criteria.Predicate;

import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;

public interface EQConvertibleDtoFilterModel<EQ extends EntityQuery> extends Serializable {

	List<Predicate> predicates(boolean isNew);

	void buildPredicates(EQ query);

	EQ getEntityQuery(boolean isNew);

	Supplier<EQ> entityQuerySupplier();

	default void addPredicate(Predicate predicate) {
		if (predicate != null)
			predicates(false).add(predicate);
	}

	default void applyPredicates(EQ query) {
		predicates(false).forEach(query::and);
	}
}
