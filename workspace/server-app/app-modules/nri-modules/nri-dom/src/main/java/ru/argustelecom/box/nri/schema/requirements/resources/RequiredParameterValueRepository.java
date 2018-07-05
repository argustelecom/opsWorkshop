package ru.argustelecom.box.nri.schema.requirements.resources;

import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.resources.ParameterSpecificationRepository;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredParameterValue;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;


/**
 * Репозиторий доступа к хранилищу требуемых параметров
 *
 * @author b.bazarov
 * @since 09.10.2017
 */
@Repository
public class RequiredParameterValueRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * репозиторий для работы с требованиями
	 */
	@Inject
	private RequiredItemRepository requiredItemRepository;

	/**
	 * репозиторий спецификации параметров
	 */
	@Inject
	private ParameterSpecificationRepository parameterSpecificationRepository;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Поиск по идентификатору
	 *
	 * @param id идентификатор
	 * @return значение
	 */
	public RequiredParameterValue findById(Long id) {
		return em.find(RequiredParameterValue.class, id);
	}

	/**
	 * Создать новое требование к значению параметра
	 *
	 * @param value требование к значению параметра
	 * @param id    ид требование к ресурсу
	 * @return созданный ресурс
	 */
	public RequiredParameterValue create(RequiredParameterValue value, Long id) {
		RequiredItem item = requiredItemRepository.findById(id);
		em.persist(value);
		item.addParameter(value);
		em.merge(item);
		return value;
	}

	/**
	 * Сохранить изменения в значении
	 *
	 * @param value значение
	 */
	public void save(RequiredParameterValue value) {
		em.merge(value);
	}

	/**
	 * Удалить значение
	 *
	 * @param id иденитификатор значения
	 */
	public void delete(Long id) {
		RequiredParameterValue value = findById(id);
		if (value != null) {
			RequiredItem item = value.getRequiredItem();
			if (item != null) {
				item.removeParameter(value);
				em.merge(item);
			}
			value.setRequiredItem(null);
			value.setParameterSpecification(null);
			em.remove(value);
		}
	}
}
