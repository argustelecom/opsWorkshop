package ru.argustelecom.box.env;

import static ru.argustelecom.box.env.util.QueryWrapper.Predicate;

import java.util.ArrayList;
import java.util.List;

import ru.argustelecom.box.env.util.QueryWrapper;
import ru.argustelecom.system.inf.modelbase.Identifiable;

public abstract class BaseJPQLConvertibleDtoFilterModel<I extends Identifiable, T extends QueryWrapper<I>>
		implements JPQLConvertibleDtoFilterModel<I, T> {

	private List<Predicate> predicates = new ArrayList<>();

	@Override
	public List<Predicate> predicates(boolean isNew) {
		if (isNew) {
			return predicates = new ArrayList<>();
		}
		return predicates;
	}

	private static final long serialVersionUID = 2947782660804189464L;
}
