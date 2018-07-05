package ru.argustelecom.box.env.personnel;

import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployee;
import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper.EMAIL;
import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper.LAST_NAME;
import static ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper.USER_NAME;
import static ru.argustelecom.box.env.personnel.EmployeeListViewState.EmployeeFilter.SEARCH_QUERY;

import java.util.Map;

import javax.inject.Inject;

import ru.argustelecom.box.env.BaseJPQLConvertibleDtoFilterModel;
import ru.argustelecom.box.env.party.PartyRepository.LoginEmployeeQueryWrapper;
import ru.argustelecom.box.env.util.QueryWrapper;
import ru.argustelecom.system.inf.page.PresentationModel;

;

@PresentationModel
public class EmployeeFilterModel extends BaseJPQLConvertibleDtoFilterModel<LoginEmployee, LoginEmployeeQueryWrapper> {

	@Inject
	private EmployeeListViewState employeeListViewState;

	private LoginEmployeeQueryWrapper queryWrapper;

	@Override
	public void buildPredicates(QueryWrapper<LoginEmployee> queryWrapper) {
		Map<String, Object> filterMap = employeeListViewState.getFilterMap();
		for (Map.Entry<String, Object> filterEntry : filterMap.entrySet()) {
			if (filterEntry != null) {
				switch (filterEntry.getKey()) {
				case SEARCH_QUERY:
					String formatted = String.format("%%%s%%", filterEntry.getValue());
					queryWrapper.or(queryWrapper.like(USER_NAME, formatted),
							queryWrapper.like(EMAIL, formatted),
							queryWrapper.like(LAST_NAME, formatted));
					break;
				}
			}
		}
	}

	@Override
	public LoginEmployeeQueryWrapper getQueryWrapper(boolean isNew) {
		if (isNew) {
			return queryWrapper = new LoginEmployeeQueryWrapper();
		}
		return queryWrapper;
	}

	private static final long serialVersionUID = 5491758901329434618L;
}
