package ru.argustelecom.box.env.security;

import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.security.model.Permission;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Named
@Repository
public class PermissionRepository {

	@PersistenceContext
	private EntityManager em;

	@NamedQuery(name = ALL_PERMISSIONS_QUERY_NAME, query = "from Permission")
	public List<Permission> queryAllPermissions() {
		return em.createNamedQuery(ALL_PERMISSIONS_QUERY_NAME, Permission.class).getResultList();
	}

	public Permission getPermission(@NotNull String permissionId) {
		return em.find(Permission.class, permissionId);
	}

	private static final String ALL_PERMISSIONS_QUERY_NAME = "PermissionRepository.queryAllPermissions";

}
