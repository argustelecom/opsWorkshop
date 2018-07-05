package ru.argustelecom.box.env.party;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.party.PartyCategory.COMPANY;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.Party;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.box.env.party.model.role.Supplier;
import ru.argustelecom.box.env.party.model.role.Supplier.SupplierQuery;
import ru.argustelecom.box.inf.service.Repository;

/**
 * Репозиторий для работы с {@linkplain ru.argustelecom.box.env.party.model.role.Supplier внешним-оператором
 * связи(контрагентом)}.
 */
@Repository
public class SupplierRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService sequenceSrv;

	@Inject
	private PartyRepository partyRp;

	/**
	 * Создаёт поставщика.
	 *
	 * @param name
	 *            официальное название компании поставщика.
	 * @param partyTypeId
	 *            идентификатор типа компании поставщика. Поставщика можно создать только с тем типом, который относится
	 *            к категории {@link PartyCategory#COMPANY}
	 */
	public Supplier create(String name, String brandName, Long partyTypeId) {
		checkNotNull(name, "Name is required attribute for supplier creation");
		checkNotNull(partyTypeId, "Party type is required attribute for supplier creation");

		PartyType partyType = em.find(PartyType.class, partyTypeId);
		checkNotNull(partyType);

		checkState(COMPANY.equals(partyType.getCategory()),
				"The party type '%s' has category which is not equals to '%s'", partyType, COMPANY);

		Supplier instance = new Supplier(sequenceSrv.nextValue(Supplier.class));
		partyRp.createCompany(name, brandName, null, partyType, instance);

		em.persist(instance);

		return instance;
	}

	/**
	 * Возвращает список всех поставщиков.
	 */
	public List<Supplier> findAll() {
		return new SupplierQuery<>(Supplier.class).getResultList(em);
	}

	/**
	 * Ищет поставщика по {@linkplain Party компании}.
	 * 
	 * @param party
	 *            компания, которая является поставщиком.
	 * @return поставщика, в случае если по данной компании поставщик не найден - null
	 */
	public Supplier find(Party party) {
		checkNotNull(party);

		SupplierQuery<Supplier> query = new SupplierQuery<>(Supplier.class);
		query.and(query.party().equal(party));
		return query.getSingleResult(em, false);
	}

	/**
	 * Ищет поставщиков по имени компании.
	 *
	 * @param legalName
	 *            имя компании.
	 * @return коллекция поставщиков, в случае, если по переданному имени, поставщии не найдены - пустая коллекция
	 */
	public List<Supplier> find(String legalName) {
		checkNotNull(legalName);

		SupplierQuery<Supplier> query = new SupplierQuery<>(Supplier.class);
		query.and(query.byLegalName(legalName));
		return query.getResultList(em);
	}

	private static final long serialVersionUID = 5954997039330734964L;

}