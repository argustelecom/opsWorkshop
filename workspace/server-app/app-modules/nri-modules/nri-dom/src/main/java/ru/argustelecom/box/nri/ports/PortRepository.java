package ru.argustelecom.box.nri.ports;

import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.ports.model.Port;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Репозиторйи доступа к портам
 */
@Repository
public class PortRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private transient EntityManager em;

	/**
	 * Получить все порты по ресурсу
	 *
	 * @param id идентификатор ресурса
	 * @return список портов ресурса
	 */
	public List<Port> loadAllPortsByResource(Long id) {
		if (id == null) {
			return Collections.emptyList();
		}

		return em.createQuery("from Port p where p.resource.id = :id")
				.setParameter("id", id)
				.getResultList();
	}

	/**
	 * Найти порт по id
	 *
	 * @param id id
	 * @return порт
	 */
	public Port findById(Long id) {
		return (Port) em.createQuery("from Port p where p.id = :id")
				.setParameter("id", id)
				.getSingleResult();
	}

	/**
	 * Сохранить изменения
	 *
	 * @param port порт
	 */
	public void save(Port port) {
		em.merge(port);
		em.flush();
	}

	/**
	 * Удалить портс ид
	 * @param id
	 */
	public void deletePort(Long id){
		Port port = em.find(Port.class,id);
		em.remove(port);
		em.flush();
	}
}
