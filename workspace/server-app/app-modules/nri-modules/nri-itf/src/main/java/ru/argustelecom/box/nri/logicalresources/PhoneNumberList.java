package ru.argustelecom.box.nri.logicalresources;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber.PhoneNumberQuery;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool_;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationInstance_;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification_;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber_;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Ленивый список телефонных номеров
 *
 * @author d.khekk
 * @since 23.11.2017
 */
public class PhoneNumberList extends EQConvertibleDtoLazyDataModel<PhoneNumber, PhoneNumberDtoTmp, PhoneNumberQuery, PhoneNumberList.PhoneNumberSort> {

	private static final long serialVersionUID = 6197587362636132921L;

	/**
	 * Транслятор дто
	 */
	@Inject
	private PhoneNumberDtoTranslatorTmp translator;

	/**
	 * Фильтры
	 */
	@Inject
	private PhoneNumberListFilterModel filterModel;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	@PostConstruct
	private void postConstruct() {
		// связываем сортировочный enum с полями ресурса
		addPath(PhoneNumberSort.NAME, query -> query.root().get(PhoneNumber_.name));
		addPath(PhoneNumberSort.POOL, query -> query.root().get(PhoneNumber_.pool).get(PhoneNumberPool_.name));
		addPath(PhoneNumberSort.STATE, query -> query.root().get(PhoneNumber_.state));
		addPath(PhoneNumberSort.SPECIFICATION, query -> query.root()
				.get(PhoneNumber_.specInstance)
				.get(PhoneNumberSpecificationInstance_.type)
				.get(PhoneNumberSpecification_.name));
	}

	@Override
	protected TypedQuery<PhoneNumber> getTypedQuery() {
		em.clear();
		return super.getTypedQuery();
	}

	@Override
	protected EQConvertibleDtoFilterModel<PhoneNumberQuery> getFilterModel() {
		return filterModel;
	}

	@Override
	protected Class<PhoneNumberSort> getSortableEnum() {
		return PhoneNumberSort.class;
	}

	@Override
	protected DefaultDtoTranslator<PhoneNumberDtoTmp, PhoneNumber> getDtoTranslator() {
		return translator;
	}

	/**
	 * Enum для сортировок по колонкам
	 */
	public enum PhoneNumberSort {
		NAME, POOL, STATE, SPECIFICATION
	}
}
