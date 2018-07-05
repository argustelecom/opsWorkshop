package ru.argustelecom.box.nri.resources;

import lombok.Getter;
import ru.argustelecom.box.nri.resources.inst.ParameterValueDto;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceAppService;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.model.ResourceStatus;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * Фрейм страницы с информацией о ресурса
 *
 * @author s.kolyada
 * @since 20.09.2017
 */
@Named(value = "resourceInfoFrameModel")
@PresentationModel
public class ResourceInfoFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Признак режима создания нового ресурса
	 */
	@Getter
	private Boolean creationMode = false;

	/**
	 * Создаваемый/редактируемый ресурс
	 */
	@Getter
	private ResourceInstanceDto resource;

	/**
	 * Спецификация создаваемого/редактируемого ресурса
	 */
	@Getter
	private ResourceSpecificationDto specification;

	/**
	 * Параметры для отображения на форме
	 */
	@Getter
	private List<ParameterValueDto> parameters;

	/**
	 * Сервис для работы с ресурсами
	 */
	@Inject
	private ResourceInstanceAppService service;

	/**
	 * Инициализирует фрейм
	 * @param resource русрс
	 */
	public void preRender(ResourceInstanceDto resource) {
		// если нам на вход не дали конкретный ресурс, то переходим в режим создания ресурса
		// при этом спецификация должна быть нам передана из вне
		this.resource = resource;
		this.specification = resource.getSpecification();
		this.parameters = resource.getParameterValues();
		parameters.sort(Comparator.comparing(param -> param.getSpecification().getId()));
	}
	/**
	 * Изменить параметр ресурса
	 *
	 * @param parameterValueDto дто параметра для изменения
	 */
	public void changeParameter(ParameterValueDto parameterValueDto) {
		service.changeParameter(resource, parameterValueDto);
	}

	/**
	 * Переименовать ресурс
	 */
	public void changeName() {
		service.renameResource(resource);
	}

	/**
	 * Изменить стстус ресурса
	 */
	public void changeStatus() {
		service.changeStatus(resource);
	}

	/**
	 * Получить список всех возможных статусов
	 * @return список статусов
	 */
	public ResourceStatus[] getAllStatuses() {
		return ResourceStatus.values();
	}
}
