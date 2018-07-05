package ru.argustelecom.box.nri.inf.dataaccess.entityquery;

import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import javax.persistence.metamodel.SingularAttribute;

/**
 * Расширение запроса сущности
 * Created by s.kolyada on 08.02.2018.
 */
public class ExtendedEntityQuery<T extends Identifiable> extends EntityQuery<T> {

	public ExtendedEntityQuery(Class<T> entityClass) {
		super(entityClass);
	}

	/**
	 * Создать расширеный текстовый фильтр
	 * @param attribModel модель
	 * @return фильттр
	 */
	protected ExtendedEntityQueryStringFilter<T> createExtendedStringFilter(SingularAttribute<? super T, String> attribModel) {
		return new ExtendedEntityQueryStringFilter<>(this, attribModel);
	}
}
