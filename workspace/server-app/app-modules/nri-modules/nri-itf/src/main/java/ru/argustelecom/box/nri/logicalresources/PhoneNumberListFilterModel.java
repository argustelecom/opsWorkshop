package ru.argustelecom.box.nri.logicalresources;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.function.Supplier;

/**
 * Создает запрос и строит предикаты для получения списка телефонных номеров
 * @author d.khekk
 * @since 23.11.2017
 */
@PresentationModel
public class PhoneNumberListFilterModel extends BaseEQConvertibleDtoFilterModel<PhoneNumber.PhoneNumberQuery> {

	private static final long serialVersionUID = -3476855218729538611L;

	/**
	 * Состояние вьюхи
	 */
	@Inject
	private PhoneNumbersViewState viewState;

	/**
	 * Запрос
	 */
	private PhoneNumber.PhoneNumberQuery phoneNumberQuery = new PhoneNumber.PhoneNumberQuery();

	@Override
	public void buildPredicates(PhoneNumber.PhoneNumberQuery query) {
		query.criteriaQuery().where(query.state().notEqual(PhoneNumberState.DELETED));

		Predicate pDigits = query.digits().likeIgnoreCase(nullsafeLike(viewState.getDigits()));
		Predicate pPool = query.pool().equal(viewState.getPool());
		Predicate pState = query.state().equal(viewState.getState());
		Predicate pSpecification = query.specification().equal(viewState.getSpecification());

		addPredicate(pDigits);
		addPredicate(pState);
		addPredicate(pPool);
		addPredicate(pSpecification);
	}

	@Override
	public PhoneNumber.PhoneNumberQuery getEntityQuery(boolean isNew) {
		if (isNew) {
			phoneNumberQuery = new PhoneNumber.PhoneNumberQuery();
			return phoneNumberQuery;
		} else return phoneNumberQuery;
	}

	@Override
	public Supplier<PhoneNumber.PhoneNumberQuery> entityQuerySupplier() {
		return PhoneNumber.PhoneNumberQuery::new;
	}

	/**
	 * null-safe like
	 *
	 * @param value значение
	 * @return значение. подходящее для запроса like, либо null если на входе ничего не было
	 */
	private String nullsafeLike(String value) {
		return value == null ? null : "%" + value + "%";
	}
}
