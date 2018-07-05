package ru.argustelecom.box.nri.loading;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.loading.model.ResourceLoading;
import ru.argustelecom.box.nri.loading.nls.ResourceLoadingAppServiceMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressRepository;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressLifecycle;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberRepository;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberLifecycle;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с нагрузкой
 * Created by b.bazarov on 18.12.2017.
 */
@ApplicationService
public class ResourceLoadingAppService implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * сервис lifecycle routing'a
	 */
	@Inject
	private LifecycleRoutingService lifecycleService;

	/**
	 * Репозиторий дступа к нагрузкам
	 */
	@Inject
	private ResourceLoadingRepository resourceLoadingRepository;

	/**
	 * Репозиторий доступа к IPадресам
	 */
	@Inject
	private IPAddressRepository ipAddressRepository;

	/**
	 * Репозиторий доступа к телефонным номерам
	 */
	@Inject
	private PhoneNumberRepository phoneNumberRepository;

	/**
	 * Транслятор логического ресурса
	 */
	@Inject
	private LogicalResourceDtoTranslator logicalResourceDtoTranslator;

	/**
	 * Создать нагрузку по брони
	 *
	 * @param bookingOrder бронь
	 * @return созданная нагрузка или null, если не удалось
	 */
	public ResourceLoading loadResource(@Nonnull BookingOrder bookingOrder) {
		ResourceLoadingAppServiceMessagesBundle messages = LocaleUtils.getMessages(ResourceLoadingAppServiceMessagesBundle.class);
		Validate.isTrue(bookingOrder != null, messages.doNotSetBooking());

		ResourceLoading loading = resourceLoadingRepository
				.createResourceLoading(new HashSet<>(bookingOrder.getBookedLogicalResource()),
										bookingOrder.getServiceInstance());

		if (loading == null || CollectionUtils.isEmpty(loading.getLoadedLogicalResource()))
			return loading;

		//Установим ресурсам статус OCCUPIED что бы их нельзя было выбирать снова
		loading.getLoadedLogicalResource().forEach(this::loadLogicalResource);

		return loading;
	}

	/**
	 * Снятие нагрузки
	 *
	 * @param loading наряд на бронирование
	 * @return истина, если успешно снято бронирование, иначе ложь
	 */
	public boolean releaseLoading(ResourceLoading loading) {
		//Установим номерам статус IPAddressState.AVAILABLE что бы их можно было выбирать снова
		loading.getLoadedLogicalResource().forEach(lr -> {
			unloadLogicalResource(lr);
			lr.setResourceLoading(null);
		});
		return resourceLoadingRepository.release(loading.getId());
	}

	/**
	 * Все нагрузки по услуге
	 *
	 * @param serviceInstance услуга
	 * @return список нагрузок
	 */
	public List<ResourceLoading> loadAllLoadingsByService(Service serviceInstance) {
		return resourceLoadingRepository.loadAllLoadingsByService(serviceInstance);
	}

	/**
	 * Нагрузить логический ресурс
	 * @param lr логический ресурс
	 */
	private void loadLogicalResource(LogicalResource lr) {
		switch (lr.getType()) {
			case IP_ADDRESS: loadIpAddress(lr); break;
			case PHONE_NUMBER: loadPhoneNumber(lr); break;
			default: throw new IllegalStateException("Logical resource is not supported");
		}
	}

	/**
	 * Разгрузить логический ресурс
	 * @param lr логический ресурс
	 */
	private void unloadLogicalResource(LogicalResource lr) {
		switch (lr.getType()) {
			case IP_ADDRESS: unloadIpAddress(lr); break;
			case PHONE_NUMBER: unloadPhoneNumber(lr); break;
			default: throw new IllegalStateException("Logical resource is not supported");
		}
	}

	/**
	 * Нагрузить ip-адрес
	 * @param lr логический ресурс
	 */
	private void loadIpAddress(LogicalResource lr) {
		IPAddress ip = (IPAddress) lr;
		lifecycleService.performRouting(ip, IPAddressLifecycle.Routes.OCCUPY);
		ipAddressRepository.save(ip);
	}

	/**
	 * Нагрузить телефонный номер
	 * @param lr логический ресурс
	 */
	private void loadPhoneNumber(LogicalResource lr) {
		PhoneNumber pn = (PhoneNumber) lr;
		lifecycleService.performRouting(pn, PhoneNumberLifecycle.Routes.OCCUPY);
		phoneNumberRepository.save(pn);
	}

	/**
	 * Разгрузить ip-адрес
	 * @param lr логический ресурс
	 */
	private void unloadIpAddress(LogicalResource lr) {
		IPAddress ip = (IPAddress) lr;
		lifecycleService.performRouting(ip, IPAddressLifecycle.Routes.UNLOCK);
		ipAddressRepository.save(ip);
	}

	/**
	 * Разгрузить телефонный номер
	 * @param lr логический ресурс
	 */
	private void unloadPhoneNumber(LogicalResource lr) {
		PhoneNumber pn = (PhoneNumber) lr;
		lifecycleService.performRouting(pn, PhoneNumberLifecycle.Routes.LOCK);
		phoneNumberRepository.save(pn);
	}

	/**
	 * Получить все ресурсы нагруженные услугой
	 * @param service услуга
	 * @return список нагруженных услугой ресурсов
	 */
	public List<LogicalResourceDto> loadAllLoadedResourcesByService(Service service) {
		List<ResourceLoading> loadings = loadAllLoadingsByService(service);

		return loadings.stream()
				.flatMap(loading -> loading.getLoadedLogicalResource().stream())
				.map(logicalResourceDtoTranslator::translate)
				.collect(Collectors.toList());
	}
}
