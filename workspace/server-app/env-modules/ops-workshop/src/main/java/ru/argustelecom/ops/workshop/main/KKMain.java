package ru.argustelecom.ops.workshop.main;

import ru.argustelecom.ops.workshop.model.ApplicationServerStatus;
import ru.argustelecom.ops.workshop.model.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author k.koropovskiy
 */
public class KKMain {
	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("PersistenceUnitKK");
		try {

			EntityManager em = emf.createEntityManager();
			try {
				em.getTransaction().begin();

				Team team = new Team("hibernate", "HB");
				em.persist(team);

				System.out.printf("ID=%s", team.getId());
				em.getTransaction().commit();
			} finally {
				em.close();
			}
		} finally {
			emf.close();

		}

	}
}
