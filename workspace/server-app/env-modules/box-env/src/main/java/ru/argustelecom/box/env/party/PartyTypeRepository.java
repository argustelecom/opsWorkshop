package ru.argustelecom.box.env.party;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.box.env.party.model.PartyType.PartyTypeQuery;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.env.type.model.TypePropertyFilterContainer;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class PartyTypeRepository implements Serializable {

	private static final long serialVersionUID = 8889799560311350721L;
	private static final String ALL_PARTY_TYPES = "PartyTypeRepository.getAllPartyTypes";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TypeFactory typeFactory;

	public PartyType createPartyType(@NotNull String name, @NotNull PartyCategory category, String keyword,
			String description) {
		PartyType newPartyType = typeFactory.createType(PartyType.class);
		newPartyType.setName(name);
		newPartyType.setCategory(category);
		newPartyType.setKeyword(keyword);
		newPartyType.setDescription(description);
		em.persist(newPartyType);
		return newPartyType;
	}

	public List<PartyType> getAllPartyTypes() {
		return new PartyTypeQuery<>(PartyType.class).getResultList(em);
	}

	public TypePropertyFilterContainer createPartyTypePropertyFilters() {
		return typeFactory.createFilterContainer(getAllPartyTypes());
	}

	/**
	 * Возвращает список типов участников по {@linkplain PartyCategory категории}.
	 * 
	 * @param category
	 *            категория, к которой должны относиться типы.
	 */
	public List<PartyType> findPartyTypesBy(PartyCategory category) {
		PartyTypeQuery<PartyType> query = new PartyTypeQuery<>(PartyType.class);
		query.and(query.category().equal(category));
		return query.getResultList(em);
	}

}