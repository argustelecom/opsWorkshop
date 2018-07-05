package ru.argustelecom.box.env.party;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.party.PartyCategory.COMPANY;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.Party;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.model.role.Owner.OwnerQuery;
import ru.argustelecom.box.env.party.nls.PartyMessagesBundle;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.exception.BusinessException;

/**
 * Репозиторий для работы с владельцами
 */
@Repository
public class OwnerRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService iss;

	@Inject
	private PartyRepository partyRp;

	/**
	 * Создает владельца, который представляет "Юридическое лицо компании"
	 * 
	 * @param partyType
	 *            тип участника с категорией {@link PartyCategory#COMPANY}
	 * @param legalName
	 *            имя компании, должно быть уникальным
	 * @param taxRate
	 *            НДС, задается в пределах от 0 до 100, включительно
	 * @param qrCodePattern
	 *            шаблон для QR-кода
	 * @param principal
	 *            признак уникальности юридического лица
	 * @return владельца
	 */
	public Owner createOwner(PartyType partyType, String legalName, BigDecimal taxRate, String qrCodePattern,
			boolean principal) {
		checkState(partyType != null && COMPANY.equals(partyType.getCategory()));
		checkNotNull(legalName);

		if (partyRp.hasCompanyWith(legalName)) {
			throw new BusinessException(getMessages(PartyMessagesBundle.class).companyNameShouldBeUnique());
		}

		Owner owner = new Owner(iss.nextValue(Owner.class), taxRate);
		owner.setParty(partyRp.createCompany(legalName, null, null, partyType, owner));
		owner.setQrCodePattern(qrCodePattern);

		if (principal) {
			makePrincipal(owner);
		}

		em.persist(owner);

		return owner;
	}

	/**
	 * Устанавливает владельцу признак юр. лица по-умолчанию, при этом снимая его с другого владельца в системе
	 *
	 * @param owner
	 *            владелец, которому необходимо установить признак юр. лица по-умолчанию
	 */
	public void makePrincipal(Owner owner) {
		checkNotNull(owner);

		if (owner.isPrincipal()) {
			return;
		}

		Owner currentPrincipalOwner = findPrincipal();

		if (currentPrincipalOwner != null) {
			currentPrincipalOwner.setPrincipal(false);
			em.flush();
		}

		owner.setPrincipal(true);
	}

	public List<Owner> findAll() {
		return new OwnerQuery().getResultList(em);
	}

	/**
	 * Возвращает основное юр. лицо компании.
	 */
	public Owner findPrincipal() {
		OwnerQuery query = new OwnerQuery();
		return query.and(query.principal().isTrue()).getSingleResult(em, false);
	}

	public Owner find(Party party) {
		checkNotNull(party);

		OwnerQuery ownerQuery = new OwnerQuery();
		ownerQuery.and(ownerQuery.party().equal(party));
		return ownerQuery.getSingleResult(em, false);
	}

	private static final long serialVersionUID = 8977000672172536590L;
}