package ru.argustelecom.box.env.map.page;

import org.jboss.logging.Logger;
import ru.argustelecom.box.env.map.geocoding.ObjectGeoRepository;
import ru.argustelecom.box.env.map.geocoding.SpecializedObjectGeoRepository;
import ru.argustelecom.box.env.map.page.aspects.CurrentMapAspect;
import ru.argustelecom.box.env.map.page.aspects.CurrentMapAspectSettings;
import ru.argustelecom.box.env.map.page.aspects.MapAspect;
import ru.argustelecom.box.env.map.page.aspects.MapWidget;
import ru.argustelecom.box.env.map.page.detailinfo.header.CurrentDetailInfoModifier;
import ru.argustelecom.box.env.map.page.mapsearchresult.CurrentMapSearchResult;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.map.component.LMapWidget;
import ru.argustelecom.system.inf.map.component.event.ClickEvent;
import ru.argustelecom.system.inf.map.component.event.FeatureEvent;
import ru.argustelecom.system.inf.map.component.event.FeatureSelectedEvent;
import ru.argustelecom.system.inf.map.component.model.MapModel;
import ru.argustelecom.system.inf.map.component.model.MapViewport;
import ru.argustelecom.system.inf.modelbase.SuperClass;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Базовая страница для создания типичных страниц, отображающих карту.
 * Не обязательно наследовать от неё любую страницу с картой. Предназначена только для типичных страниц.
 * 
 * <h1>Типичная страница карты</h1>
 * <p>Имеет структуру: Блок фильтров слева, блок с картой по середине, справа блок детальных сведений выбранного объекта.</p>
 * 
 *  <p>Поддерживает аспекты: Имеет один или несколько аспектов. Пользователь может переключаться между ними, один из них текущий
 *  {@link CurrentMapAspect}. Каждый аспект - какая-то "тема" например "Порты доступа", "Потенциальные клиенты".
 *  Текущий аспект отпределяет состав фильтров, отображаемых на карте данных, состав детальных сведений.
 *  Плюс в том, что пользователь может переключаться между темами сохраняя viewport карты.</p>
 *  
 *  <p>Имеет понятие выбранного на карте объекта {@link CurrentMapObject}. По нему отображает детальные сведения</p>
 */
@PresentationModel
public class MapViewModel extends ViewModel {
	private static final Logger log = Logger.getLogger(MapViewModel.class);
	private static final long serialVersionUID = 1L;

	@Inject
	private MapWidget aspectsMapWidget;

	@Inject
	private MapModel mapModel;

	@Inject
	protected CurrentMapAspect currentAspect;
	@Inject
	private CurrentMapViewport currentViewport;
	@Inject
	protected CurrentMapObject currentObject;
	@Inject
	private ObjectGeoRepository geoRep;

	@Inject
	@Any
	private Instance<SpecializedObjectGeoRepository> repositories;

	@Inject
	private CurrentMapSearchResult currentMapResultViewer;
	@Inject
	private CurrentDetailInfoModifier currentDetailInfoModifier;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		// Преднамерено не длинный UOW
		// Пользователь может выполнять навигацию, переключать фильтры, получая новые данные.
		// Неправильно бездумно накапливать все эти данные.
		//unitOfWork.makePermaLong();

		if (currentViewport.getMapWidget() == null) {
			currentViewport.setMapWidget(getMapWidget());
		}

		// За счёт этого мы создаём currentViewport и поэтому он слышит CurrentMapObject change при открытии страницы
		if (currentViewport.getValue() == null) {
			// Карта не может отобразиться без указания viewport
			currentViewport.setValue(new MapViewport(mapModel.getActiveBaseLayer()));
		}

		//MapAspectsFrameModel больше нет
		//// т.к. короткий UnitOfWork, то нужно провоцировать создание MapAspectsFrameModel, чтобы она могла
		//// реагировать на CDI-события (Reception.ALWAYS не вариант, т.к. другая страница может захотеть отобразить карту
		//// сети с одним аспектом без возможности выбора и MapAspectsFrameModel)
		//currentAspect.getValueAsObject();
		aspectsMapWidget.set(getMapWidget());
	}

	public void onMapClick(ClickEvent event) {
		log.debugv("onMapClick {0}", event);

		MapAspect aspect = currentAspect.getValueAsObject();
		if (aspect == null) {
			currentObject.setValue(null);
			return;
		}
		aspect.checkCurrentMapObject();
		// подбор ближайщего элемента
		BusinessObject nearest = geoRep.findNearestEnity(currentViewport.getValue().getBaseLayer().getMapId(),
				event.getPosition(), currentAspect.getValueAsObject().rendersEntities());
		if (nearest != null) {
			log.debugv("По клику найден ближайший объект {0}", nearest);
			currentObject.setValue(nearest);
		} else {
			log.debug("По клику не найден достаточно близкий объект");
		}
	}

	public void onFeatureSelected(FeatureSelectedEvent event) {
		log.debugv("onFeatureSelected {0}", event);
		SuperClass selectedObject = null;
		if (event.getFeature() != null) {
			selectedObject = new EntityConverter(em).convertToObject(event.getFeature().getId());
		}
		currentObject.setValue(selectedObject);
	}

	public void onFeatureEdited(FeatureEvent event) {
		if (currentAspect.getValueAsObject() != null) {
			currentAspect.getValueAsObject().onFeatureEdited(event.getLayer().getId(), event.getFeature());
		}
		if (currentMapResultViewer.getValue() != null) {
			currentMapResultViewer.getValueAsObject().onFeatureEdited(event.getLayer().getId(), event.getFeature());
		}
	}

	@SuppressWarnings("unused")
	private void mapObjectChanged(
			@Observes(notifyObserver = Reception.IF_EXISTS, during = TransactionPhase.BEFORE_COMPLETION) CurrentMapObject event) {
		currentDetailInfoModifier.showRoot();
		currentMapResultViewer.getValueAsObject().mapObjectChanged();
		if (currentAspect.getValueAsObject() != null) {
			// CurrentMapObject не даем фрейму аспекта, т.к. ему явно понадобится не только в этом методе - пусть сам
			// инжектит
			currentAspect.getValueAsObject().mapObjectChanged();
		}
	}

	@SuppressWarnings("unused")
	private void mapAspectSettingsChanged(
			@Observes(notifyObserver = Reception.IF_EXISTS, during = TransactionPhase.BEFORE_COMPLETION) CurrentMapAspectSettings event) {
		currentMapResultViewer.setValue(null);

		// FIXME из-за совместной реализации аспекта ФЛ ЮЛ ПК
		MapAspect aspect = currentAspect.getValueAsObject();
		aspect.checkCurrentMapObject();
	}

	@SuppressWarnings("unused")
	private void mapAspectChanged(
			@Observes(notifyObserver = Reception.IF_EXISTS, during = TransactionPhase.BEFORE_COMPLETION) CurrentMapAspect event) {
		currentMapResultViewer.setValue(null);

		MapAspect aspect = event.getValueAsObject();
		if (aspect != null) {
			aspect.checkCurrentMapObject();
		} else {
			currentObject.setValue(null);
		}
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	public LMapWidget getMapWidget() {
		return new LMapWidget("mapVar");
	}

	/**
	 * Вызывается при перерендеринге всей карты. 
	 * Должне полностью обновить содержимое карты.
	 */
	public void preRenderMap() {
		// нужно сделать базовые слои grayscale, но где? нет new MapModel - она инжектится
		// в postConstruct может получиться провокация ненужного создания модели (на самом деле MapModel сейчас
		// продюсится Dependent, поэтому инстанциируется сразу)
		// getMapModel может вызываться 500 раз на реквест
		// решение: настраиваем grayscale только перед рендерингом карты, когда это собственно на что-то влияет
		// mapModel.getBaseLayers().forEach(bl -> bl.setGrayscale(true));
		// Upd: передумали, пока что по дефолту слои с цветом, а MapModelFactory добавляет отдельный вариант засеренного
		// базового слоя OSM (TASK-77453)
		
		log.debug("preRender аспектов карты");
		// нет удаления предыдущего выбранного слоя, т.к. известно, что пока что модель карты не держится в памяти,
		// т.е. она свежесозданная и чистая

		// всегда предлагаем аспекту заполнить модель карты, потому что могли запросить ререндер без изменений аспекта
		// или его настроек, а UnitOfWork может быть коротким, т.е. модель карты свежая и незаполненная
		// сам аспект определяет, нужно ли ему что-то делать с моделью
		if (currentAspect.getValue() != null && currentMapResultViewer.getValue() == null) {
			log.debugv("Заполнение модели карты аспектом {0}", currentAspect.getValue());
			currentAspect.getValueAsObject().populateMapModel(mapModel);
		}

		if (currentMapResultViewer.getValue() != null) {
			log.debugv("Заполнение модели карты результатами поиска {0}", currentMapResultViewer.getValue());
			currentMapResultViewer.getValueAsObject().populateMapModel(mapModel);
		}
		
	}
	
	public void preRenderDetailInfo(){
		if (currentAspect.getValue() != null){
			currentAspect.getValueAsObject().preRenderDetailInfo();
		}
		
	}

	// Пришлось вынести сюда, чтобы set viewParam провоцировал postConstruct
	public SuperClass getCurrentObject() {
		return currentObject.getValue();
	}

	public void setCurrentObject(SuperClass value) {
		currentObject.setValue(value);
	}

	public SuperClass getSearchedAddress() {
		return currentObject.getValue();
	}

	/**
	 * Изменение текущего объекта с принудительной сменой viewPort
	 */
	public void setSearchedAddress(SuperClass value) {
		currentObject.setValue(value, true);
	}

}
