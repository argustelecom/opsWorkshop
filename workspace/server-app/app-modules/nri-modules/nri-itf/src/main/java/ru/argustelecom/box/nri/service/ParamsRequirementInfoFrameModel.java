package ru.argustelecom.box.nri.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.nri.resources.requirements.RequiredItemDto;
import ru.argustelecom.box.nri.resources.requirements.RequiredParameterValueAppService;
import ru.argustelecom.box.nri.resources.requirements.RequiredParameterValueDto;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.schema.requirements.resources.comparators.CompareAction;
import ru.argustelecom.box.nri.schema.requirements.resources.comparators.ParameterDataTypeExtension;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;


/**
 * Модель формы работы с требованиями к парметрам
 * b.bazarov
 */
@Named(value = "paramsRequirementInfoFrameModel")
@PresentationModel
public class ParamsRequirementInfoFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Сервис для работы с трбованиями к параметрам
	 */
	@Inject
	private RequiredParameterValueAppService requiredParameterValueAppService;

	/**
	 * Текущее требование
	 */
	@Getter
	@Setter
	private RequiredItemDto requiredItem;

	/**
	 * текущее требование к параметру
	 */
	@Getter
	@Setter
	private RequiredParameterValueDto currentRequiredParameterValueDto = RequiredParameterValueDto.builder().build();

	/**
	 * список выбранных требований к параметрам
	 */
	@Getter
	@Setter
	private List<RequiredParameterValueDto> selectedRequiredParameterValueDto;


	/**
	 * возможные спецификации параметров
	 */
	@Getter
	private List<ParameterSpecificationDto> possibleParamSpecification;

	/**
	 * Инициализация
	 *
	 * @param item требование
	 */
	public void preRender(RequiredItemDto item) {
		currentRequiredParameterValueDto = RequiredParameterValueDto.builder().build();
		possibleParamSpecification =  item == null ? null : item.getResourceSpecification() == null ? null : item.getResourceSpecification().getParameters();
		this.requiredItem = item;

	}

	/**
	 * Типы сравнения
	 *
	 * @return Типы сравнения
	 */
	public List<CompareAction> getTypes() {

		if (currentRequiredParameterValueDto.getParameterSpecification() != null) {

			ArrayList<CompareAction> list = new ArrayList<>();
			ParameterDataTypeExtension.comapratorsFor(currentRequiredParameterValueDto.getParameterSpecification()
					.getDataType()).values().forEach(item -> list.addAll(item.supportedComparations()));

			return list.stream().distinct().collect(toList());
		}
		return new ArrayList<>();
	}

	/**
	 * Очистить параметры создания
	 */
	public void cleanCreationParams() {
		currentRequiredParameterValueDto = RequiredParameterValueDto.builder().build();
	}

	/**
	 * Создать/изменить требование
	 */
	public void submit() {
		if (currentRequiredParameterValueDto == null)
			return;
		if (currentRequiredParameterValueDto.getId() != null) {
			//Редактирование
			requiredParameterValueAppService.save(currentRequiredParameterValueDto);
		} else {
			//Создание нового
			requiredItem.getRequiredParameters().add(requiredParameterValueAppService.create(currentRequiredParameterValueDto, requiredItem));
		}
		cleanCreationParams();
	}

	/**
	 * Удаление выделенных требований к параметрам
	 */
	public void deleteSelected() {
		if (!CollectionUtils.isEmpty(selectedRequiredParameterValueDto)) {
			selectedRequiredParameterValueDto.forEach(rpv -> requiredParameterValueAppService.remove(rpv));
			selectedRequiredParameterValueDto.forEach(rpv -> requiredItem.getRequiredParameters().remove(rpv));
			selectedRequiredParameterValueDto = null;
		}
	}
}
