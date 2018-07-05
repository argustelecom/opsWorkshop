package ru.argustelecom.box.env.personnel;

import static ru.argustelecom.box.env.security.RoleListViewState.RoleFilter.SEARCH_QUERY;

import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.system.inf.page.PresentationState;

@Named(value = "employeeListVS")
@PresentationState
@Getter
@Setter
public class EmployeeListViewState extends FilterViewState {

	@FilterMapEntry(SEARCH_QUERY)
	private String searchQuery;

	public static final class EmployeeFilter {
		public static final String SEARCH_QUERY = "SEARCH_QUERY";
	}

	private static final long serialVersionUID = -7160117817796622913L;
}