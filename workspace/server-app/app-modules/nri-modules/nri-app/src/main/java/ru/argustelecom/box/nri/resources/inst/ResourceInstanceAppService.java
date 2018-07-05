package ru.argustelecom.box.nri.resources.inst;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberRepository;
import ru.argustelecom.box.nri.resources.ResourceInstanceRepository;
import ru.argustelecom.box.nri.resources.inst.nls.ResourceInstanceAppServiceMessagesBundle;
import ru.argustelecom.box.nri.resources.model.ParameterValue;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationRepository;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;
import ru.argustelecom.system.inf.exception.BusinessException;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Сервис для работы с ресурсами
 *
 * @author d.khekk
 * @since 21.09.2017
 */
@ApplicationService
public class ResourceInstanceAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Транслятор ресурсов
	 */
	@Inject
	private ResourceInstanceDtoTranslator translator;

	/**
	 * Репозиторий ресурсов
	 */
	@Inject
	private ResourceInstanceRepository repository;

	/**
	 * Репозиторий спецификаций
	 */
	@Inject
	private ResourceSpecificationRepository specificationRepository;

	/**
	 * Репозиторий номеров
	 */
	@Inject
	private PhoneNumberRepository phoneNumberRepository;

	/**
	 * Сервис генерации айдишников
	 */
	@Inject
	private IdSequenceService idSequenceService;

	/**
	 * Найти ресурс по id
	 *
	 * @param id айди ресурса
	 * @return найденный ресурс
	 */
	public ResourceInstanceDto findResource(Long id) {
		return translator.translate(repository.findOne(id));
	}

	/**
	 * Создать новый ресурс
	 * @param resourceDto дто ресурса
	 * @param parentResourceDto дто родительского ресурса
	 * @return созданный ресурс
	 */
	public ResourceInstanceDto createResource(ResourceInstanceDto resourceDto, ResourceInstanceDto parentResourceDto) {
		ResourceSpecification resourceSpecification = specificationRepository.findOne(resourceDto.getSpecification().getId());
		ResourceInstance resource = ResourceInstance.builder()
				.id(idSequenceService.nextValue(ResourceInstance.class))
				.name(resourceDto.getName())
				.status(resourceDto.getStatus())
				.specification(resourceSpecification)
				.build();
		resource.setParameterValues(resourceDto.getParameterValues().stream()
				.map(parameterDto -> ParameterValue.builder()
						.id(idSequenceService.nextValue(ParameterValue.class))
						.resource(resource)
						.value(parameterDto.getValue())
						.specification(resourceSpecification.getParameters().stream()
								.filter(paramSpec -> paramSpec.getId().equals(parameterDto.getSpecification().getId()))
								.findFirst()
								.orElseThrow(() -> new BusinessException(LocaleUtils.getMessages(ResourceInstanceAppServiceMessagesBundle.class).specWasNotFound())))
						.build())
				.collect(toList()));
		repository.create(resource);
		if (parentResourceDto != null) {
			ResourceInstance parentResource = repository.findOne(parentResourceDto.getId());
			parentResource.addChild(resource);
			repository.save(parentResource);
		}
		return translator.translate(resource);
	}

	/**
	 * Изменить параметр ресурса
	 * @param resourceDto дто ресурса
	 * @param parameterValueDto параметр для изменения
	 */
	public void changeParameter(ResourceInstanceDto resourceDto, ParameterValueDto parameterValueDto) {
		ResourceInstance resource = repository.findOne(resourceDto.getId());
		resource.getParameterValues().stream()
				.filter(parameterValue -> parameterValue.getSpecification().getId().equals(
						parameterValueDto.getSpecification().getId()))
				.findFirst()
				.ifPresent(parameterValue -> parameterValue.setValue(parameterValueDto.getValue()));
		repository.save(resource);
	}

	/**
	 * Переименовать ресурс
	 * @param resourceDto дто ресурса
	 */
	public void renameResource(ResourceInstanceDto resourceDto) {
		ResourceInstance resource = repository.findOne(resourceDto.getId());
		resource.setName(resourceDto.getName());
		repository.save(resource);
	}

	/**
	 * Удалить ресурс
	 * @param id id
	 */
	public void removeResource(Long id) {
		repository.delete(id);
	}

	/**
	 * Изменить статус ресурса
	 * @param resourceDto ресурс
	 */
	public void changeStatus(ResourceInstanceDto resourceDto) {
		ResourceInstance resource = repository.findOne(resourceDto.getId());
		resource.setStatus(resourceDto.getStatus());
		repository.save(resource);
	}

	/**
	 * Добавить телефонные номера в ресурс
	 * @param resId ресурс
	 * @param phoneNumbers номера
	 * @return обновленный ресурс
	 */
	public ResourceInstanceDto addPhoneNumbers(Long resId, List<PhoneNumberDto> phoneNumbers) {
		List<Long> phoneNumberIds = phoneNumbers.stream().map(PhoneNumberDto::getId).collect(toList());
		ResourceInstance savedResource = repository.addPhoneNumbers(resId, phoneNumberIds);
		return translator.translate(savedResource);
	}
}
