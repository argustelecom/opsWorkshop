package ru.argustelecom.box.env.login.testdata;

import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.security.PermissionRepository;
import ru.argustelecom.box.env.security.RoleRepository;
import ru.argustelecom.box.env.security.model.Permission;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 *  Оставлено до лучших времен, до доработки параметризации LoginProvider
 */
public class EmployeePermissionsProvider implements TestDataProvider {

    public final static String DESIRED_PERMISSIONS = "role.provider.permissions";
    public final static String NOTHING = "role.provider.nothing";

    @PersistenceContext
    private EntityManager em;

    @Inject
    private PermissionRepository permissionRp;

    @Inject
    private RoleRepository roleRp;

    @Override
    public void provide(TestRunContext testRunContext) {

        Employee employee
                = (Employee) testRunContext.getBusinessPropertyWithUnmarshalling(BoxLoginProvider.CREATED_EMPLOYEE_PROP_NAME);

        List<String> desiredPermissions = testRunContext.getProviderParam(DESIRED_PERMISSIONS, List.class);

        addRoleForEmployee(employee, desiredPermissions);
    }

    /**
     * Добавить роль для Employee.Только перечисленные в параметре, если они есть, иначе - все
     * @param employee
     *            Employee, для которого требуется добавить правала
     */
    private void addRoleForEmployee(Employee employee, List<String> desiredPermissions) {
        List<Role> oldRoles = employee.getRoles();

        oldRoles.forEach(employee::removeRole);

        Role role;
        if (desiredPermissions == null) {
            role = new Role(1L);
        } else {
            role = findOrCreateTestRole();
            addDesiredPermissions(role, desiredPermissions);
        }

        employee.addRole(role);
        em.merge(employee);
    }

    private Role findOrCreateTestRole() {
        List<Role> allRoles = roleRp.queryAllRoles();
        if (!allRoles.isEmpty()) {
            return allRoles.get(0);
        }
        return roleRp.createRole("Тестовая роль", "Тестовое описание");
    }

    private void addDesiredPermissions(Role role, List<String> desiredPermissions) {
        Set<Permission> actualPermissions = new HashSet<>();

        desiredPermissions.forEach(permissionId -> {
            Permission permission = permissionRp.getPermission(permissionId);
            if (permission != null) {
                actualPermissions.add(permission);
            }
        });

        role.updatePermissions(actualPermissions);
    }
}