package ru.argustelecom.box.nri.building;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.building.model.BuildingElementType;
import ru.argustelecom.box.nri.building.model.BuildingElement_;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Репозиторий доступа к хранилищу элементов строений
 * Created by s.kolyada on 23.08.2017.
 */
@Repository
public class BuildingElementRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Сервис генерации айдишников
	 */
	@Inject
	private IdSequenceService idSequenceService;

	/**
	 * Получить строение расположенное по адресу
	 *
	 * @param location расположение
	 * @return элемент строения
	 */
	public BuildingElement findElementByLocation(Location location) {
		try {
			BuildingElement.BuildingElementQuery query = new BuildingElement.BuildingElementQuery();

			//@formatter:off
			query.and(
					query.location().equal(location)
			);
			//@formatter:on

			return query.createTypedQuery(em).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/**
	 * Создать новый элемент
	 *
	 * @param name        имя нового элемента
	 * @param elementType тип элемента
	 * @param location    расположение
	 * @param parent      родительский элемент
	 * @return новый созданный элемент
	 */
	public BuildingElement create(String name, BuildingElementType elementType, Location location, BuildingElement parent) {
		BuildingElement element = BuildingElement.builder()
				.id(idSequenceService.nextValue(BuildingElement.class))
				.name(name)
				.type(elementType)
				.parent(parent)
				.location(location)
				.children(new ArrayList<>())
				.build();
		if (parent != null) {
			parent.addChild(element);
			em.merge(parent);
		} else {
			em.persist(element);
		}
		return element;
	}

	/**
	 * Найти элемент по айди
	 *
	 * @param id id искомого элемента
	 * @return найденный элемент
	 */
	public BuildingElement findElementById(Long id) {
		return em.find(BuildingElement.class, id);
	}

	/**
	 * Удалить элемент
	 *
	 * @param id айди удаляемого элемента
	 */
	public void delete(Long id) {
		BuildingElement element = findElementById(id);
		BuildingElement parent = element.getParent();
		if (parent != null) {
			parent.removeChild(element);
		}
		em.remove(element);
	}

	/**
	 * Обновить имя и тип элемента
	 *
	 * @param id   id изменяемого элемента
	 * @param name новое имя элемента
	 * @param type новый тип элемента
	 * @return обновленный элемент
	 */
	public BuildingElement updateNameAndType(Long id, String name, BuildingElementType type) {
		BuildingElement element = findElementById(id);
		element.setName(name);
		element.setType(type);
		return em.merge(element);
	}

	/**
	 * Обновить расположение
	 *
	 * @param id       id изменяемого элемента
	 * @param location новое расположение
	 * @return обновленный элемент
	 */
	public BuildingElement updateLocation(Long id, Location location) {
		BuildingElement element = findElementById(id);
		element.setLocation(location);
		return em.merge(element);
	}

	/**
	 * Сменить родительский элемент
	 *
	 * @param elementId id изменяемого элемента
	 * @param parentId  айди нового родителя
	 */
	public void changeParent(Long elementId, Long parentId) {
		BuildingElement element = findElementById(elementId);
		BuildingElement oldParent = element.getParent();
		if (oldParent != null)
			oldParent.removeChild(element);

		BuildingElement parent = findElementById(parentId);
		element.setParent(parent);
	}

	/**
	 * Получает список элементов строений по части адреса
	 *
	 * @param locations адреса для поиска
	 * @return список строений
	 */
	public List<BuildingElement> findAllByLocation(List<Location> locations) {
		if (locations.isEmpty()) {
			return Collections.emptyList();
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BuildingElement> query = cb.createQuery(BuildingElement.class);
		Root<BuildingElement> root = query.from(BuildingElement.class);
		query.where(root.get(BuildingElement_.location).in(locations));
		return em.createQuery(query).getResultList();
	}

	/**
	 * Найти все элементы данного типа
	 *
	 * @param type тип для поиска
	 * @return список элементов строения данного типа
	 */
	public List<BuildingElement> findAllByElementType(BuildingElementType type) {
		return em.createQuery("FROM BuildingElement b WHERE b.type = :type", BuildingElement.class)
				.setParameter("type", type).getResultList();
	}

	/**
	 * Изменить тип элемента строения
	 *
	 * @param element элемент строения
	 * @param to      новый тип
	 */
	public void changeType(BuildingElement element, BuildingElementType to) {
		element.setType(to);
		em.merge(element);
	}
}
