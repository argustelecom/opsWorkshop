package ru.argustelecom.box.nri.booking;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.booking.nls.BookingOrderRepositoryMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Репозиторий доступа к нарядам на бронирование
 * Created by s.kolyada on 19.12.2017.
 */
@Repository
public class BookingOrderRepository implements Serializable {

	private static final long serialVersionUID = 1L;

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
	 * Создать наряд на бронирование
	 *
	 * @param resources бронируемые ресурсы
	 * @param serviceInstance услуга
	 * @param requirement требование
	 * @return наряд на бронирование
	 */
	public BookingOrder createBookingOrder(Set<LogicalResource> resources, Service serviceInstance,
										   ResourceRequirement requirement) {
		BookingOrder order = BookingOrder.builder()
				.id(idSequenceService.nextValue(BookingOrder.class))
				.bookedLogicalResource(resources)
				.serviceInstance(serviceInstance)
				.requirement(requirement)
				.build();

		em.persist(order);
		for (LogicalResource logicalResource:order.getBookedLogicalResource()){
			logicalResource.setBookingOrder(order);
			em.merge(logicalResource);
		}
		em.flush();
		return order;
	}

	/**
	 * Снятие брони
	 *
	 * @param bookingOrder наряд на бронирование
	 * @return
	 */
	public boolean release(BookingOrder bookingOrder) {
		BookingOrder order = em.find(BookingOrder.class, bookingOrder.getId());

		if (order == null) {
			return false;
		}

		em.remove(order);
		for (LogicalResource logicalResource:order.getBookedLogicalResource()){
			logicalResource.setBookingOrder(null);
			em.merge(logicalResource);
		}
		em.flush();
		return true;
	}

	/**
	 * Найти бронь по id
	 *
	 * @param id идентификационный номер брони
	 * @return бронь
	 */
	public BookingOrder findOne(Long id) {
		return em.find(BookingOrder.class, id);
	}

	/**
	 * Список всех броней по услуге
	 *
	 * @param serviceInstance услуга
	 * @return список
	 */
	public List<BookingOrder> loadAllLoadingsByService(Service serviceInstance) {
		return em.createQuery("From BookingOrder bo WHERE bo.serviceInstance = :service", BookingOrder.class)
				.setParameter("service", serviceInstance)
				.getResultList();
	}

	/**
	 * Найти бронь по ресурсу
	 * @param id идентификатор ресурса
	 * @return бронь
	 */
	public BookingOrder findBookingByResource(Long id) {
		LogicalResource logicalResource = em.find(LogicalResource.class, id);
		if (logicalResource == null) {
			BookingOrderRepositoryMessagesBundle messages = LocaleUtils.getMessages(BookingOrderRepositoryMessagesBundle.class);
			throw new IllegalStateException(messages.didNotFindResourceWithId() + id);
		}
		return logicalResource.getBookingOrder();
	}

	/**
	 * Найти все брони по требованию
	 * @param br требование
	 * @return брони
	 */
	public List<BookingOrder> loadAllBookingsByRequirement(ResourceRequirement br){
		return em.createQuery("From BookingOrder bo WHERE bo.requirement =:requirement",BookingOrder.class)
				.setParameter("requirement",br).getResultList();
	}
}
