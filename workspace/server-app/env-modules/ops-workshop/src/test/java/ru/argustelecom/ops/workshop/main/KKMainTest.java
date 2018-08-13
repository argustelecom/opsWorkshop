package ru.argustelecom.ops.workshop.main;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.argustelecom.ops.workshop.model.Artifact;
import ru.argustelecom.ops.workshop.model.Customer;
import ru.argustelecom.ops.workshop.model.Product;
import ru.argustelecom.ops.workshop.model.Team;
import ru.argustelecom.ops.workshop.model.Teammate;
import ru.argustelecom.ops.workshop.model.UsageType;
import ru.argustelecom.ops.workshop.model.Version;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class KKMainTest {

	EntityManagerFactory emf;
	EntityManager em;

	@After
	public void tearDown() throws Exception {
		em.getTransaction().rollback();
		em.close();
		emf.close();
	}

	@Before
	public void setUp() throws Exception {
		emf = Persistence.createEntityManagerFactory("PersistenceUnitKK");
		em = emf.createEntityManager();
		em.getTransaction().begin();
	}

	@Test
	public void main() {
		Artifact ring = new Artifact("Кольцо всевластия","" );
		em.persist(ring);
		Artifact boring = new Artifact("Кольцо вселени","" );
		em.persist(boring);

		Version oldVersion = new Version("3.20.19");
		em.persist(oldVersion);
		Version version = new Version("3.20.20");
		em.persist(version);

		UsageType usageTypeTest = new UsageType("test", "Для тестирования");
		em.persist(usageTypeTest);
		UsageType usageTypeDev = new UsageType("dev", "Для разработки");
		em.persist(usageTypeDev);

		Product productNRI = new Product("Network Resource Inventory", "TASK", "ТУ/NRI");
		em.persist(productNRI);
		Product productTS = new Product("TechService", "TASK", "ТУ/NRI");
		em.persist(productTS);
		em.flush();

		Team teamNRI = new Team("NRI", "ТУ/NRI");
		teamNRI.addProduct(productNRI);
		em.persist(teamNRI);
		em.refresh(productNRI);
		Team teamTS = new Team("TS", "ТУ/NRI");
		teamTS.addProduct(productTS);
		em.persist(teamTS);
		em.refresh(productTS);

		Teammate t1 = new Teammate("Иванов И.И", "i.ivanov@argustelecom.ru", "i.ivanov");
		t1.addToTeam(teamNRI);
		em.persist(t1);
		Teammate t2 = new Teammate("Петрова И.И", "i.petrova@argustelecom.ru", "i.petrova");
		t2.addToTeam(teamNRI);
		t2.addToTeam(teamTS);
		em.persist(t2);
		Teammate t3 = new Teammate("Сидоров И.И", "i.sidorov@argustelecom.ru", "i.sidorov");
		t2.addToTeam(teamTS);
		em.persist(t3);

		em.flush();
		em.refresh(teamNRI);
		em.refresh(teamTS);

		Customer customer = new Customer("БигБосс", "ЗАО БольшеБоссов", "bigboss");
		customer.addProduct(productNRI);
		customer.addProduct(productTS);
		em.persist(customer);

		em.flush();
		em.refresh(productNRI);
		em.refresh(productTS);

//		ApplicationServerInstance asiTest = new ApplicationServerInstance(
//				ApplicationServerStatus.RUNNING, "testServer", "192.168.100.40", 0);
//		ApplicationServerInstance asiDev = new ApplicationServerInstance(
//				ApplicationServerStatus.RUNNING, "devServer", "192.168.100.40", 1);
//		asiTest.setCustomer(customer);
//		asiTest.setVersion(version);
//		asiTest.setUsageType(usageTypeTest);
//		asiDev.addTeam(teamNRI);
//		asiDev.addTeam(teamTS);
//		asiDev.setCustomer(customer);
//		asiDev.setVersion(version);
//		asiDev.setUsageType(usageTypeDev);
//
//		em.persist(version);
//		em.persist(usageTypeTest);
//		em.persist(usageTypeDev);
//		em.persist(t1);
//		em.persist(t2);
//		em.persist(t3);
//		em.persist(teamNRI);
//		em.persist(teamTS);
//		em.persist(customer);
//		em.persist(asiDev);
//		em.persist(asiTest);
		em.flush();
		System.out.println(productNRI.toString());
		System.out.println(productTS.toString());
	}
}