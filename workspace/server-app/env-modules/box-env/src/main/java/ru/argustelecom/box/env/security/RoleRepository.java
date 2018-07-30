package ru.argustelecom.box.env.security;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;

@Repository
public class RoleRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@NamedQuery(name = ALL_ROLES_QUERY_NAME, query = "from Role order by objectName")
	public List<Role> queryAllRoles() {
		return em.createNamedQuery(ALL_ROLES_QUERY_NAME, Role.class).getResultList();
	}

	private static final String ALL_ROLES_QUERY_NAME = "RoleRepository.queryAllRoles";

	// TODO Заменить на полнотекстовый поиск
	@NamedQuery(name = ROLES_QUERY_NAME, query = "from Role r where upper(r.objectName) like :query or upper(r.description) like :query order by objectName")
	public List<Role> queryRoles(String query) {
		if (Strings.isNullOrEmpty(query)) {
			return Collections.emptyList();
		}

		String preparedQuery = query.trim().toUpperCase();
		if (!preparedQuery.startsWith("%")) {
			preparedQuery = "%" + preparedQuery;
		}
		if (!preparedQuery.endsWith("%")) {
			preparedQuery += "%";
		}
		return em.createNamedQuery(ROLES_QUERY_NAME, Role.class).setParameter("query", preparedQuery).getResultList();
	}

	public Role createRole(String roleName, String roleDescription) {
		Role role = new Role(idSequence.nextValue(Role.class));
		role.setObjectName(roleName);
		role.setDescription(roleDescription);
		em.persist(role);

		return role;
	}

	public void removeRole(Role role) throws BusinessExceptionWithoutRollback {
		if (role.isSys()) {
			throw new BusinessExceptionWithoutRollback(
					format("Невозможно удалить системную роль {0}", role.getObjectName()));
		}
		em.remove(role);
	}

	private static final String ROLES_QUERY_NAME = "RoleRepository.queryRoles";
	private static final long serialVersionUID = -5915068789035193093L;
}
