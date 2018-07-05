package ru.argustelecom.box.nri.logicalresources.ip.subnet;

import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.model.IPSubnet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий сетей
 *
 * @author a.wisniewski
 * @since 12.12.2017
 */
@Repository
public class IPSubnetRepository implements Serializable {

	public static final long serialVersionUID = 1L;

	/**
	 * Энтити мэнэджер
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * находит сеть по id
	 *
	 * @param subnetId id
	 * @return сеть
	 */
	public IPSubnet findOne(Long subnetId) {
		return em.find(IPSubnet.class, subnetId);
	}

	/**
	 * находит сеть по id и обновляеть её из БД
	 *
	 * @param subnetId id
	 * @return сеть
	 */
	public IPSubnet findAndLoadFomBDOne(Long subnetId) {
		IPSubnet sub = em.find(IPSubnet.class, subnetId);
		if (sub != null)
			em.refresh(sub);
		return sub;
	}


	/**
	 * Найти все подсети
	 *
	 * @return список подсетей
	 */
	public List<IPSubnet> findAll() {
		return em.createQuery("FROM IPSubnet s", IPSubnet.class).getResultList();
	}

	/**
	 * Получить все "верхние" подсети
	 *
	 * @return список подсетей
	 */
	@SuppressWarnings("unchecked")
	public List<IPSubnet> findOnlyParentSubnets() {
		return em.createQuery("FROM IPSubnet subnet WHERE subnet.parent IS NULL", IPSubnet.class).getResultList();
	}

	/**
	 * ищет сеть по имени
	 *
	 * @param subnetName имя
	 * @return сеть, либо null
	 */
	public IPSubnet findByName(String subnetName) {
		return em.createQuery("FROM IPSubnet WHERE name = :subnetName", IPSubnet.class)
				.setParameter("subnetName", subnetName)
				.getResultList()
				.stream().findFirst().orElse(null);
	}

	/**
	 * удаляет сеть
	 *
	 * @param subnet сеть
	 */
	public void remove(IPSubnet subnet) {
		em.remove(subnet);
	}

	/**
	 * Перенести подсети  из сети subnetFrom в сеть subnetTo
	 *
	 * @param subnetFrom Откуда
	 * @param subnetTo   куда
	 */
	public void rebaseChildrenSubnet(IPSubnet subnetFrom, IPSubnet subnetTo) {
		for (IPSubnet sub : subnetFrom.getChildSubnets()) {
			sub.setParent(subnetTo);
			if (subnetTo != null)
				subnetTo.getChildSubnets().add(sub);
			em.merge(sub);
		}
		subnetFrom.getChildSubnets().clear();
		em.merge(subnetFrom);
		if (subnetTo != null)
			em.merge(subnetTo);
	}

	/**
	 * флашит контекст
	 */
	public void flush() {
		em.flush();
	}

	/**
	 * Создать подсеть
	 *
	 * @param subnet подсеть
	 */
	public void persist(IPSubnet subnet) {
		em.persist(subnet);
		em.flush();
	}

	/**
	 * возвращает потенциальных детей для еще не заведенной сети
	 * Логика: детьми для новой сети будут все дети, которые:
	 * 1) входят в новую сеть
	 * 2) у которых отец либо отсутствует, либо отец шире новой сети (новая сеть входит в отца)
	 *
	 * @param subnet новая сеть
	 * @return список потенциальных детей
	 */
	@SuppressWarnings("unchecked") // ругается на каст, хотя мы явно передаем тип
	public List<IPSubnet> getPossibleChildSubnets(String subnet) {
		return em.createNativeQuery("SELECT subnet.* FROM nri.ip_subnet subnet "
						+ "LEFT JOIN nri.ip_subnet parent_subnet ON subnet.parent_subnet_id = parent_subnet.id "
						+ "WHERE CAST(subnet.name AS INET) << CAST(:subnet AS INET) AND "
						+ "(subnet.parent_subnet_id IS NULL OR CAST(:subnet AS INET) << CAST(parent_subnet.name AS INET))",
				IPSubnet.class)
				.setParameter("subnet", subnet)
				.getResultList();
	}

	/**
	 * возвращает ближайшего отца
	 *
	 * @param subnet сеть
	 * @return ближайший отец, либо null, если такового нет
	 */
	@SuppressWarnings("unchecked") // ругается на cast, хотя мы явно передаем тип
	public IPSubnet getClosestParent(String subnet) {
		Optional<IPSubnet> closestParent = (Optional<IPSubnet>) em.createNativeQuery(
				"SELECT * FROM nri.ip_subnet subnet " + "WHERE cast(:sub AS INET) << cast(name AS INET) "
						+ "ORDER BY masklen(cast(name AS INET)) DESC LIMIT 1", IPSubnet.class)
				.setParameter("sub", subnet).getResultList().stream().findFirst();
		return closestParent.orElse(null);
	}

	/**
	 * возвращает айпишники, которые являются прямыми детьми для переданной сети
	 *
	 * @param subnetName сеть
	 * @return айпишники - прямые дети
	 */
	@SuppressWarnings("unchecked") // ругается на cast, хотя мы явно передаем тип
	public List<IPAddress> getIpsThatDoesntBelongToInnerSubnets(String subnetName) {
		return em.createNativeQuery("SELECT * FROM nri.ip_address ip "
						+ "LEFT JOIN nri.ip_subnet subnet ON ip.parent_subnet_id = subnet.id "
						+ "WHERE cast(ip.name AS INET) << cast(:subnet AS INET) AND "
						+ "cast(:subnet AS INET) << cast(subnet.name AS INET) AND "
						+ "ip.ip_address_state != 'DELETED'",
				IPAddress.class)
				.setParameter("subnet", subnetName)
				.getResultList();
	}

	/**
	 * Изменить комментарий к подсети
	 *
	 * @param subnetId   ID подсети
	 * @param newComment новый комментарий
	 */
	public void changeComment(Long subnetId, String newComment) {
		IPSubnet subnet = em.find(IPSubnet.class, subnetId);
		if (subnet != null) {
			subnet.setComment(newComment);
			em.merge(subnet);
		}
	}
}