package ru.argustelecom.box.env.party;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class PartyTypeAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private PartyTypeRepository partyTypeRp;

	public List<PartyType> findPartyTypeByCategory(PartyCategory category) {
		checkNotNull(category);
		return partyTypeRp.findPartyTypesBy(category);
	}

	/**
	 * Возвращает все ключевые слова для указанного типа
	 * 
	 * @param partyTypeId
	 *            идентификатор типа участника
	 * @return все ключевые слова для указанного типа
	 */
	public Collection<String> getKeywords(Long partyTypeId) {
		return checkNotNull(em.find(PartyType.class, checkNotNull(partyTypeId))).getProperties().stream()
				.map(TypeProperty::getKeyword).collect(toList());
	}

	private static final long serialVersionUID = 73843527286318723L;
}