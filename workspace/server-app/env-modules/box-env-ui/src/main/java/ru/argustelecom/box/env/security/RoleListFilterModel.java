package ru.argustelecom.box.env.security;

import static ru.argustelecom.box.env.security.RoleListViewState.RoleFilter;
import static ru.argustelecom.box.env.security.model.Role.RoleQuery;

import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class RoleListFilterModel extends BaseEQConvertibleDtoFilterModel<RoleQuery> {

	@Inject
	private RoleListViewState roleListViewState;

	@Override
	public void buildPredicates(RoleQuery query) {
		Map<String, Object> filterMap = roleListViewState.getFilterMap();
		for (Map.Entry<String, Object> filterEntry : filterMap.entrySet()) {
			if (filterEntry != null) {
				switch (filterEntry.getKey()) {
				case RoleFilter.SEARCH_QUERY: {
					String pattern = String.format("%%%s%%", filterEntry.getValue());
					query.or(query.name().like(pattern), query.desc().like(pattern));
					break;
				}
				}
			}
		}
	}

	@Override
	public Supplier<RoleQuery> entityQuerySupplier() {
		return RoleQuery::new;
	}

	private static final long serialVersionUID = -8906284966574818571L;
}
