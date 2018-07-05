package ru.argustelecom.box.env.map.page;

import org.primefaces.util.SecurityUtils;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.system.inf.map.component.model.MapViewport;
import ru.argustelecom.system.inf.map.component.model.MapViewportFactory;
import ru.argustelecom.system.inf.map.component.model.baselayer.BaseLayer;
import ru.argustelecom.system.inf.metadata.Metadata;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.modelbase.SuperClass;
import ru.argustelecom.system.inf.page.CurrentEntity;
import ru.argustelecom.system.inf.page.PresentationState;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkState;

/**
 * Текущий объект на карте.
 * <p>
 * Например, адрес (Region, Street, Building).
 * 
 * @author s.golovanov
 */
@PresentationState
public class CurrentMapObject extends CurrentEntity<SuperClass> {
	private static final long serialVersionUID = 1L;

	@Inject
	private Metadata metadata;
	@Inject
	private CurrentMapViewport currentViewport;
	@Inject
	private MapViewportFactory viewportFactory;

	
	/** Шорткат для получения {@link #getValue()} as Building.
	 * null, если getValue не Building.
	 * Ясно что не надо также добавлять шорткаты для всех возможных сущностей. 
	 * Только для частоиспользуемых на карте сущностей среды.
	 */
	public Building getValueAsBuilding(){
		if (!isNull() && getValue() instanceof Building){
			return (Building)getValue();
		}else{
			return null;
		}
	}

	/**
	 * Устанавливает новый текущий объект и изменяет {@link CurrentMapViewport} чтобы
	 * текущий объект отображался в центре и с нужным масштабом.
	 */
	@Override
	public void setValue(SuperClass value){
		setValue(value, false);
	}

	public void setValue(SuperClass value, boolean forceFitViewport) {
		SuperClass oldValue = getValue();
		super.setValue(value);

		if (!isNull() && changed(oldValue)) {
			if (forceFitViewport || !viewportFactory.fitsObject(currentViewport.getValue(), value))
				fitToViewport();
		}
	}

	@Override
	protected void doSetValue( SuperClass value) {
		//Могут засеттить ObjectRef ObjectGeo.object 
		// 1. он embedable , мы не сможем его find
		// 2. сторонний наблюдатель, интересуюшийся определёнными классами value, не заметит например Building представленный как ObjectValue
		if ( value instanceof Building ){
				value = em.find( Building.class, value.getId() );
		}
		super.doSetValue(value);
	}

	@Override
	public String getEntityViewId(SuperClass value) {
		Building b = getValueAsBuilding();
		if ( b != null && SecurityUtils.ifAnyGranted("System_ViewBuilding, System_CreateAndEditBuilding")) {
			return "/views/system/address/buildingpassport/BuildingPassportView.xhtml";
		}
		return super.getEntityViewId(value);
	}
	
	/**
	 * Устанавливает {@link CurrentMapViewport} так чтобы текущий объект вмещался в него и находился в центре.
	 */
	public void fitToViewport(){
		checkState( !isNull() );
		// К этому моменту ViewModel уже должна дать дефолтный viewport
		checkState(currentViewport.getValue() != null);
		
		BaseLayer baseLayer = currentViewport.getValue().getBaseLayer();
		MapViewport newViewport = viewportFactory.createViewportToShow(getValue(), baseLayer);
		currentViewport.setValue(newViewport);
	}

	/**
	 * Возвращает true, если для выбранного объекта есть представление детальных сведений.
	 */
	public boolean hasDetail() {
		return getValue() instanceof Building;
	}
}
