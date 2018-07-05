package ru.argustelecom.box.env.map.geocoding;

import static com.google.common.base.Preconditions.checkState;

import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.jpa.QueryHints;
import org.jboss.logging.Logger;

import ru.argustelecom.box.env.map.geocoding.model.MapArea;

/**
 * Предоставляет информацию о имеющихся картах мира ({@link MapArea}).
 */
@ApplicationScoped
public class MapRepository {
	private static final Logger log = Logger.getLogger(MapRepository.class);

	private List<MapArea> maps;
	private MapArea defaultMap;

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@PostConstruct
	protected void postConstruct() {
		Query q = em.createNamedQuery(MapArea.QUERY_MAPS);
		maps = q.setHint(QueryHints.HINT_READONLY, true).getResultList();
		defaultMap = maps.stream().max(Comparator.comparingLong(MapArea::getPriority)).get();

		log.infov("Найдено карт мира для тонкого клиента: {0}", maps.size());
		if (maps.size() == 0) {
			log.warn("В конфигурации (MapArea) нет ни одной карты для тонкого клиента");
		} else {
			checkState(defaultMap != null);
		}

	}

	/**
	 * Возвращает карты мира, которые должны отображаться в тонком клиенте. Возвращает в порядке приоритета выбора.
	 */
	public List<MapArea> getMaps() {
		return maps;
	}

	/**
	 * Возвращает карту по-умолчанию, для использования в функциях вне контекста определённой карты. Когда пользователь
	 * просмативает карту, он может переключать базовые слои. Каждый базовый слой соответствует своей MapArea. В таких
	 * случаях надо использовать карту базового слоя (BaseLayer.mapId), а не карту по-умолчанию (defaultMap).
	 * <p>
	 * В редких случаях функция выполняется не в контексте определённой карты. Например надо в бизнес-логике вычислить
	 * расстояние между двумя зданиями и для этого получить их координаты. Тогда следует запрашивать координаты из карты
	 * по-умолчанию.
	 * <p>
	 * Карта по-умолчанию - это наиболее приоритетная карта {@link MapArea#getPriority()}.
	 * 
	 * @return
	 */
	public MapArea getDefaultMap() {
		return defaultMap;
	}

}
