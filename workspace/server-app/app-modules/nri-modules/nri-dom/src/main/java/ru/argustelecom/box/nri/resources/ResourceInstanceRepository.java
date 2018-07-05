package ru.argustelecom.box.nri.resources;

import org.geolatte.geom.Envelope;
import org.geolatte.geom.G2D;
import ru.argustelecom.box.env.address.map.LocationGeoRepository;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;
import ru.argustelecom.box.nri.map.network.accessports.ConnectionPointPositionsLoadCriteria;
import ru.argustelecom.box.nri.map.network.accessports.model.AccessPortsPosition;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Iterables.isEmpty;

/**
 * Репозиторий ресурсов
 *
 * @author d.khekk
 * @since 21.09.2017
 */
@Repository
public class ResourceInstanceRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	@Inject
	private LocationGeoRepository locationGeoRepository;

	/**
	 * Найти ресурс
	 *
	 * @param id айди ресурса
	 * @return найденный ресурс
	 */
	public ResourceInstance findOne(Long id) {
		return em.find(ResourceInstance.class, id);
	}

	/**
	 * Создать новый ресурс
	 *
	 * @param resource ресурс для создания
	 * @return созданный ресурс
	 */
	public ResourceInstance create(ResourceInstance resource) {
		em.persist(resource);
		return resource;
	}

	/**
	 * Сохранить изменения в ресурсе
	 *
	 * @param resource ресурс
	 */
	public void save(ResourceInstance resource) {
		em.merge(resource);
	}

	/**
	 * Удалить ресурс
	 *
	 * @param id иденитификатор ресурса
	 */
	public void delete(Long id) {
		ResourceInstance resource = findOne(id);
		ResourceInstance parent = resource.getParent();
		if (parent != null) {
			parent.removeChild(resource);
		}
		em.remove(resource);
	}

	/**
	 * Добавить телефонные номера в ресурс
	 *
	 * @param resId          ресурс
	 * @param phoneNumberIds телефонные номера
	 * @return сохраненный ресурс
	 */
	public ResourceInstance addPhoneNumbers(Long resId, List<Long> phoneNumberIds) {
		ResourceInstance resourceInstance = findOne(resId);
		if (!resourceInstance.canContainLogicalResource(LogicalResourceType.PHONE_NUMBER)) {
			throw new IllegalStateException("Resource can not contain logical resources of type "
					+ LogicalResourceType.PHONE_NUMBER.getName());
		}
		if (!isEmpty(phoneNumberIds)) {
			em.createQuery("update PhoneNumber p set p.resource.id = :res WHERE p.id in :ids")
					.setParameter("res", resId)
					.setParameter("ids", phoneNumberIds)
					.executeUpdate();
		}
		em.clear(); // без clear em не найдет свежедобавленных номеров
		return em.find(ResourceInstance.class, resId);
	}

	/**
	 * Загружает позиции которые надо отображать
	 * @param criteria Настройки
	 * @param mapId идентификатор карты
	 * @param envelope прямоугольник в котором надо искать
	 * @return список статистики по ресурсам
	 */
	public Collection<AccessPortsPosition> loadPositions(ConnectionPointPositionsLoadCriteria criteria, Long mapId, Envelope<G2D> envelope) {

		//TODO впихнуть mapId вообще у нас элементы привязаны к конкретной карте?

		//Запрос вытаскивает ресурсы по спецификации в заданном районе
		//Используется рекурсия от текущего ресурса к предку без родителей т.к. только родитель может иметь инсталляцию

		Query query = em.createNativeQuery("SELECT ST_X(cast(og.location AS geometry)) as lng ,ST_Y(cast(og.location AS geometry)) as lat,l.*,l.free+l.booked+l.loaded as total FROM \n" +
				"(SELECT l.id as building_id,l.name as building_name ,sum(ri.sr) as free,sum(ri.su) as booked,sum(ri.sd)as loaded FROM \n" +
				"(SELECT id as id_r,sum(FREE) as sr,sum(BOOKED) as su,sum(LOADED)as sd FROM (\n" +
				"SELECT DISTINCT *,CASE WHEN ri.state = 'FREE' THEN 1 ELSE 0 END AS FREE,\n" +
				"CASE WHEN ri.state = 'BOOKED' THEN 1 ELSE 0 END AS BOOKED,\n" +
				"CASE WHEN ri.state = 'LOADED' THEN 1 ELSE 0 END AS LOADED\n" +
				" FROM (WITH RECURSIVE r AS ( SELECT ri.*,CASE WHEN loading_id is null AND booking_order_id is NULL THEN 'FREE' \n" +
				"WHEN loading_id is not null THEN 'LOADED' ELSE 'BOOKED' END AS state,ri.id AS base_id FROM nri.resource_instance AS ri WHERE ri.id IN (SELECT ri.id FROM nri.resource_instance ri\n" +
				"WHERE ri.specification_id = :spec )\n" +
				"UNION ALL\n" +
				"SELECT \n" +
				"ri.*,state,r.base_id\n" +
				"FROM nri.resource_instance AS ri\n" +
				"JOIN r\n" +
				"ON ri.id = r.parent_res_id)\n" +
				"SELECT * FROM r\n" +
				"WHERE r.parent_res_id IS NULL) AS ri WHERE ri.state IN (:status)) AS ri1 GROUP BY id)\n" +
				" AS ri,nri.resource_installation rinst,system.location l,nri.building_element be \n" +
				"WHERE\n" +
				"rinst.id=ri.id_r \n" +
				"AND rinst.installation_building_element_root_id = be.id\n" +
				"AND l.id = be.location_id GROUP BY (l.id,l.name)) l, system.object_geo og,system.location_geo lg WHERE l.building_id=lg.location_id\n" +
				"AND og.id=lg.id\n" +
				"AND og.location && ST_MakeEnvelope(:l1, :l2,:l3,:l4,4326)", AccessPortsPosition.BASE_RESULT_MAPPING);



		query.setParameter("spec", criteria.getRs());
		query.setParameter("l1", envelope.lowerLeft().getLon());
		query.setParameter("l2", envelope.lowerLeft().getLat());
		query.setParameter("l3", envelope.upperRight().getLon());
		query.setParameter("l4", envelope.upperRight().getLat());
		query.setParameter("status", criteria.getObjectStates().stream().map((st) -> st.toString()).collect(Collectors.toList()));
		return query.getResultList();
	}
}
