package ru.argustelecom.box.env;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import ru.argustelecom.box.env.dto.IdentifiableDto;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.modelbase.Identifiable;

public abstract class BaseEQConvertibleDtoFilterModel<EQ extends EntityQuery>
		implements EQConvertibleDtoFilterModel<EQ> {
	private List<Predicate> predicates = new ArrayList<>();
	private EQ entityQuery;

	@Override
	public List<Predicate> predicates(boolean isNew) {
		if (isNew) {
			return predicates = new ArrayList<>();
		}
		return predicates;
	}

	@Override
	public EQ getEntityQuery(boolean isNew) {
		return isNew ? entityQuery = entityQuerySupplier().get() : entityQuery;
	}

	protected Identifiable getIdentifiable(Object identifiableDto) {
		checkArgument(identifiableDto instanceof IdentifiableDto);
		return ((IdentifiableDto) identifiableDto).getIdentifiable();
	}

	private static final long serialVersionUID = -31535372687894677L;

}
