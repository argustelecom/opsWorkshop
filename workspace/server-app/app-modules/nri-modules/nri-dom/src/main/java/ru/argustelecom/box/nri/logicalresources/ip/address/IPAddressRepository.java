package ru.argustelecom.box.nri.logicalresources.ip.address;

import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.List;

/**
 * Репозиторий доступа к IP-адресам
 *
 * @author d.khekk
 * @since 11.12.2017
 */
@Repository
public class IPAddressRepository implements Serializable {

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
	 * @return IP-адрес
	 */
	public IPAddress findOne(Long id) {
		return em.find(IPAddress.class, id);
	}

	/**
	 * Поиск по идентификатору с обновлением кеша
	 *
	 * @param id идентификатор
	 * @return IP-адрес
	 */
	public IPAddress findOneWithRefresh(Long id) {
		em.clear();
		return findOne(id);
	}

	/**
	 * Создать новый IP-адрес
	 *
	 * @param ipAddress новый IP-адрес
	 * @return созданный IP-адрес
	 */
	public IPAddress create(IPAddress ipAddress) {
		em.persist(ipAddress);
		return ipAddress;
	}

	/**
	 * Сохранить изменения в сущности IP-адреса
	 *
	 * @param ipAddress измененный IP-адрес
	 */
	public void save(IPAddress ipAddress) {
		em.merge(ipAddress);
	}

	/**
	 * Найти список IP адресов, принадлежащих подсети
	 *
	 * @param subnetId ID подсети
	 * @return список IP адресов
	 */
	public List<IPAddress> findListIpBySubnetId(Long subnetId) {
		return em.createQuery("FROM IPAddress ip WHERE ip.subnet.id = :subnetId", IPAddress.class)
				.setParameter("subnetId", subnetId)
				.getResultList();
	}

	/**
	 * Найти первый результат удовлетворящий заданным предикатам
	 * @param predicates предикаты
	 * @param eq запрос
	 * @return первый удовлетворящий результат
	 */
	public IPAddress findFirstWithPredicates(List<Predicate> predicates,  EntityQuery<IPAddress> eq) {
		predicates.forEach(pr -> eq.and(pr));
		return eq.getFirstResult(em);
	}
}
