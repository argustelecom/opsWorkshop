package ru.argustelecom.ops.env.idsequence;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.ops.inf.modelbase.SequenceDefinition;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.SuperClass;

/**
 * Сервис отвечающий за генерацию <b>ID</b>, все сущности при создании должны спрашивать у него <b>ID</b> для своего
 * создания.
 * <p/>
 * В дальнейшем для разных сущностей могут появится отдельные генераторы, поэтому для получения <b>ID</b> в метод
 * передается класс, для котогоро необходимо получить значение из соответствующего ему генератора. Для добавления нового
 * маппинга сущности на её генератор см {@linkplain SequenceDefinition}
 */
@Stateless
public class IdSequenceService implements Serializable {

	private static final long serialVersionUID = -1709303146833067375L;

	private static final String GET_NEXT_VALUE = "select nextval(:sequence_name)";
	private static final String GET_CURRENT_VALUE = "select currval(:sequence_name)";

	@PersistenceContext
	private EntityManager em;

	/**
	 * @param clazz
	 *            класс, для которого нужно получить <b>ID</b>.
	 * @return значение сгенерированного <b>ID</b>.
	 */
	public Long nextValue(Class<? extends SuperClass> clazz) {
		return Long.valueOf(em.createNativeQuery(GET_NEXT_VALUE).setParameter("sequence_name", getSequenceName(clazz))
				.getSingleResult().toString());
	}

	/**
	 * @param clazz
	 *            класс, для которого нужно узнать текущий(последний использованный) <b>ID</b>.
	 * @return значение текущего(последнего использованного) <b>ID</b>.
	 */
	public Long currentValue(Class<? extends SuperClass> clazz) {
		return Long.valueOf(em.createNativeQuery(GET_CURRENT_VALUE)
				.setParameter("sequence_name", getSequenceName(clazz)).getSingleResult().toString());
	}

	/**
	 * @param clazz
	 *            класс, для которого нужно определить генератор.
	 * @return Имя генератора.
	 */
	private String getSequenceName(Class<? extends SuperClass> clazz) {
		if (clazz.isAnnotationPresent(SequenceDefinition.class)) {
			SequenceDefinition sd = clazz.getAnnotation(SequenceDefinition.class);
			return sd.name();
		} else {
			throw new SystemException(String.format("Есть класс %s, у которого нет @SequenceDefinition",
					clazz.getCanonicalName()));
		}
	}

}