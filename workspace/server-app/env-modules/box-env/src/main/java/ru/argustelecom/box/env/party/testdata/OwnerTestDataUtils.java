package ru.argustelecom.box.env.party.testdata;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.OwnerRepository;
import ru.argustelecom.box.env.party.PartyCategory;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.box.env.party.model.role.Owner;

import static ru.argustelecom.box.env.util.UITestUtils.getOrElse;
import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class OwnerTestDataUtils implements Serializable {

	private static final long serialVersionUID = -7459101372293783233L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private OwnerRepository ownerRp;

	@Inject
	private PartyTypeTestDataUtils partyTypeTestDataUtils;

	/**
	 * Возвращает первого найденного владельца {@link Owner}, если ничего не найдено, создает владельца с указанным
	 * именем
     * @param principal Параметр, определяющий, является ли владелец основным или нет
	 * @return Владелец
	 */
	public Owner findOrCreateTestOwner(boolean principal) {

		Owner.OwnerQuery query = new Owner.OwnerQuery();
		List<Owner> availableOwners = query.and(
				query.principal().equal(principal)
		).getResultList(em);

		return getOrElse(availableOwners, () -> createTestOwner(principal));
	}

	public Owner findOrCreateTestOwner() {
		return findOrCreateTestOwner(true);
	}

	/**
	 * Создает и возвращает тестового владельца
	 * @param principal Параметр, определяющий, является ли владелец основным или нет
     * @return Тестовый владелец
	 */
	public Owner createTestOwner(boolean principal) {
		PartyType partyType = partyTypeTestDataUtils.findOrCreateTestPartyType(PartyCategory.COMPANY);
		return ownerRp.createOwner(
		        partyType, uniqueId("Тестовая компания"), BigDecimal.TEN, null, principal
        );
	}
}