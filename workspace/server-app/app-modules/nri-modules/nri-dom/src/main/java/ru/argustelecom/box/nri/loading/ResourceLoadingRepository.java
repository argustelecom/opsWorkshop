package ru.argustelecom.box.nri.loading;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.loading.model.ResourceLoading;
import ru.argustelecom.box.nri.loading.nls.ResourceLoadingRepositoryMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Репозиторий доступа к нарядам на бронирование
 * Created by b.bazarov on 21.12.2017.
 */
@Repository
public class ResourceLoadingRepository implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * сервис lifecycle routing'a
	 */
	@Inject
	private LifecycleRoutingService lifecycleService;
	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Сервис генерации айдишников
	 */
	@Inject
	private IdSequenceService idSequenceService;

	/**
	 * Создать нагрузку
	 *
	 * @param resources       нагружаемые ресурсы
	 * @param serviceInstance услуга
	 * @return нагрузка
	 */
	public ResourceLoading createResourceLoading(Set<LogicalResource> resources, Service serviceInstance) {
		ResourceLoading loading = ResourceLoading.builder()
				.id(idSequenceService.nextValue(BookingOrder.class))
				.loadedLogicalResource(resources)
				.serviceInstance(serviceInstance)
				.build();
		em.persist(loading);

		for (LogicalResource logicalResource : loading.getLoadedLogicalResource()) {
			logicalResource.setResourceLoading(loading);
			em.merge(logicalResource);
		}
		em.flush();
		return loading;
	}

	/**
	 * Снятие нагрузки
	 *
	 * @param id нагрузка
	 * @return
	 */
	public boolean release(Long id) {
		ResourceLoading loading = em.find(ResourceLoading.class, id);

		if (loading == null) {
			return false;
		}
		em.remove(loading);

		return true;
	}

	/**
	 * Найти вснагрузку по услуге
	 *
	 * @param serviceInstance услуга
	 * @return список нагрузок
	 */
	public List<ResourceLoading> loadAllLoadingsByService(Service serviceInstance) {
		return em.createQuery("From ResourceLoading rl WHERE rl.serviceInstance = :service", ResourceLoading.class)
				.setParameter("service", serviceInstance)
				.getResultList();
	}

	/**
	 * Найти нагрузку по ресурсу
	 *
	 * @param id идентификатор ресурса
	 * @return нагрузка
	 */
	public ResourceLoading findLoadingByResource(Long id) {
		LogicalResource logicalResource = em.find(LogicalResource.class, id);
		if (logicalResource == null) {
			ResourceLoadingRepositoryMessagesBundle messages = LocaleUtils.getMessages(ResourceLoadingRepositoryMessagesBundle.class);
			throw new IllegalStateException(messages.couldNotFindResourceWithId() + id);
		}
		return logicalResource.getResourceLoading();
	}
}
