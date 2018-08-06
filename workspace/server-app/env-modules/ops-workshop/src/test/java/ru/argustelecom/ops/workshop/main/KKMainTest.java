package ru.argustelecom.ops.workshop.main;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.argustelecom.ops.workshop.model.ApplicationServerInstance;
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
		Version version = new Version("3.20.20");
		UsageType usageTypeTest = new UsageType("test", "Для тестирования");
		UsageType usageTypeDev = new UsageType("dev", "Для разработки");
		Team teamNRI = new Team("NRI", "ТУ/NRI");
		Team teamTS = new Team("NRI", "ТУ/NRI");
		Teammate t1 = new Teammate("Иванов И.И", "i.ivanov@argustelecom.ru", "i.ivanov", teamNRI);
		Teammate t2 = new Teammate("Петрова И.И", "i.petrova@argustelecom.ru", "i.petrova", teamNRI);
		t2.addToTeam(teamTS);
		Teammate t3 = new Teammate("Сидоров И.И", "i.sidorov@argustelecom.ru", "i.sidorov", teamTS);
		Customer customer = new Customer("БигБосс", "ЗАО БольшеБоссов", "bigboss");
		Product productNRI = new Product("Network Resource Inventory", "TASK", "ТУ/NRI");
		Product productTS = new Product("TechService", "TASK", "ТУ/NRI");
		customer.addProduct(productNRI);
		customer.addProduct(productTS);
		teamNRI.addProduct(productNRI);
		teamTS.addProduct(productTS);
		ApplicationServerInstance asiTest = new ApplicationServerInstance(
				ApplicationServerInstance.ApplicationServerStatus.RUNNING, "testServer", "192.168.100.40", 0);
		ApplicationServerInstance asiDev = new ApplicationServerInstance(
				ApplicationServerInstance.ApplicationServerStatus.RUNNING, "devServer", "192.168.100.40", 1);
		asiTest.setCustomer(customer);
		asiTest.setVersion(version);
		asiTest.setUsageType(usageTypeTest);
		asiDev.addTeam(teamNRI);
		asiDev.addTeam(teamTS);
		asiDev.setCustomer(customer);
		asiDev.setVersion(version);
		asiDev.setUsageType(usageTypeDev);

		em.persist(version);
		em.persist(usageTypeTest);
		em.persist(usageTypeDev);
		em.persist(t1);
		em.persist(t2);
		em.persist(t3);
		em.persist(teamNRI);
		em.persist(teamTS);
		em.persist(customer);
		em.persist(asiDev);
		em.persist(asiTest);
		em.flush();
		em.refresh(productNRI);
		em.refresh(productTS);
		System.out.println(asiDev.toString());
		System.out.println(asiTest.toString());
	}
}