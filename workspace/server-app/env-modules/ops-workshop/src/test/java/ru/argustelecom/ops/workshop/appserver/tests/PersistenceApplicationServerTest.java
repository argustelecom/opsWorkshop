package ru.argustelecom.ops.workshop.appserver.tests;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Ignore;
import org.junit.Test;

import junit.framework.TestCase;
import ru.argustelecom.ops.env.idsequence.IdSequenceService;
import ru.argustelecom.ops.workshop.application.server.model.ApplicationServer;
import ru.argustelecom.ops.workshop.application.server.model.ApplicationServerState;
import ru.argustelecom.ops.workshop.customer.model.Customer;
import ru.argustelecom.ops.workshop.team.model.Team;
import ru.argustelecom.ops.workshop.product.model.Product;
import ru.argustelecom.ops.workshop.usagetype.model.UsageType;
import ru.argustelecom.ops.workshop.version.model.Version;

/**
 *
 *
 * @author v.semchenko
 */
public class PersistenceApplicationServerTest extends TestCase {

	private final String TEST_CUSTOMER_NAME = "Заказчик_1";

	/**
	 * тест проверки записи сущности AplicationServer в локальную БД h2.
	 */
	@Test
	public void test() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("PersistenceUnitKK");

		try {
			EntityManager em = emf.createEntityManager();

			em.getTransaction().begin();
			try {
				Product module = new Product("system-if");
				em.persist(module);

				Version argusVersion = new Version("3.20.31");
				em.persist(argusVersion);

				UsageType usageFor = new UsageType("Тестирование", "TEST");
				em.persist(usageFor);

				Team developerTeam = new Team("Команда Инфраструктуры", "АРГУС(TASK)");
				developerTeam.addProduct(module);
				em.persist(developerTeam);

				Customer customer = new Customer(TEST_CUSTOMER_NAME);
				em.persist(customer);

				ApplicationServer appServer = new ApplicationServer(ApplicationServerState.TURNED_OFF, "app-ktp",
						"jboss3", 0, "/jboss_prod");
				appServer.addTeam(developerTeam);
				appServer.setCustomer(customer);
				appServer.setVersion(argusVersion);
				appServer.setUsageType(usageFor);
				//запоминнаем объект и передаем его в управление EM
				em.persist(appServer);
				//flush() актуализируем данные в БД из нашего persistence context.
//				em.flush();
// 				em.refresh(module);

				System.out.println("[INFO] Сервер: " + appServer.toString() + "\n\n");
				System.out.println("[INFO] Команда разработчиков: " + developerTeam.toString() + "\n\n");
				System.out.println("[INFO] Заказчик: " + customer.toString() + "\n\n");
				System.out.println("[INFO] Модуль: " + module.toString() + "\n\n");

				em.getTransaction().commit();
			}catch(Exception e){
				e.printStackTrace();
			} finally {
				em.close();
			}
		}finally{
			emf.close();
		}
	}

}
