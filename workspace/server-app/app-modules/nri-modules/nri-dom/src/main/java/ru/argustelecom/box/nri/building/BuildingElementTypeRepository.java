package ru.argustelecom.box.nri.building;

import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.building.model.BuildingElementType;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Репозиторий доступа к хранилищу типов элементов строений
 * Created by s.kolyada on 23.08.2017.
 */
@Repository
public class BuildingElementTypeRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequenceService;

	/**
	 * Получить все типы элементов строений
	 *
	 * @return список со всеми типами
	 */
	public List<BuildingElementType> findAll() {
		return new BuildingElementType.BuildingElementTypeQuery().createTypedQuery(em).getResultList();
	}

	/**
	 * Создать новый тип элемента строения
	 *
	 * @param name  Имя типа
	 * @param level Уровень из модели адресов
	 * @param icon  Иконка
	 * @return Созданный тип элемента строений
	 */
	public BuildingElementType create(String name, LocationLevel level, BuildingElementTypeIcon icon) {
		BuildingElementType buildingElementType = BuildingElementType.builder().id(idSequenceService.nextValue(BuildingElementType.class))
				.name(name)
				.locationLevel(level)
				.icon(icon)
				.build();
		em.persist(buildingElementType);
		return buildingElementType;
	}

	/**
	 * Обновить тип элемента строения
	 *
	 * @param id   id типа элемента
	 * @param name Имя типа элемента
	 * @param icon иконка
	 * @return Обновленный тип элемента строения
	 */
	public BuildingElementType update(Long id, String name, BuildingElementTypeIcon icon) {
		BuildingElementType buildingElementType = findOne(id);
		buildingElementType.setName(name);
		buildingElementType.setIcon(icon);
		return em.merge(buildingElementType);
	}

	/**
	 * Найти тип элемента строения
	 *
	 * @param elementTypeId айди типа элемента
	 * @return найденный тип
	 */
	public BuildingElementType findOne(Long elementTypeId) {
		return em.find(BuildingElementType.class, elementTypeId);
	}

	/**
	 * Удалить тип элемента строения
	 *
	 * @param elementTypeId айди типа элемента
	 */
	public void remove(Long elementTypeId) {
		BuildingElementType elementType = findOne(elementTypeId);
		em.remove(elementType);
	}

	/**
	 * Найти все типы данного уровня
	 *
	 * @param level уровень
	 * @return список типов
	 */
	public List<BuildingElementType> findAllByLevel(LocationLevel level) {
		return em.createQuery("FROM BuildingElementType b WHERE b.locationLevel = :level", BuildingElementType.class)
				.setParameter("level", level).getResultList();
	}
}
