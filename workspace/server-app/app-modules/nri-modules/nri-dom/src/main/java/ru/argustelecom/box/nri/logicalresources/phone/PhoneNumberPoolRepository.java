package ru.argustelecom.box.nri.logicalresources.phone;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Репозиторий доступа к пулу номеров
 * Created by s.kolyada on 31.10.2017.
 */
@Repository
public class PhoneNumberPoolRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Сервис генерации ID
	 */
	@Inject
	private IdSequenceService idSequenceService;

	/**
	 * Поиск по идентификатьору
	 *
	 * @param id идентификатор
	 * @return пул телефонных номеров
	 */
	public PhoneNumberPool findOne(Long id) {
		return em.find(PhoneNumberPool.class, id);
	}

	/**
	 * Поиск по идентификатору с обновлением кеша
	 *
	 * @param id идентификатор
	 * @return пул телефонных номеров
	 */
	public PhoneNumberPool findOneWithRefresh(Long id) {
		em.clear();
		return em.find(PhoneNumberPool.class, id);
	}

	/**
	 * Найти все пулы телефонных номеров
	 *
	 * @return список пулов телефонных номеров
	 */
	public List<PhoneNumberPool> findAll() {
		return em.createQuery("FROM PhoneNumberPool p", PhoneNumberPool.class).getResultList();
	}

	/**
	 * Проинициализировать новый пул в БД
	 *
	 * @param name    имя нового пула
	 * @param comment комментарий
	 * @return проинищиализированный пул
	 */
	public PhoneNumberPool create(String name, String comment) {
		PhoneNumberPool newPool = PhoneNumberPool.builder()
				.id(idSequenceService.nextValue(PhoneNumberPool.class))
				.name(name)
				.comment(comment)
				.build();
		em.persist(newPool);
		return newPool;
	}

	/**
	 * Удалить пул
	 *
	 * @param pool удаляемый пул
	 */
	public void remove(PhoneNumberPool pool) {
		em.createQuery("DELETE FROM PhoneNumberPool p WHERE p.id = :id").setParameter("id", pool.getId()).executeUpdate();
	}

	/**
	 * Сохранить изменения пула в БД
	 *
	 * @param pool измененный пул
	 */
	public void save(PhoneNumberPool pool) {
		em.merge(pool);
	}
}
