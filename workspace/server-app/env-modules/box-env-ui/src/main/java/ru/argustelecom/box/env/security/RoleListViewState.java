package ru.argustelecom.box.env.security;

import static ru.argustelecom.box.env.security.RoleListViewState.RoleFilter.SEARCH_QUERY;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.system.inf.page.PresentationState;

@PresentationState
@Getter
@Setter
public class RoleListViewState extends FilterViewState {
	@FilterMapEntry(SEARCH_QUERY)
	private String searchQuery;

	public static final class RoleFilter {
		public static final String SEARCH_QUERY = "SEARCH_QUERY";
	}

	private static final long serialVersionUID = 5848990166956005664L;
}
