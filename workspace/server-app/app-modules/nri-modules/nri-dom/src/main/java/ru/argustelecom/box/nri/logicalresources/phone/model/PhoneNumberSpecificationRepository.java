package ru.argustelecom.box.nri.logicalresources.phone.model;

import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Репозиторий доступа к спецификациям телефонных номеров
 * Created by s.kolyada on 31.10.2017.
 */
@Repository
public class PhoneNumberSpecificationRepository implements Serializable {

	private static final long serialVersionUID = -8248219787526651978L;

	/**
	 * Запрос на получения всех спецификаций
	 */
	private static final String ALL_SPECS = "PhoneNumberSpecificationRepository.getAllSpecs";

	/**
	 * ЕМ
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Создать спецификацию
	 *
	 * @param name            имя
	 * @param description     описания
	 * @param newMask         маска
	 * @param blockedInterval срок блокировки номера
	 * @return созданная спека
	 */
	public PhoneNumberSpecification createPhoneNumberSpec(@NotNull String name, String description, String newMask, int blockedInterval) {
		PhoneNumberSpecification newPhonenumberSpec = new PhoneNumberSpecification(MetadataUnit.generateId(em));
		newPhonenumberSpec.setName(name);
		newPhonenumberSpec.setMask(newMask);
		newPhonenumberSpec.setBlockedInterval(blockedInterval);
		newPhonenumberSpec.setDescription(description);
		em.persist(newPhonenumberSpec);
		return newPhonenumberSpec;
	}

	/**
	 * Получить все спецификации телефонных номеров
	 *
	 * @return список со всеми спецификациями
	 */
	@NamedQuery(name = ALL_SPECS, query = "from PhoneNumberSpecification")
	public List<PhoneNumberSpecification> getAllSpecs() {
		return em.createNamedQuery(ALL_SPECS, PhoneNumberSpecification.class).getResultList();
	}

	/**
	 * Найти спецификацию
	 *
	 * @param id id
	 * @return спецификацию
	 */
	public PhoneNumberSpecification findOne(Long id) {
		return em.find(PhoneNumberSpecification.class, id);
	}
}
