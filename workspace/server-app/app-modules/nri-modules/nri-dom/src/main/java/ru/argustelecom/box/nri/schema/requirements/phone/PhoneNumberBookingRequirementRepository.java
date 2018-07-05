package ru.argustelecom.box.nri.schema.requirements.phone;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.phone.model.PhoneNumberBookingRequirement;
import ru.argustelecom.box.nri.schema.requirements.phone.nls.PhoneNumberBookingRequirementRepositoryMessagesBundle;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Репозиторий для требований к телефонным номерам
 * Created by b.bazarov on 01.02.2018
 */
@Repository
public class PhoneNumberBookingRequirementRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис генерации ID
	 */
	@Inject
	private IdSequenceService idSequenceService;
	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Создать требование
	 *
	 * @param name     имя
	 * @param schemaId схема
	 * @return требование
	 */
	public PhoneNumberBookingRequirement create(String name, Long schemaId) {
		ResourceSchema schema = em.find(ResourceSchema.class, schemaId);
		if (schema == null) {
			throw new IllegalStateException(LocaleUtils.getMessages(PhoneNumberBookingRequirementRepositoryMessagesBundle.class).couldNotFindSchemaById()
					+ schemaId + LocaleUtils.getMessages(PhoneNumberBookingRequirementRepositoryMessagesBundle.class).forRequirement() + name);
		}
		PhoneNumberBookingRequirement requirement = PhoneNumberBookingRequirement.builder()
				.id(idSequenceService.nextValue(PhoneNumberBookingRequirement.class))
				.name(name)
				.schema(schema)
				.build();
		schema.getBookings().add(requirement);
		em.persist(requirement);
		em.merge(schema);

		return requirement;
	}

	/**
	 * Найти по идентификатору
	 *
	 * @param id идентификатор
	 * @return требование к телефонному номеру
	 */
	public PhoneNumberBookingRequirement findById(Long id) {
		return em.find(PhoneNumberBookingRequirement.class, id);
	}

	/**
	 * Удалить
	 *
	 * @param id идентификатор
	 * @return результат
	 */
	public Boolean remove(Long id) {
		PhoneNumberBookingRequirement requirement = findById(id);
		if (requirement == null) {
			return false;
		}

		ResourceSchema schema = requirement.getSchema();
		if (schema == null) {
			return false;
		}

		schema.getBookings().remove(requirement);
		em.merge(schema);

		em.remove(requirement);

		return true;
	}
}
