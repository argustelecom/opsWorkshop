package ru.argustelecom.box.env.address;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.address.AddressQueryResult.ADDRESS_QUERY_RESULT_MAPPER;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

@DomainService
public class AddressSearchService {

	private static final String SEARCH_ADDRESSES = "AddressSearchService.searchAddress";

	@PersistenceContext
	private EntityManager em;

	//@formatter:off
	@NamedNativeQuery(name = SEARCH_ADDRESSES, resultSetMapping = ADDRESS_QUERY_RESULT_MAPPER,
			query = "SELECT location_id, location_class, tree_display_name " +
					"FROM address_search.search_locations(:rawInput, :maxResults, " +
					"CAST (:searchLevel AS address_search.t_search_level)) ")
	public List<AddressQueryResult> searchAddress(String rawInput, int maxResults, SearchLevel searchLevel) {
		checkNotNull(rawInput);
		checkNotNull(searchLevel);

		return em.createNamedQuery(SEARCH_ADDRESSES, AddressQueryResult.class)
				.setParameter("rawInput", rawInput)
				.setParameter("maxResults", maxResults)
				.setParameter("searchLevel", searchLevel.name())
				.getResultList();
	}
	//@formatter:on
}
