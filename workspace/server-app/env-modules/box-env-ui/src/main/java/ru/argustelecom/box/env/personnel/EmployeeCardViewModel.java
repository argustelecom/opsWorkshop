package ru.argustelecom.box.env.personnel;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import ru.argustelecom.box.env.party.CurrentPartyRole;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "employeeCardVM")
@PresentationModel
public class EmployeeCardViewModel extends ViewModel {

	private static final long serialVersionUID = -3735157077287872608L;

	private static final Logger log = Logger.getLogger(EmployeeCardViewModel.class);

	public static final String VIEW_ID = "/views/env/personnel/EmployeeCardView.xhtml";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CurrentPartyRole currentPartyRole;

	private Employee employee;

	private List<Role> possibleRoles;
	private DualListModel<Role> rolesDualList;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		initRoles();
		unitOfWork.makePermaLong();
	}

	protected void refresh() {
		checkNotNull(currentPartyRole.getValue(), "currentPartyRole required");
		if (currentPartyRole.changed(employee)) {
			employee = (Employee) currentPartyRole.getValue();
			log.debugv("postConstruct. employee_id={0}", employee.getId());
		}
	}

	public List<Role> getPossibleRoles() {
		if (possibleRoles == null) {
			possibleRoles = em.createNamedQuery(Role.GET_ALL_ROLES, Role.class).getResultList();
			possibleRoles.removeAll(employee.getRoles());
		}
		return possibleRoles;
	}

	@SuppressWarnings("unchecked")
	public void transferSelectedRoles(TransferEvent event) {
		if (event.isAdd()) {
			possibleRoles.removeAll(event.getItems());
			for (Object role : event.getItems()) {
				employee.addRole((Role) role);
			}
		} else {
			possibleRoles.addAll((List<Role>) event.getItems());
			for (Object role : event.getItems()) {
				employee.removeRole((Role) role);
			}
		}
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void initRoles() {
		rolesDualList = new DualListModel<>(getPossibleRoles(), employee.getRoles());
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Employee getEmployee() {
		return employee;
	}

	public DualListModel<Role> getRolesDualList() {
		return rolesDualList;
	}

	public void setRolesDualList(DualListModel<Role> rolesDualList) {
		this.rolesDualList = rolesDualList;
	}
}