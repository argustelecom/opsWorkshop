package ru.argustelecom.ops.env.home;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.ops.env.party.model.role.Employee;
import ru.argustelecom.system.inf.login.ArgusPrincipal;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class HomeViewModel extends ViewModel {

	private static final long serialVersionUID = 1L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ArgusPrincipal principal;

	private Employee employee;

	public Employee getEmployee() {
		if (employee == null)
			employee = em.find(Employee.class, principal.getWorkerId());
		return employee;
	}

}