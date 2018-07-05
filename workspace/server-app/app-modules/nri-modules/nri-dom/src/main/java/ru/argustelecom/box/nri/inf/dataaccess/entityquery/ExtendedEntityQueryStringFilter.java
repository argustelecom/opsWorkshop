package ru.argustelecom.box.nri.inf.dataaccess.entityquery;

import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;

/**
 * Расширенный фильтр для текста
 * Created by s.kolyada on 08.02.2018.
 */
public class ExtendedEntityQueryStringFilter<E extends Identifiable> extends EntityQueryStringFilter<E> {

	protected ExtendedEntityQueryStringFilter(EntityQuery<E> masterQuery, SingularAttribute<? super E, String> attribModel) {
		super(masterQuery, attribModel);
	}

	protected ExtendedEntityQueryStringFilter(EntityQuery<E> masterQuery, Path<String> attribPath, SingularAttribute<?, String> attribModel) {
		super(masterQuery, attribPath, attribModel);
	}

	/**
	 * Создать предикат для выражения "содержит"
	 * @param value значение
	 * @return предикат
	 */
	public Predicate contains(String value) {
		return !this.isValidParam(value)?null:this.criteriaBuilder().like(this.attribPath(), this.createContainsParameter(value));
	}

	/**
	 * Создать предикат для выражения "содержит" без учёта регистра
	 * @param value значение
	 * @return предикат
	 */
	public Predicate containsIgnoreCase(String value) {
		if(!this.isValidParam(value)) {
			return null;
		} else {
			CriteriaBuilder cb = this.criteriaBuilder();
			return cb.like(cb.upper(this.attribPath()), cb.upper(this.createContainsParameter(value)));
		}
	}

	/**
	 * создать параметр
	 * @param value значение
	 * @return параметр
	 */
	private ParameterExpression<String> createContainsParameter(String value) {
		String wildcard = value.indexOf(37) == -1 && value.indexOf(63) == -1?"%" + value + "%":value;
		return this.masterQuery().createParam(this.attribJavaType(), wildcard);
	}
}
