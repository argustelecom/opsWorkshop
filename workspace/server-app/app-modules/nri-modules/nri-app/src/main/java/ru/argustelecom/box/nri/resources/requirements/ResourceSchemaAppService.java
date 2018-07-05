package ru.argustelecom.box.nri.resources.requirements;


import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.booking.BookingOrderRepository;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.schema.ResourceSchemaRepository;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.box.nri.service.ServiceSpecificationRepository;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы со схемами ресурсов
 * Created by b.bazarov on 05.10.2017.
 */
@ApplicationService
public class ResourceSchemaAppService implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * репозиторий для работы с спецификациями служб
	 */
	@Inject
	private ServiceSpecificationRepository serviceSpecificationRepository;

	/**
	 * репозиторий для работы со схемами
	 */
	@Inject
	private ResourceSchemaRepository schemaRepository;

	/**
	 * Репозиторий бронирований
	 */
	@Inject
	BookingOrderRepository bookingOrderRepository;

	/**
	 * Транслятор схем в дто
	 */
	@Inject
	private ResourceSchemaDtoTranslator resourceSchemaDtoTranslator;

	/**
	 * Создать новую схему
	 *
	 * @param name                   имя
	 * @param serviceSpecificationId спецификация службы
	 * @return дто схемы
	 */
	public ResourceSchemaDto createResourceSchema(String name, Long serviceSpecificationId) {
		return resourceSchemaDtoTranslator.translate(schemaRepository.create(name, serviceSpecificationRepository.findOne(serviceSpecificationId)));
	}

	/**
	 * Удалить схему
	 *
	 * @param elementTypeId айди типа элемента
	 */
	public void removeResourceSchema(@NotNull Long elementTypeId) {
		releaseAllBookingsBySchema(elementTypeId);
		schemaRepository.delete(elementTypeId);
	}

	/**
	 * Снимает бронь со всего что имеет связь со схемой
	 * @param schemaId ид схемы
	 */
	private void releaseAllBookingsBySchema(@NotNull Long schemaId){
		ResourceSchema schema = schemaRepository.findById(schemaId);
		if (schema == null)
			return;
		for (ResourceRequirement br : schema.getBookings()) {
			List<BookingOrder> orders = bookingOrderRepository.loadAllBookingsByRequirement(br);
			//высвобождаем забронированные ресурсы
			if (CollectionUtils.isNotEmpty(orders)) {
				orders.forEach(bookingOrderRepository::release);
			}
		}
	}

	/**
	 * Найти все схемы с этой спецификацией
	 *
	 * @param serviceSpecificationId спецификация
	 * @return список
	 */
	public List<ResourceSchemaDto> findAll(Long serviceSpecificationId) {

		List<ResourceSchemaDto> list = new ArrayList<>();
		schemaRepository.findByServiceSpecification(serviceSpecificationRepository.findOne(serviceSpecificationId))
				.forEach(schema -> list.add(resourceSchemaDtoTranslator.translate(schema)));
		return list;
	}

	/**
	 * Изменить имя
	 *
	 * @param schemaDto дто схемы
	 */
	public void changeName(ResourceSchemaDto schemaDto) {
		ResourceSchema schema = schemaRepository.findById(schemaDto.getId());
		if (schema != null) {
			schema.setName(schemaDto.getName());
			schemaRepository.save(schema);
		}

	}

	/**
	 * Найти все схемы подключения услуги по привязанные к спецификации
	 *
	 * @param serviceSpec спецификация
	 * @return список схем подключения
	 */
	public List<ResourceSchema> findAllByServiceSpec(ServiceSpec serviceSpec) {
		return schemaRepository.findByServiceSpecification(serviceSpec);
	}

}
