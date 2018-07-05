package ru.argustelecom.box.nri.coverage;

import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.env.address.map.model.LocationGeo;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.building.model.BuildingElement;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Iterables.isEmpty;
import static java.util.Collections.emptyList;

/**
 * Репозиторий доступа к хранилищу точек монтирования
 *
 * @author d.khekk, s.kolyada
 * @since 31.08.2017
 */
@Repository
public class ResourceInstallationRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	static final String FIND_ALL_COVERING_ELEMENT = "ResourceInstallationRepository.findAllCoveringElement";

	private static final String INSTALLATION_COVERING_BUILDING_ELEMENT_QUERY
			= "ResourceInstallationRepository.findInstallationsCoveringBuildingElement";

	private static final String INSTALLATION_BY_RESOURCE_ID_VIA_PARENT_ROOT_QUERY
			= "ResourceInstallationRepository.findByResource";

	private static final String BUILDINGS_LOCATIONS_WITH_INSTALLATIONS_QUERY
			= "ResourceInstallationRepository.findLocationsOfInstallations";

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Поиск по идентификатьору
	 *
	 * @param id идентификатор
	 * @return утановка ресурса
	 */
	public ResourceInstallation findById(Long id) {
		return em.find(ResourceInstallation.class, id);
	}

	/**
	 * Найти все точки монтирования, принадлежащие элементу строения
	 *
	 * @param buildingElement элемент строения
	 * @return список точек монтирования
	 */
	public List<ResourceInstallation> findAllByBuildingElement(BuildingElement buildingElement) {
		try {
			ResourceInstallation.ResourceInstallationQuery query = new ResourceInstallation.ResourceInstallationQuery();
			query.and(query.installedAt().equal(buildingElement));
			return query.createTypedQuery(em).getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
	}

	/**
	 * Найти все точки монтирования принадлежащие элементу строения и всем его дочерним элементам
	 *
	 * @param buildingElement элемент строения
	 * @return точки монтирования
	 */
	public List<ResourceInstallation> findAllInstallationsByBuilding(BuildingElement buildingElement) {
		try {
			ResourceInstallation.ResourceInstallationQuery query = new ResourceInstallation.ResourceInstallationQuery();
			query.and(query.installationRoot().equal(buildingElement));
			return query.createTypedQuery(em).getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
	}

	/**
	 * Найти все точки монтирования, в зону охвата которых входит элемент строения
	 *
	 * @param buildingElement элемент строения
	 * @return список точек монтирования
	 */
	@NamedQuery(name = FIND_ALL_COVERING_ELEMENT,
			query = "FROM ResourceInstallation r WHERE :building_element MEMBER OF r.cover")
	public List<ResourceInstallation> findAllCover(BuildingElement buildingElement) {
		return em.createNamedQuery(FIND_ALL_COVERING_ELEMENT, ResourceInstallation.class)
				.setParameter("building_element", buildingElement)
				.getResultList();
	}

	/**
	 * Найти все точки монтирования покрывающие элемент строения
	 *
	 * @param location расположение
	 * @return список точек монтирования
	 */
	@NamedNativeQuery(name = INSTALLATION_COVERING_BUILDING_ELEMENT_QUERY,
			query = "SELECT inst.id FROM nri.resource_installation_coverage cov " +
					"  LEFT JOIN nri.resource_installation inst ON inst.id = cov.installation_id " +
					"WHERE cov.building_element_id IN " +
					"      (WITH RECURSIVE r AS ( " +
					"        SELECT be.id, be.parent_element_id FROM nri.building_element AS be WHERE be.location_id = :locationId " +
					"        UNION ALL " +
					"        SELECT be.id, be.parent_element_id FROM nri.building_element AS be JOIN r ON be.id = r.parent_element_id " +
					"      ) " +
					"      SELECT r.id FROM r)")
	public List<ResourceInstallation> findInstallationsCoveringBuildingElement(Location location) {
		List<BigInteger> installationIdList = em.createNamedQuery(INSTALLATION_COVERING_BUILDING_ELEMENT_QUERY)
				.setParameter("locationId", location.getId()).getResultList();

		return EntityManagerUtils.findList(em, ResourceInstallation.class,
				installationIdList.stream().map(BigInteger::longValue).collect(Collectors.toList()));
	}

	/**
	 * Обновить точку монтирования
	 *
	 * @param installationId        идентификатор инсталляции
	 * @param newInstalledAtElement элемент структуры дома куда смонтирован ресурс
	 * @return обновлённую инсталляцию
	 */
	public ResourceInstallation setInstalledAt(Long installationId, BuildingElement newInstalledAtElement) {
		ResourceInstallation installation = findById(installationId);
		installation.setInstalledAt(newInstalledAtElement);
		em.persist(installation);
		return installation;
	}

	/**
	 * Обновить комментарий установки
	 *
	 * @param installationId идентификатор утсановки
	 * @param comment        комментарий
	 * @return обновлённая установка
	 */
	public ResourceInstallation updateComment(Long installationId, String comment) {
		ResourceInstallation installation = findById(installationId);
		installation.setComment(comment);
		em.persist(installation);
		return installation;
	}

	/**
	 * Обновить список покрытых элементов
	 *
	 * @param installationId    id инсталляции
	 * @param coveredElementIds id новых элементов
	 * @return обновленную инсталляцию
	 */
	public ResourceInstallation updateCoveredElements(Long installationId, List<Long> coveredElementIds) {
		List<BuildingElement> buildingElements;

		if (isEmpty(coveredElementIds)) {
			buildingElements = emptyList();
		} else {
			buildingElements = em
					.createQuery("SELECT e FROM BuildingElement e WHERE e.id IN :ids", BuildingElement.class)
					.setParameter("ids", coveredElementIds).getResultList();
		}
		ResourceInstallation installation = em.find(ResourceInstallation.class, installationId);
		installation.setCover(buildingElements);
		em.persist(installation);
		return installation;
	}

	/**
	 * Ищет точки монтирования по id
	 *
	 * @param ids ids
	 * @return точки монтирования
	 */
	public List<ResourceInstallation> findByInstalledAtIdIn(List<Long> ids) {
		if (isEmpty(ids))
			return emptyList();
		return em.createQuery("SELECT inst FROM ResourceInstallation inst WHERE inst.installedAt.id IN :ids",
				ResourceInstallation.class)
				.setParameter("ids", ids).getResultList();
	}

	/**
	 * Найти установку по ресурсу
	 * Ищем по рутовому родительскому элементу ресурса - только у него может быть установка
	 * Т.к. установка и ресурс имеют одинаковые ключи, то берём ключ ресурса, как ключ инсталляции
	 *
	 * @param id идентификатор ресурса
	 * @return установка
	 */
	@NamedNativeQuery(name = INSTALLATION_BY_RESOURCE_ID_VIA_PARENT_ROOT_QUERY,
			query = "WITH RECURSIVE r AS (\n" +
					"    SELECT ri.id, ri.parent_res_id FROM nri.resource_instance AS ri WHERE ri.id = :resourceId\n" +
					"    \n" +
					"    UNION \n" +
					"    \n" +
					"    SELECT \n" +
					"        ri.id, ri.parent_res_id\n" +
					"    FROM nri.resource_instance AS ri\n" +
					"    JOIN r\n" +
					"    ON ri.id = r.parent_res_id\n" +
					")\n" +
					"SELECT r.id FROM r\n" +
					"WHERE r.parent_res_id IS NULL;")
	public ResourceInstallation findByResource(Long id) {
		BigInteger installationId = (BigInteger) em.createNamedQuery(INSTALLATION_BY_RESOURCE_ID_VIA_PARENT_ROOT_QUERY)
				.setParameter("resourceId", id).getSingleResult();
		return findById(installationId.longValue());
	}

	/**
	 * Создать новую установку
	 *
	 * @param resource        ресурс
	 * @param buildingElement элемент строения для установки
	 * @return созданный ресурс
	 */
	public ResourceInstallation createInstallation(ResourceInstance resource, BuildingElement buildingElement) {
		// при создании установки сразу выставляем ссылку на строение к которому оно относится
		// т.к. мы не поддерживаем кейс переноса точки установки вне пределов одного строения,
		// то можем выставлять эту ссылку всего 1 раз
		BuildingElement rootBuildingElement = buildingElement;
		while (rootBuildingElement.getParent() != null) {
			rootBuildingElement = rootBuildingElement.getParent();
		}
		ResourceInstallation installation = ResourceInstallation.builder()
				.id(resource.getId())
				.installedAt(buildingElement)
				.installationRoot(rootBuildingElement)
				.resource(resource)
				.build();
		em.persist(installation);
		return installation;
	}

	/**
	 * Удалить установку
	 *
	 * @param id иденитификатор установки
	 */
	public void delete(Long id) {
		ResourceInstallation installation = findById(id);
		if (installation != null && installation.getResource() != null) {
			installation.getResource().setInstallation(null);
			em.remove(installation);
		}
	}

	/**
	 * Получить локации строения в которых существуют инсталляции
	 * @param mapId идентификатор карты
	 * @return список локаций строений, в которых есть инсталляции
	 */
	@NamedNativeQuery(name = BUILDINGS_LOCATIONS_WITH_INSTALLATIONS_QUERY,
			query = "SELECT distinct lg.id from nri.resource_installation as inst " +
					"inner JOIN nri.building_element as belem on belem.id = inst.installation_building_element_root_id " +
					"inner join system.object_geo as lg on lg.location_id = belem.location_id;")
	public Collection<LocationGeo> findLocationsOfInstallations(Long mapId) {
		List<BigInteger> installationIdList = em.createNamedQuery(BUILDINGS_LOCATIONS_WITH_INSTALLATIONS_QUERY)
				.getResultList();

		if (CollectionUtils.isEmpty(installationIdList)) {
			return Collections.emptyList();
		}

		return EntityManagerUtils.findList(em, LocationGeo.class,
				installationIdList.stream().map(BigInteger::longValue).collect(Collectors.toList()));
	}
}
