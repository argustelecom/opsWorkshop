package ru.argustelecom.box.env.map.geocoding.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * MapArea - подложка. 
 * Подложка отображает местность на карте. Поверх подложки отображаются, "функциональные" слои
 * карты. Всё множество MapArea представляет собой конфигурацию, определяющую доступные пользователю подложки.
 * Пользователь редактирует как справочник "Картографическая подложка" с целью подключения новых закупленных подложек.
 * 
 * MapArea иерархичны. 
 * Корневая MapArea обозначает карту мира. Карта мира это сформированное кем-то описание планеты.
 * Например, osm, google и др предоставляют карту мира. Карта мира может быть составлена из нескольких карт,
 * представленных дочерними MapArea, описывающими отдельные регионы. Например, закупленные заказчиком подложки могут
 * формировать карту мира "закупленные карты". Таким образом, каждое дерево MapArea - это карта мира (Map). Иерархия
 * образуется атрибутом MapAreaParent. Корень иерархии (карта мира) указан атрибутом Map.
 * 
 * Тонкий клиент отображает карту мира. 
 * Т.е. отображает карту мира, позволяет пользователю свободную навигацию в
 * пределах прав доступа. Дочерние MapArea тонкому клиенту не интересны. Тонкий клиент получает карту мира как растровые
 * тайлы по http с тайл-сервера. См атрибут TailServer. В качестве тайл-сервера могут выступать публичные сервера osm,
 * google и др. Множество закупленных заказчиком векторных подложек может быть преобразовано в единую карту мира и
 * предоставлено тонкому клиенту приватным тайл сервером.
 * 
 * Толстый клиент отображает дочерние MapArea. 
 * Толстый клиент отображает подложку как множество векторных слоёв
 * средствами MapInfo. Толстый клиент не может отображать всю карту мира, потому что это потребует слишком много
 * ресурсов. Толстый клиент не может отображать подложки с ранными системами координат. Поэтому, толстый клиент не
 * отображает всю карту мира сразу, а только выбранную пользователем MapArea. MapArea, являющиеся листьями дерева
 * соответствуют находящимся на клиенте файлам подложки, содержащим векторные слои в формате MapInfo в каталоге
 * указанном в MapAreaName. Толстый клиент отображать их. Родительские MapArea, содержащие дочерние MapArea в одной
 * системе координат являются "группами подложек", которые толстый клиент может открыть и показать вместе.
 * 
 * Каждая MapArea - хранилище фигур. 
 * Поверх подложки на карте отображаются фигуры объектов (ObjectGeo). Каждая фигура
 * имеет координаты и принадлежит определённой подложке (MapArea) на которой должна отображаться по этим координатам.
 * Один и тот же объект может отображаться по разным координатам на разных картах мира, чтобы добиться правильного
 * расположения фигуры относительно подложки. Например, колодец позиционируют на углу здания, а угол здания имеет разные
 * координаты на разных подложках. В пределах одной карты мира (дерева MapArea) объект может иметь не более одной
 * фигуры. Фигура объекта всегда принадлежит наиболее детальному узлу дерева MapArea в который "попадает". "Попадает"
 * означает что обозначенный фигурой объект находится в Region MapArea или его дочернем.
 * 
 * Система координат MapArea. 
 * Тонкий клиент читает и пишет координаты объектов (ObjectGeo) в географической системе
 * координат (долгота, широта) WGS84 (SRID=4326). Сам трансформирует их в спроецированные (EPSG:3857 SRID=3857) для
 * отображения и обратно. Это позволяет избежать сложностей с различными системами координат. Толстый клиент читает и
 * пишет координаты объектов в cartesian системе координат продиктованной файлом подложки. Cartesian система координат
 * может быть план-схемой (local) или проекционной (projection). Проекционная система по система координат позволяет
 * пересчитывать координаты в географическую. Требуется обеспечить "видимость" в тонком клиенте объекта, установленного
 * на карту в тостом клиенте и наоборт. Поэтому должна быть указана проекционная система координат (ProjectionSRID) для
 * MapArea отображаемых толстым к проекционной системе координат. Тогда координаты будут автоматически пересчитываться
 * из проекционной в географическую и обратно. Для подложек, отображаемых толстым в local системе координат
 * ProjectionSRID указывать не надо.
 * 
 * 
 * Охватываемый регион 
 * Для всех MapArea обязательно указан Region. Означает что подложка описывает этот регион и все
 * дочерние.
 */
@Entity
@Table(name = "map_area")
@Access(AccessType.FIELD)
@NamedQuery(name=MapArea.QUERY_MAPS, query="from MapArea where parent is null and baseLayerClassName is not null order by priority desc")
public class MapArea extends ru.argustelecom.system.inf.modelbase.SuperClass {

	private static final long serialVersionUID = -1003333919736336282L;

	public static final String QUERY_MAPS = "MapArea.QueryMaps";

	@Column(name = "keyword")
	private String keyword;

	@Column(name = "web_base_layer_class_name")
	private String baseLayerClassName;

	@JoinColumn(name = "parent_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private MapArea parent;

	@Column(name = "priority")
	private long priority;

	public MapArea() {
		super(null, 1L);
	}
	
	public MapArea( Long id, String objectName ){
		super(id, 1L);
		this.objectName = objectName;
	}

	
	@Id
	@Override
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return id;
	}

	/**
	 * Произвольное пояснение для отображения пользователю. Чтобы понять что это за подложка.
	 */
	@Column(name = "desc")
	@Access(AccessType.PROPERTY)
	@Override
	public String getObjectName() {
		return objectName;
	};

	@Override
	public void setObjectName(String value) {
		this.objectName = value;
	};

	
	/**Разработчико-читаемый строковой идентификатор подложки.*/
	public String getKeyword() {
		return keyword;
	}

	/**
  	Родительская подложка.
	Данный MapArea является подложкой, охватывающей часть территории родительского MapArea.
	Например:
	* Существует корневой MapArea, обозначающий карту мира от osm и его дочерние MapArea, обозначающие части территории, выделенные для отображения в толстом  клиенте в виде векторных слоёв.
	* Существует корневой MapArea, обозначающий группу одновременно открываемых подложек толстого клиента и его дочерние подложки.

	Region данного MapArea является дочерним от Region родительского MapArea.
	 */
	protected MapArea getParent(){
		return parent;
	}

	
	public String getBaseLayerClassName(){
		return baseLayerClassName;
	}
	
	
	/**
		Приоритет выбора подложки.
		Если на клиенте при отображении чего-то на карте подходит несколько подложек, то наиболее приоритетная подложка должна быть выбрана по-умолчанию, а остальные предоставлены для возможного выбора в порядке приоритета.
		Приоритет требуется чтобы стараться выбирать одну подложку, потому что качественное отображение фигур относительно подложки не может быть обеспечено для всех подложек из за их различий. 
		
		Большее значение - выше приоритет.
		
		Подложка с наибольшим приоритетом отображается по-умолчанию  при отображении карты (но пользователь может переключиться).
		Поэтому считается что она содержит наиболее "хорошие" координаты объектов.
	 */
	public long getPriority(){
		return priority;
	}
	

};
