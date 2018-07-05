package ru.argustelecom.box.nri.resources.spec;

import ru.argustelecom.box.inf.service.ApplicationService;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Сервис спецификаций ресурсов
 * @author a.wisniewski
 * @since 29.09.2017
 */
@ApplicationService
public class ResourceSpecificationAppService {

	/**
	 * Репозиторий спецификаций ресурсов
	 */
	@Inject
	private ResourceSpecificationRepository specRepository;

	/**
	 * Транслятор спецификаций ресурсов
	 */
	@Inject
	private ResourceSpecificationDtoTranslator translator;

	/**
	 * Возвращает все спецификации ресурсов
	 * @return все спецификацйии ресурсов
	 */
	public List<ResourceSpecificationDto> findAllSpecifications() {
		return specRepository.findAll().stream()
				.map(translator::translate)
				.collect(toList());
	}

	/**
	 * Найти спецификацию
	 * @param id идентификатор спецификации
	 * @return спецификацию
	 */
	public ResourceSpecificationDto findSpecification(Long id) {
		return translator.translate(specRepository.findOne(id));
	}
}
