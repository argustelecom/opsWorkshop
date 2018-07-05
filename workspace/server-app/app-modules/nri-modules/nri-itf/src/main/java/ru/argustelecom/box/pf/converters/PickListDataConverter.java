package ru.argustelecom.box.pf.converters;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.List;
import java.util.Objects;

/**
 * Конвертер данных для PickList'a из PrimeFaces
 * Без использования данного конвертера даже если указать в DualList'e тип хранимых данных
 * на выходе всегда получаем коллекцию строк
 * Created by s.kolyada on 08.02.2018.
 */
@FacesConverter("pickListDataConverter")
public class PickListDataConverter implements Converter {

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object entity) {
		if (entity == null)
			return "";
		return String.valueOf(entity.hashCode());
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String modelValue) {
		Object result = null;
		if (StringUtils.isBlank(modelValue)) {
			return null;
		}

		if (component instanceof PickList) {
			Object dualList = ((PickList) component).getValue();
			DualListModel dl = (DualListModel) dualList;
			result = findObject(dl.getSource(), modelValue);
			if (result == null) {
				result = findObject(dl.getTarget(), modelValue);
			}
		}

		return result;
	}

	/**
	 * Получение объекта по его хэшкоду
	 *
	 * @param objects 	список объектов
	 * @param hashCode  хэшкод искомого элемента
	 * @return объект с указанным хэшкодом или null если такого нет
	 */
	@SuppressWarnings("unchecked")
	private Object findObject(List objects, String hashCode) {
		return objects
				.stream()
				.filter(Objects::nonNull)
				.filter(obj -> hashCode.equals(String.valueOf(obj.hashCode())))
				.findFirst()
				.orElse(null);
	}
}
