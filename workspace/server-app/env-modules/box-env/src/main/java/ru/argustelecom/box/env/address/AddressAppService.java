package ru.argustelecom.box.env.address;

import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class AddressAppService {

	@Inject
	private AddressSearchService addressSearchSrv;

	@Inject
	private LocationRepository locationRp;

	public List<AddressQueryResult> searchAddress(String rawInput, int maxResults, SearchLevel searchLevel) {
		return addressSearchSrv.searchAddress(rawInput, maxResults, searchLevel);
	}

	public Boolean reindex(Long locationId) {
		return locationRp.reindex(locationId);
	}

	public Boolean reindexFull() {
		return locationRp.fullReindex();
	}
}
