package ru.argustelecom.box.nri.resources.requirements;

import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.resources.spec.ResourceSpecificationRepository;
import ru.argustelecom.box.nri.schema.ResourceSchemaRepository;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.resources.RequiredItemRepository;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;

import javax.inject.Inject;
import java.util.Optional;

/**
 * сервис для работы с требованиями
 * b.bazarov
 */
@ApplicationService
public class RequiredItemAppService {

	/**
	 * репозиторий для работы с требованиями
	 */
	@Inject
	private RequiredItemRepository requiredItemRepository;

	/**
	 * репозиторий для работы со схемами
	 */
	@Inject
	private ResourceSchemaRepository resourceSchemaRepository;

	/**
	 * репозиторий для работы с спецификациями ресурсов
	 */
	@Inject
	private ResourceSpecificationRepository resourceSpecificationRepository;

	/**
	 * транслятор в Дто
	 */
	@Inject
	private RequiredItemDtoTranslator requiredItemDtoTranslator;

	/**
	 * создаём объект по идентификатору спецификации и родительскому требованию
	 *
	 * @param id     идентификатор спецификации
	 * @param parent родительское требование
	 * @return созданный объект
	 */
	public RequiredItemDto create(Long id, RequiredItemDto parent) {
		return requiredItemDtoTranslator.translate(requiredItemRepository.create(resourceSpecificationRepository.findOne(id), requiredItemRepository.findById(parent.getId())));
	}

	/**
	 * создаём объект по идентификатору спецификации и схемы
	 *
	 * @param resourceSpecificationId идентификатор спецификации
	 * @param schema                  Схема
	 * @return созданный объект
	 */
	public RequiredItemDto create(Long resourceSpecificationId, ResourceSchemaDto schema) {
		return requiredItemDtoTranslator.translate(requiredItemRepository.create(resourceSpecificationRepository.findOne(resourceSpecificationId)
				, resourceSchemaRepository.findById(schema.getId())));
	}

	/**
	 * Удаляем требование
	 *
	 * @param id иденгтификационный номер требования
	 */
	public void removeItem(Long id) {
		RequiredItem item = requiredItemRepository.findById(id);
		if (item != null && item.getSchema() != null) {
			ResourceSchema schema = resourceSchemaRepository.findById(item.getSchema().getId());
			Optional.ofNullable(schema)
					.ifPresent(resSchema -> resSchema.removeRequirement(item));
		}
		requiredItemRepository.delete(item);
	}
}
