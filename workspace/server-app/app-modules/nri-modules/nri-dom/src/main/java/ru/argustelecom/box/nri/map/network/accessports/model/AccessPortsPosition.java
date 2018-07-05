package ru.argustelecom.box.nri.map.network.accessports.model;

import lombok.Builder;
import org.geolatte.geom.G2D;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;


import static com.google.common.base.Preconditions.checkArgument;

/**
 * Результат запроса статистики портов доступа по зданию. Счётчики портов в разрезе эксплуатационных статусов замаплены
 * хардкодно, т.к. эти статусы имеют жесткий заранее определённый смысл и получаются из БД одной выборкой.
 *
 * @see <a href="http://support.argustelecom.ru:10609/browse/TASK-47766">TASK-47766</a>
 *
 */
@MappedSuperclass
//@formatter:off
@SqlResultSetMapping(name = AccessPortsPosition.BASE_RESULT_MAPPING, classes =
	@ConstructorResult(targetClass = AccessPortsPosition.class, columns = {
		@ColumnResult(name = "building_id", type = long.class),
		@ColumnResult(name = "building_name", type = String.class),
		@ColumnResult(name = "lng", type = double.class),
		@ColumnResult(name = "lat", type = double.class),
		@ColumnResult(name = "total", type = int.class),
		@ColumnResult(name = "free", type = int.class),
		@ColumnResult(name = "booked", type = int.class),
		@ColumnResult(name = "loaded", type = int.class)}))
//@formatter:on
public class AccessPortsPosition {

	public static final String BASE_RESULT_MAPPING = "AccessPortsPosition.BaseResultMapping";

	private long buildingId;
	private String buildingName;
	private G2D position;
	private Statistic value;

	public static class Statistic {
		private int portsTotal;
		private int free;
		private int booked;
		private int loaded;



		protected Statistic(int portsTotal, int free, int booked, int loaded) {
			checkArgument(portsTotal == booked + free + loaded);
			this.portsTotal = portsTotal;
			this.free = free;
			this.booked = booked;
			this.loaded = loaded;
		}

		/**
		 * Суммарное количество портов, прошедших фильтр.
		 */
		public int getPortsTotal() {
			return portsTotal;
		}

		/**
		 * Число свободных портов из всех, прошедших фильтр. Свободные, значит не занятые услугой/бронью, независимо от
		 * их эксплуатационного статуса.
		 */
		public int getFree() {
			return free;
		}

		/**
		 * Число портов, имеющих эксплуатационный статус "Проект".
		 */
		public int getBooked() {
			return booked;
		}

		/**
		 * Число портов, имеющих эксплуатационный статус "Предварительная готовность".
		 */
		public int getLoaded() {
			return loaded;
		}



	}

	/**
	 * Конструктор со всеми параметрами
	 * @param buildingId идентификационный номер здания
	 * @param buildingName имя здания
	 * @param lng долгота
	 * @param lat широта
	 * @param portsTotal общее количество ресурсов
	 * @param free свободных ресурсов
	 * @param booked забронированных
	 * @param loaded нагруженных
	 */
	@Builder
	public AccessPortsPosition(long buildingId, String buildingName, double lng, double lat, int portsTotal, int free, int booked, int loaded) {

		this.buildingId = buildingId;
		this.buildingName = buildingName;
		this.position = new G2D(lng, lat);
		this.value = new Statistic(portsTotal,free, booked,loaded);
	}

	/**
	 * возвращает ид
	 * @return
	 */
	public long getBuildingId() {
		return buildingId;
	}

	/**
	 * Подпись здания для отображения хинта на карте: улица, №дома.
	 */
	public String getBuildingName() {
		return buildingName;
	}

	/**
	 * Геопозиция для маркера на карте
	 */
	public G2D getPosition() {
		return position;
	}

	/** Значения статистики по данному зданию */
	public Statistic getValue() {
		return value;
	}

}
