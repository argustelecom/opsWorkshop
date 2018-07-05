package ru.argustelecom.box.nri.resources.requirements;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.resources.ParameterSpecificationRepository;
import ru.argustelecom.box.nri.schema.requirements.resources.RequiredItemRepository;
import ru.argustelecom.box.nri.schema.requirements.resources.RequiredParameterValueRepository;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredParameterValue;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Сервис для работы со значениями
 * Created by b.bazarov on 09.10.2017.
 */
@ApplicationService
public class RequiredParameterValueAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис генерации айдишников
	 */
	@Inject
	private IdSequenceService idSequenceService;

	/**
	 * Репозиторий для работы со значениями
	 */
	@Inject
	private RequiredParameterValueRepository requiredParameterValueRepository;

	/**
	 * репозиторий спецификаций параметров
	 */
	@Inject
	private ParameterSpecificationRepository parameterSpecificationRepository;

	/**
	 * репозиторий с требованиями к рес
	 */
	@Inject
	private RequiredItemRepository requiredItemRepository;
	/**
	 * транслятор
	 */
	@Inject
	private RequiredParameterValueDtoTranslator requiredParameterValueDtoTranslator;

	/**
	 *  Создание значения
	 * @param dto Дто требования к  значению параметру
	 * @param riDto Требование
	 * @return  Дто значения
	 */
	public RequiredParameterValueDto create(RequiredParameterValueDto dto, RequiredItemDto riDto) {


		RequiredParameterValue value = RequiredParameterValue.builder().id(idSequenceService.nextValue(RequiredParameterValue.class)).
				requiredValue(dto.getValue()).compareAction(dto.getCompareAction()).parameterSpecification(
				parameterSpecificationRepository.findOne(dto.getParameterSpecification().getId()))
				.requiredItem(requiredItemRepository.findById(riDto.getId())).build();
		return requiredParameterValueDtoTranslator.translate(requiredParameterValueRepository.create(value, riDto.getId()));
	}

	/**
	 * Создание значения
	 *
	 * @param dto Дто значения
	 */
	public void save(RequiredParameterValueDto dto) {
		RequiredParameterValue value = requiredParameterValueRepository.findById(dto.getId());
		if (value != null) {
			value.setRequiredValue(dto.getValue());
			value.setCompareAction(dto.getCompareAction());
			value.setParameterSpecification(parameterSpecificationRepository.findOne(dto.getParameterSpecification().getId()));
			requiredParameterValueRepository.save(value);
		}
	}


	/**
	 * Удалить требование
	 *
	 * @param dto Дто требования
	 */
	public void remove(RequiredParameterValueDto dto) {
		requiredParameterValueRepository.delete(dto.getId());
	}

}
