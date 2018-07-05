package ru.argustelecom.box.env.party;

import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.model.role.OwnerParameter;
import ru.argustelecom.box.inf.service.Repository;

/**
 * Репозиторий для работы {@link OwnerParameter}
 */
@Repository
public class OwnerParameterRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService sequence;

	/**
	 * Создает экземпляр доп. параметра для владельца
	 * 
	 * @param owner
	 *            владелец созданного параметра
	 * @param keyword
	 *            ключевое слово
	 * @param name
	 *            название параметра
	 * @param value
	 *            значение параметра
	 * @return возвращает созданный экземпляр параметра
	 */
	public OwnerParameter create(Owner owner, String keyword, String name, String value) {
		checkState(!EMPTY.equals(keyword));
		checkState(!EMPTY.equals(name));

		OwnerParameter parameter = new OwnerParameter(sequence.nextValue(OwnerParameter.class), owner);
		parameter.setKeyword(keyword);
		parameter.setObjectName(name);
		parameter.setValue(value);

		em.persist(parameter);

		return parameter;
	}

	private static final long serialVersionUID = -1150682434790188939L;
}
