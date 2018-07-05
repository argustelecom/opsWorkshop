package ru.argustelecom.box.nri.logicalresources.phone;

import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Iterables.isEmpty;

/**
 * Репозиторий доступа телефонным номерам
 * Created by b.bazarov on 31.10.2017.
 */
@Repository
public class PhoneNumberRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Поиск по идентификатору
	 *
	 * @param id идентификатор
	 * @return телефонный номер
	 */
	public PhoneNumber findOne(Long id) {
		return em.find(PhoneNumber.class, id);
	}

	/**
	 * Найти телефонные номера по списку идентификаторов
	 * @param ids список идентификаторов
	 * @return список телефонных номеров
	 */
	public List<PhoneNumber> findMany(List<Long> ids) {
		Query query = em.createQuery("FROM LogicalResource lr WHERE lr.id IN (:ids)");
		query.setParameter("ids", ids);
		return query.getResultList();
	}

	/**
	 * Поиск по идентификатору с обновлением кеша
	 *
	 * @param id идентификатор
	 * @return телефонный номер
	 */
	public PhoneNumber findOneWithRefresh(Long id) {
		em.clear();
		return em.find(PhoneNumber.class, id);
	}

	/**
	 * Найти все телефонные номера
	 *
	 * @return список найденных номеров
	 */
	public List<PhoneNumber> findAll() {
		return em.createQuery("FROM PhoneNumber p", PhoneNumber.class).getResultList();
	}

	/**
	 * Найти все телефонные номера со статусом
	 *
	 * @param state статус
	 * @return список найденных номеров
	 */
	public List<PhoneNumber> findAllWithState(PhoneNumberState state) {
		return em.createQuery("FROM PhoneNumber p WHERE p.state = :state", PhoneNumber.class)
				.setParameter("state", state).getResultList();
	}

	/**
	 * Найти все цифровые "имена" телефонных номеров
	 *
	 * @return список цифровых "имен" телефонных номеров
	 */
	public List<String> findAllNotDeletedPhoneDigits() {
		return em.createQuery("SELECT p.digits FROM PhoneNumber p WHERE p.state != 'DELETED'", String.class).getResultList();
	}

	/**
	 * Найти телефонные номера по пулу, в котором он состоит
	 *
	 * @param pool пул для поиска
	 * @return список найденных номеров
	 */
	public List<PhoneNumber> findByPool(PhoneNumberPool pool) {
		return em.createQuery("FROM PhoneNumber p WHERE p.pool = :pool", PhoneNumber.class)
				.setParameter("pool", pool).getResultList();
	}

	/**
	 * Найти телефонные номера от .. до ..
	 * @param from от номера
	 * @param to до номера
	 * @return телефонные номера от номера до номера
	 */
	public List<PhoneNumber> findPhoneNumbers(String from, String to) {
		return em.createQuery("from PhoneNumber p WHERE name >= :from AND name <= :to", PhoneNumber.class)
				.setParameter("from", from)
				.setParameter("to", to)
				.getResultList();
	}

	/**
	 * Удалить телефонный номер
	 *
	 * @param phoneNumber номер для удаления
	 */
	public void remove(PhoneNumber phoneNumber) {
		em.createQuery("DELETE FROM PhoneNumber p WHERE p.id = :id").setParameter("id", phoneNumber.getId()).executeUpdate();
	}

	/**
	 * убирает связанный ресурс у номеров
	 *
	 * @param phoneNumberIds id номеров
	 */
	public void removeFromResource(List<Long> phoneNumberIds) {
		if (isEmpty(phoneNumberIds))
			return;
		em.createQuery("UPDATE PhoneNumber phone SET phone.resource = null WHERE phone.id IN :ids")
				.setParameter("ids", phoneNumberIds).executeUpdate();
	}

	/**
	 * Добавить номер к ресурсу
	 *
	 * @param resId   id ресурса
	 * @param phoneId id номера
	 */
	public void addToResource(Long resId, Long phoneId) {
		ResourceInstance res = em.find(ResourceInstance.class, resId);
		PhoneNumber phoneNumber = findOne(phoneId);
		phoneNumber.setResource(res);
		em.merge(phoneNumber);
	}

	/**
	 * Убирает связанный ресурс у одного номера
	 *
	 * @param phoneNumber номер телефона
	 */
	public void removeFromResource(PhoneNumber phoneNumber) {
		phoneNumber.setResource(null);
		em.merge(phoneNumber);
	}

	/**
	 * Сохраняет Телефонный номер
	 *
	 * @param number Телефонный номер
	 */
	public void save(PhoneNumber number) {
		em.merge(number);
	}

	/**
	 * Удалить несколько номеров
	 *
	 * @param ids ID удаляемых номеров
	 */
	public void removeSeveralNumbers(List<Long> ids) {
		if (!isEmpty(ids))
			em.createQuery("DELETE FROM PhoneNumber p WHERE p.id IN :ids").setParameter("ids", ids).executeUpdate();
	}

	/**
	 * Найти первый результат удовлетворящий заданным предикатам
	 * @param predicates предикаты
	 * @param eq запрос
	 * @param maxResults максимальное кол-во возвращаемых результатов
	 * @return первый удовлетворящий результат
	 */
	public List<PhoneNumber> findByPredicates(List<Predicate> predicates, EntityQuery<PhoneNumber> eq, int maxResults) {
		predicates.forEach(pr -> eq.and(pr));
		TypedQuery<PhoneNumber> typedQuery = eq.createTypedQuery(em);
		typedQuery.setMaxResults(maxResults);
		return typedQuery.getResultList();
	}
}