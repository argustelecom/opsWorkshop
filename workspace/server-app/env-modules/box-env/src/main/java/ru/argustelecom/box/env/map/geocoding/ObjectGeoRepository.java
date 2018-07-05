package ru.argustelecom.box.env.map.geocoding;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import ru.argustelecom.box.env.map.geocoding.model.ObjectGeo;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.hibernate.type.AOraArrayUserType;
import ru.argustelecom.system.inf.map.crs.CrsUtils;
import ru.argustelecom.system.inf.modelbase.SuperClass;
import ru.argustelecom.system.inf.utils.CheckUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Предназначен для получения геоданных независимо от типа сущности. Предоставляет базовые операции получения геоданных,
 * применимые ко всем сущностям. Не занимается специфичными сущностям операциями. Они реализуются в имплементаторах
 * SpecializedObjectGeoRepository.
 *
 * Для выполнения базовых операций опрашивает имплементации SpecializedObjectGeoRepository предоставляя им возможность
 * выполнить операцию. Если никто не выполнил, выполняет сам, дефолтным образом.
 */
@Repository
public class ObjectGeoRepository implements Serializable {

	private static final long serialVersionUID = 4956460714740882556L;

	/**
	 * Радиус поиска по-умолчнию, в метрах = 1000
	 */
	public static final Double DEFAULT_SEARCH_RANGE = 1000D; // в метрах

	@PersistenceContext
	private EntityManager em;

	@Inject
	@Any
	private Instance<SpecializedObjectGeoRepository> objectGeoRepositories;

	/**
	 * Возвращает специфический для объекта репозиторий.
	 * 
	 * @param object
	 *            объект, для которого необходимо подобрать репозиторий
	 * @return
	 */
	private SpecializedObjectGeoRepository instanceFor(SuperClass object) {
		if (!objectGeoRepositories.isUnsatisfied()) {
			for (SpecializedObjectGeoRepository next : objectGeoRepositories) {
				if (next.objectSupported(object)) {
					return next;
				}
			}
		}

		return null;
	}

	/**
	 * Общая точка входа для поиска записей объекта на карте.
	 *
	 * @param object
	 *            объект, фигуру которого вернуть
	 * @param mapId
	 *            карта мира (MapArea) для отображения на которой вернуть фигуру. Указывай BaseLayer.mapId. Если не
	 * @return
	 */
	public ObjectGeo findByObject(SuperClass object, Long mapId) {
		// сначала ведем поиск картографических данных объекта общим случаем (точная геометрия)
		checkArgument(CheckUtils.isValidSuperClass(object));
		checkArgument(mapId != null);

		// cпециализированный репозиторий может вернуть геоинформацию на основании геоинформации о другом объекте.
		SpecializedObjectGeoRepository specializedObjectGeoRepository = instanceFor(object);
		if (specializedObjectGeoRepository != null) {
			return specializedObjectGeoRepository.findByObject(object, mapId);
		} else {
			return em.find(ObjectGeo.class, mapId);
		}
	}

	/**
	 * Возвращает ближайшую фигуру к указанной точке {@code center} на карте {@code mapId} в пределах {@code range}
	 *
	 * @param mapId
	 *            Идентификатор карты мира, в рамках которой осуществляется поиск. Не может быть {@code null}
	 * @param center
	 *            Геометрия, определяющая точку отсчета. Для полигонов, скорее всего расстояние будет оцениваться не от
	 *            центроида, а от границы полгиона. Не может быть {@code null}
	 * @param range
	 *            Максимальное расстояние от точки отсчета в метрах. Ограничение для первичного фильтра по окну с
	 *            помощью spatial index. Если {@code null}, то применяется
	 *            {@link ObjectGeoRepository#DEFAULT_SEARCH_RANGE} = 1000 м
	 * @param entityIds
	 *            Фильтр по сущности искомых фигур. Сравнение происходит без учёта наследования, {@code null} означает
	 *            "любые"
	 * @return Ближайшую к {@code center} из фигур на заданной карте в пределах заданного радиуса с учётом сущности;<br>
	 *         {@code null}, если в пределах радиуса поиска не нашлось ни одной фигуры;
	 */
	public ObjectGeo findNearest(Long mapId, G2D center, Double range, Long... entityIds) {
		List<ObjectGeo> result = findAllNearest(mapId, center, range, entityIds);
		return result.isEmpty() ? null : result.get(0);
	}

	public List<ObjectGeo> findAllNearest(Long mapId, G2D center, Double range, Long... entityIds) {
		checkNotNull(mapId);
		checkNotNull(center);
		Point<G2D> point = new Point<>(center, CrsUtils.WGS84_CRS);

		TypedQuery<ObjectGeo> q;
		if (entityIds == null) {
			q = em.createNamedQuery(ObjectGeo.FIND_NEAREST_ANY_ENT_QUERY, ObjectGeo.class);
		} else {
			q = em.createNamedQuery(ObjectGeo.FIND_NEAREST_QUERY, ObjectGeo.class);
			q.setParameter("entityIds", new AOraArrayUserType(entityIds));
		}
		q.setParameter("mapId", mapId);
		q.setParameter("center", point);
		q.setParameter("range", range != null ? range : DEFAULT_SEARCH_RANGE);

		return q.getResultList();
	}

	public BusinessObject findNearestEnity(Long mapId, G2D position, Set<Class> classes) {
		for (SpecializedObjectGeoRepository sor : objectGeoRepositories) {
			if (!sor.supportsAnyClass(classes)) {
				continue;
			}
			return sor.findNearest(mapId, position);
		}
		return null;
	}
}
