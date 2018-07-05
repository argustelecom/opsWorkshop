package ru.argustelecom.box.nri.resources;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.box.nri.resources.inst.ParameterValueDto;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceAppService;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.box.nri.resources.model.ResourceStatus;
import ru.argustelecom.box.nri.resources.nls.CreateResourceInfoFrameModelMessageBundle;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationDtoTranslator;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationRepository;
import ru.argustelecom.box.nri.resources.spec.model.ParameterDataType;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Фрейм страницы с информацией о ресурса
 *
 * @author s.kolyada
 * @since 20.09.2017
 */
@Named(value = "createResourceInfoFrameModel")
@PresentationModel
public class CreateResourceInfoFrameModel implements Serializable {

	private static final Logger log = Logger.getLogger(CreateResourceInfoFrameModel.class);
	private static final long serialVersionUID = 1L;

	/**
	 * Типы данных, которые можно инкрементировать
	 */
	@Getter
	private static final List<ParameterDataType> INCREMENTABLE_DATA_TYPES = Arrays.asList(ParameterDataType.FLOAT,
			ParameterDataType.INTEGER, ParameterDataType.STRING);

	/**
	 * Создаваемый/редактируемый ресурс
	 */
	@Getter
	private ResourceInstanceDto resource;

	/**
	 * Родительский ресурс, на случай, елси создаётся дочерний
	 */
	@Getter
	private ResourceInstanceDto parentResource;

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
	 * Инкрементируемые параметры
	 */
	@Getter
	@Setter
	private List<ParameterSpecificationDto> incrementParams = new ArrayList<>();

	/**
	 * Количество создаваемых ресурсов
	 */
	@Getter
	@Setter
	private Integer newResourcesNumber = 1;

	/**
	 * Начальное значение инкремента
	 */
	@Getter
	@Setter
	private Integer initialResNumber = 1;

	/**
	 * Надо ли добавлять инкремент к имени ресурса
	 */
	@Getter
	@Setter
	private Boolean shouldIncrementName = true;

	/**
	 * Коллбек для передачи на страницу созданных ресурсов
	 */
	@Getter
	@Setter
	private Callback<List<ResourceInstanceDto>> onSaveButtonPressed;

	/**
	 * Сервис для работы с ресурсами
	 */
	@Inject
	private ResourceInstanceAppService service;

	/**
	 * Репозиторий для работы с спецификациями ресурсов
	 */
	@Inject
	private ResourceSpecificationRepository specificationRepository;

	/**
	 * Транслятор спецификации ресурса в ДТО
	 */
	@Inject
	private ResourceSpecificationDtoTranslator specificationDtoTranslator;

	/**
	 * Конвертер спецификации ресурса для JSF
	 */
	@Getter
	private ResourceSpecificationDTOConverter converter = new ResourceSpecificationDTOConverter();

	/**
	 * Инициализирует фрейм
	 *
	 * @param parentResource  родительский ресурс
	 * @param specificationId идентификатор спецификации
	 */
	public void preRender(ResourceInstanceDto parentResource, Long specificationId) {
		// если нам на вход не дали конкретный ресурс, то переходим в режим создания ресурса
		// при этом спецификация должна быть нам передана из вне
		this.resource = ResourceInstanceDto.builder().build();
		this.specification = specificationDtoTranslator.translate(specificationRepository.findOne(specificationId));
		this.parentResource = parentResource;
		this.parameters = initParameters(specification);
		this.parameters.sort(Comparator.comparing(param -> param.getSpecification().getName()));

		reinitNewResourceDefaults();
	}

	/**
	 * Сбросить настройки создания ресурса
	 */
	private void reinitNewResourceDefaults() {
		incrementParams = new ArrayList<>();
		newResourcesNumber = 1;
		initialResNumber = 1;
		shouldIncrementName = true;
	}

	/**
	 * Проинициализировать
	 *
	 * @param resSpecification дто спецификации ресурса
	 * @return список параметров
	 */
	private List<ParameterValueDto> initParameters(ResourceSpecificationDto resSpecification) {
		List<ParameterValueDto> res = new ArrayList<>();

		if (resSpecification != null) {
			for (ParameterSpecificationDto paramSpec : resSpecification.getParameters()) {
				res.add(ParameterValueDto.builder().specification(paramSpec).build());
			}
		}

		return res;
	}

	/**
	 * Создать ресурсы
	 */
	public void createResource() {
		if (!validateRequiredParams())
			return;

		String name = resource.getName();
		List<ResourceInstanceDto> createdResources = new ArrayList<>();
		// если всё ок, то создаём нужное кол-во ресурсов
		for (int i = 0; i < newResourcesNumber; i++) {
			resource = createSingleResource(name);
			createdResources.add(resource);
			initialResNumber++;
		}

		// если коллбек для создания нам не передавали, значит мы его и не трогаем
		if (onSaveButtonPressed != null)
			onSaveButtonPressed.execute(createdResources);

		// если создавали независимый ресурс, то перенаправляем на его страницу
		if (resource.getSpecification().getIsIndependent()) {
			try {
				redirectToNewResource();
			} catch (IOException e) {
				log.error("Ошибка во время редиректа", e);
			}
		}
	}

	/**
	 * Провалидировать параметры
	 *
	 * @return true, если валидация прошла успешно
	 */
	private boolean validateRequiredParams() {
		for (ParameterValueDto value : parameters) {
			if (StringUtils.isBlank(value.getValue()) && value.getSpecification().getRequired()) {
				if (isEmpty(incrementParams) || !incrementParams.contains(value.getSpecification())) {
					CreateResourceInfoFrameModelMessageBundle messages = LocaleUtils.getMessages(CreateResourceInfoFrameModelMessageBundle.class);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, messages.someRequiredParametersDidNotSet(), null));
					FacesContext.getCurrentInstance().validationFailed();
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Создать ресурс
	 */
	private ResourceInstanceDto createSingleResource(String name) {
		ResourceInstanceDto newRes = ResourceInstanceDto.builder()
				.name(name)
				.specification(specification)
				.status(resource.getStatus())
				.build();
		if (shouldIncrementName && newResourcesNumber > 1) {
			newRes.setName(newRes.getName() + " " + initialResNumber);
		}
		if (isEmpty(incrementParams)) {
			newRes.addParameterValues(parameters);
		} else {
			for (ParameterValueDto param : parameters) {
				ParameterValueDto modifiedParam = param.toBuilder().build();
				if (incrementParams.contains(param.getSpecification())) {
					modifiedParam.setValue(Optional.ofNullable(modifiedParam.getValue()).orElse("")
							+ initialResNumber);
				}
				newRes.addParameterValue(modifiedParam);
			}
		}
		if (parentResource != null)
			parentResource.addChild(newRes);

		return service.createResource(newRes, parentResource);
	}

	/**
	 * Получить список всех возможных статусов
	 *
	 * @return список статусов
	 */
	public ResourceStatus[] getAllStatuses() {
		return ResourceStatus.values();
	}

	/**
	 * Подготовить список спецификаций параметров для автоподбора
	 *
	 * @param query запрос
	 * @return список подходящих спецификаций
	 */
	public List<ParameterSpecificationDto> completeSpec(String query) {
		List<ParameterSpecificationDto> filteredThemes = new ArrayList<>();

		for (ParameterValueDto param : parameters) {
			ParameterSpecificationDto paramSpec = param.getSpecification();

			// игнорировать параметры, которые мы не можем заинкрементить
			// не выводить уже выбранные варианты
			if (incrementParams != null
					&& !incrementParams.contains(paramSpec)
					&& paramSpec.getName().toLowerCase().contains(query.toLowerCase())
					&& INCREMENTABLE_DATA_TYPES.contains(paramSpec.getDataType())) {
				filteredThemes.add(paramSpec);
			}
		}

		return filteredThemes;
	}

	/**
	 * Перенаправление на страницу созданного ресурса
	 *
	 * @throws IOException исключение при редиректе
	 */
	private void redirectToNewResource() throws IOException {
		FacesContext.getCurrentInstance()
				.getExternalContext()
				.redirect("ResourceView.xhtml?resource=ResourceInstance-" + resource.getId());
	}

	/**
	 * Конвертер спецификации ресурса в формат пригодный для отображения в JSF
	 */
	public class ResourceSpecificationDTOConverter implements Converter, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
			if (value != null && value.trim().length() > 0) {
				try {
					return specification.getParameters()
							.stream()
							.filter(sp -> sp.getId().equals(Long.parseLong(value)))
							.findFirst().orElse(null);
				} catch (NumberFormatException e) {
					CreateResourceInfoFrameModelMessageBundle messages = LocaleUtils.getMessages(CreateResourceInfoFrameModelMessageBundle.class);
					throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,messages.conversionError()
							, messages.notValidSpecification()));
				}
			} else {
				return null;
			}
		}

		@Override
		public String getAsString(FacesContext fc, UIComponent uic, Object object) {
			if (object != null) {
				return String.valueOf(((ParameterSpecificationDto) object).getId());
			} else {
				return null;
			}
		}
	}
}
