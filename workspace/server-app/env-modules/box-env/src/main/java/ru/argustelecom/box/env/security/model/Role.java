package ru.argustelecom.box.env.security.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
@NamedQuery(name = Role.GET_ALL_ROLES, query = "select r from Role r")
public class Role extends BusinessDirectory {

	public static final String GET_ALL_ROLES = "Role.getAllRoles";

	@Size(max = DESCRIPTION_LENGTH)
	@Column(length = DESCRIPTION_LENGTH)
	private String description;

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	private RoleStatus status = RoleStatus.ACTIVE;

	//@formatter:off
	@ManyToMany
	@JoinTable(schema = "system", name = "role_permissions", 
		joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"))
	private List<Permission> permissions = new ArrayList<>();
	//@formatter:on

	@Column(name = "is_sys", nullable = false)
	private boolean sys;

	protected Role() {
	}

	public Role(Long id) {
		super(id);
	}

	@Override
	@Size(max = OBJECT_NAME_LENGTH)
	@Access(AccessType.PROPERTY)
	@Column(name = "name", length = OBJECT_NAME_LENGTH)
	public String getObjectName() {
		return super.getObjectName();
	}

	public boolean isSys() {
		return sys;
	}

	public void setSys(boolean sys) {
		this.sys = sys;
	}

	@Override
	public Boolean getIsSys() {
		return sys;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RoleStatus getStatus() {
		return status;
	}

	public void setStatus(RoleStatus status) {
		this.status = status;
	}

	public List<Permission> getPermissions() {
		return Collections.unmodifiableList(permissions);
	}

	public void addPermission(Permission permission) {
		if (!permissions.contains(permission))
			permissions.add(permission);
	}

	public boolean removePermission(Permission permission) {
		return permissions.remove(permission);
	}

	public void updatePermissions(Set<Permission> actualPermissions) {
		// Выкинуть привилегии, которых нет в целевом списке
		Iterator<Permission> it = permissions.iterator();
		while (it.hasNext()) {
			Permission permission = it.next();
			if (!actualPermissions.contains(permission)) {
				it.remove();
			}
		}

		// Добавить привилегии которых не хватает по сравнению с целевым списком
		actualPermissions.forEach(permission -> {
			if (!permissions.contains(permission)) {
				permissions.add(permission);
			}
		});
	}

	public static class RoleQuery extends EntityQuery<Role> {
		private	EntityQueryStringFilter<Role> name = createStringFilter(Role_.objectName);
		private	EntityQueryStringFilter<Role> desc = createStringFilter(Role_.description);

		public RoleQuery() {
			super(Role.class);
		}

		public EntityQueryStringFilter<Role> name() {
			return name;
		}

		public EntityQueryStringFilter<Role> desc() {
			return desc;
		}
	}

	private static final long serialVersionUID = -5655851014742642152L;
	public static final int OBJECT_NAME_LENGTH = 100;
	public static final int DESCRIPTION_LENGTH = 250;
}
