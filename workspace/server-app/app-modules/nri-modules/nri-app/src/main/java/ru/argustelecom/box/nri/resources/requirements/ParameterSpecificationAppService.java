package ru.argustelecom.box.nri.resources.requirements;

import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.resources.ParameterSpecificationRepository;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDtoTranslator;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Сервис для работы со спецификациями параметра
 * b.bazarov
 */
@ApplicationService
public class ParameterSpecificationAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * транслятор спецификаций
	 */
	@Inject
	private ParameterSpecificationDtoTranslator parameterSpecificationDtoTranslator;

	/**
	 * репозиторий спецификаций параметров
	 */
	@Inject
	private ParameterSpecificationRepository parameterSpecificationRepository;

	/**
	 * Получить все спецификации параметра
	 *
	 * @return список всех спецификаций параметра
	 */
	public List<ParameterSpecificationDto> getAllParameterSpecification() {
		return parameterSpecificationRepository.findAll().stream()
				.map(parameterSpecificationDtoTranslator::translate)
				.collect(toList());
	}
}
