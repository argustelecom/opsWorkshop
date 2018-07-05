package ru.argustelecom.box.nri.integration;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.booking.BookingAppService;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.integration.viewmodel.BookingResourceDataHolder;
import ru.argustelecom.box.nri.integration.viewmodel.LogicalResourceDataHolder;
import ru.argustelecom.box.nri.integration.viewmodel.nls.ServiceResourceInfoFMMessagesBundle;
import ru.argustelecom.box.nri.loading.ResourceLoadingAppService;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaAppService;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.box.nri.service.ServiceSpecificationRepository;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Контроллер фрейма с информацие о ресурсах услуги,
 * используемого в других модулях BOX
 *
 * Created by s.kolyada on 22.12.2017.
 */
@Named(value = "serviceResourceInfoFrameModel")
@PresentationModel
public class ServiceResourceInfoFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Текущий сервис
	 */
	private Service currentService;

	/**
	 * Текущая выбранная схема
	 */
	@Getter
	@Setter
	private ResourceSchema selectedSchema;

	/**
	 * Доступные схемы
	 */
	@Getter
	private List<ResourceSchema> availableSchemas;

	/**
	 * Ресурсн нагружающие услугу
	 */
	@Getter
	private List<LogicalResourceDataHolder> loadings;

	/**
	 * Бронирования ресурсов
	 */
	@Getter
	private List<BookingResourceDataHolder> bookings;

	/**
	 * Сервис работы со схемами
	 */
	@Inject
	private ResourceSchemaAppService resourceSchemaAppService;

	/**
	 * Сервис работы со спецификациями услуг
	 */
	@Inject
	private ServiceSpecificationRepository serviceSpecificationRepository;

	/**
	 * Сервис работы с нагрузками
	 */
	@Inject
	private ResourceLoadingAppService resourceLoadingAppService;

	/**
	 * Сервис работы с броянми
	 */
	@Inject
	private BookingAppService bookingAppService;

	/**
	 * Создать требование к IP адресу
	 */
	@Getter
	private Consumer<ResourceRequirement> createNewBooking = (newBooking) -> {
		initialize();
	};

	/**
	 * Подготовка к отоюражению
	 *
	 * @param service услуга
	 */
	public void preRender(Service service) {
		if (service == null) {
			reset();
			return;
		}

		currentService = service;

		initialize();
	}

	/**
	 * Инициализация
	 */
	private void initialize() {
		// сперва пытаемся получиь список нагруженных ресурсов, если такие есть, то выводятся только они
		// и дальнейшая загрузка данных не требуется
		loadings = initLoadingsIfAny(currentService);
		if (CollectionUtils.isNotEmpty(loadings)) {
			return;
		}

		// теперь ищем информацию о бронях, если такая есть, то на этом и останавливаемся
		// предоставляя пользователю возможность её редактирования и просмотра
		bookings = initBookingsIfAny(currentService);
		if (CollectionUtils.isNotEmpty(bookings)) {
			sortBookings();
			return;
		}

		// раз мы тут, то нет ни нагруженных ресурсов, ни забронированных
		// значит надо бронировать ресурсы, если требуется, поэтому загружаем доступные нам схемы
		// если схема всего 1, то сразу её и выбираем
		availableSchemas = loadAvailableSchemas(currentService);
		if (CollectionUtils.isNotEmpty(availableSchemas) && availableSchemas.size() == 1) {
			selectedSchema = availableSchemas.get(0);
			availableSchemas.clear();
			bookings = initBookingsForSchema(selectedSchema);
			sortBookings();
		}
	}

	/**
	 * Сбросиьт данные
	 */
	private void reset() {
		selectedSchema = null;
		availableSchemas.clear();
		currentService = null;
	}

	/**
	 * Получить список нагрузок для услуги, с учётом возможно забронированных ресурсов
	 * @param service услуга
	 * @return список нагруженных ресурсов или пустой лист, если их нет
	 */
	private List<LogicalResourceDataHolder> initLoadingsIfAny(Service service) {
		List<LogicalResourceDto> allLoadedResources = resourceLoadingAppService.loadAllLoadedResourcesByService(service);

		if (CollectionUtils.isEmpty(allLoadedResources)) {
			return Collections.emptyList();
		}

		return allLoadedResources.stream()
				.map(LogicalResourceDataHolder::convert)
				.collect(Collectors.toList());
	}

	/**
	 * Получить список заброннированных услугой ресурсов, если такие есть
	 * @param service услуга
	 * @return список нагруженных ресурсов или пустой лист, если их нет
	 */
	private List<BookingResourceDataHolder> initBookingsIfAny(Service service) {
		List<BookingOrder> bookingOrders = bookingAppService.loadAllBookingsByService(service);
		if (CollectionUtils.isEmpty(bookingOrders)) {
			return Collections.emptyList();
		}

		Set<ResourceRequirement> solvedRequirements = bookingOrders.stream().map(BookingOrder::getRequirement).collect(Collectors.toSet());
		Set<ResourceSchema> schemas = solvedRequirements.stream().map(ResourceRequirement::getSchema).collect(Collectors.toSet());

		if (CollectionUtils.isNotEmpty(schemas) && schemas.size() > 1) {
			throw new IllegalStateException(LocaleUtils.getMessages(ServiceResourceInfoFMMessagesBundle.class).foundBookingsByOneServiceForDifferentSchemas());
		}

		selectedSchema = schemas.iterator().next();

		List<BookingResourceDataHolder> result = initBookingsForSchema(selectedSchema);

		result.forEach(dh -> {
			if (solvedRequirements.contains(dh.getRequirement())) {
				BookingOrder currentOrder = bookingOrders.stream()
						.filter(bk -> dh.getRequirement().equals(bk.getRequirement()))
						.findAny()
						.orElse(null);
				dh.setBookingOrder(currentOrder);
			}
		});

		return result;
	}

	/**
	 * Получить список нагрузок для схемы подключения
	 * @param schema схема подключения
	 * @return список всех нагрузок
	 */
	private List<BookingResourceDataHolder> initBookingsForSchema(ResourceSchema schema) {
		List<BookingResourceDataHolder> result = new ArrayList<>(schema.getBookings().size());
		for (ResourceRequirement requirement : schema.getBookings()) {
			BookingOrder currentOrder = null;
			BookingResourceDataHolder holder = BookingResourceDataHolder.builder()
					.bookingOrder(currentOrder)
					.requirement(requirement)
					.build();
			result.add(holder);
		}
		return result;
	}

	/**
	 * Перезагрузить модель требований к бронированиям
	 */
	public void initializeRequirementsModel() {
		if (Objects.isNull(selectedSchema) || CollectionUtils.isEmpty(selectedSchema.getBookings())) {
			return;
		}
		bookings = new ArrayList<>(selectedSchema.getBookings().size());
		for (ResourceRequirement requirement : selectedSchema.getBookings()) {
			BookingResourceDataHolder holder = BookingResourceDataHolder.builder()
					.bookingOrder(null)
					.requirement(requirement)
					.build();
			bookings.add(holder);
		}
	}

	/**
	 * Загрузить доступные схемы подключения
	 */
	private List<ResourceSchema> loadAvailableSchemas(Service service) {
		return resourceSchemaAppService
				.findAllByServiceSpec(EntityManagerUtils.initializeAndUnproxy(service.getPrototype()));
	}

	/**
	 * Отсортировать бронирования по их статусу - сперва идут незабронированные
	 */
	private void sortBookings() {
		bookings.sort((a, b) -> Boolean.compare(a.isBooked(), b.isBooked()));
	}

	/**
	 * Выводить ли фрейм с ресурсами
	 * @return истина, если выводить, иначе ложь
	 */
	public boolean shouldBeRendered() {
		// TODO дополнить условиями
		return CollectionUtils.isNotEmpty(loadings)
				|| (selectedSchema != null && CollectionUtils.isNotEmpty(bookings))
				|| CollectionUtils.isNotEmpty(availableSchemas);
	}

	/**
	 * Вывдодить ли выбор схемы
	 * @return  истина, если выводить, иначе ложь
	 */
	public boolean shouldRenderSchemaSelection() {
		return CollectionUtils.isEmpty(loadings)
				&& CollectionUtils.isEmpty(bookings)
				&& selectedSchema == null;
	}

	/**
	 * Выводить ли нагруженные ресурсы
	 * @return  истина, если выводить, иначе ложь
	 */
	public boolean shouldRenderLoadings() {
		return CollectionUtils.isNotEmpty(loadings);
	}

	/**
	 * Выводоить ли информацию о брониварониях
	 * @return  истина, если выводить, иначе ложь
	 */
	public boolean shouldRenderBookings() {
		return !shouldRenderLoadings()
				&& !shouldRenderSchemaSelection()
				&& CollectionUtils.isNotEmpty(bookings);
	}
}
