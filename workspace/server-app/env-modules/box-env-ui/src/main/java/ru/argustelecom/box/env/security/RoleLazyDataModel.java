package ru.argustelecom.box.env.security;

import static ru.argustelecom.box.env.security.RoleLazyDataModel.RoleSort;
import static ru.argustelecom.box.env.security.model.Role.RoleQuery;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.env.security.model.Role_;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class RoleLazyDataModel extends EQConvertibleDtoLazyDataModel<Role, RoleListDto, RoleQuery, RoleSort> {

	@Inject
	private RoleListFilterModel roleListFilterModel;

	@Inject
	private RoleListDtoTranslator roleListDtoTranslator;

	@PostConstruct
	private void postConstruct() {
		initPaths();
	}

	private void initPaths() {
		addPath(RoleSort.id, query -> query.root().get(Role_.id));
		addPath(RoleSort.name, query -> query.root().get(Role_.objectName));
	}

	@Override
	protected Class<RoleSort> getSortableEnum() {
		return RoleSort.class;
	}

	@Override
	protected DefaultDtoTranslator<RoleListDto, Role> getDtoTranslator() {
		return roleListDtoTranslator;
	}

	@Override
	protected EQConvertibleDtoFilterModel<RoleQuery> getFilterModel() {
		return roleListFilterModel;
	}


	public enum RoleSort {
		id, name
	}

	private static final long serialVersionUID = -46293977722694515L;
}
