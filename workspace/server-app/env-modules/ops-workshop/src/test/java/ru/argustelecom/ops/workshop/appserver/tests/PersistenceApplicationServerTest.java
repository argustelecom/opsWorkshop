package ru.argustelecom.ops.workshop.appserver.tests;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


import org.junit.Test;

import junit.framework.TestCase;
import ru.argustelecom.ops.workshop.application.server.model.ApplicationServer;
import ru.argustelecom.ops.workshop.model.ApplicationServerStatus;
import ru.argustelecom.ops.workshop.model.Customer;
import ru.argustelecom.ops.workshop.model.Product;
import ru.argustelecom.ops.workshop.model.Team;
import ru.argustelecom.ops.workshop.model.UsageType;
import ru.argustelecom.ops.workshop.model.Version;

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
				Product module = new Product("system-if", "АРГУС", "Инфраструктура(java)");
				em.persist(module);

				Version argusVersion = new Version("3.20.31");
				em.persist(argusVersion);

				UsageType usageFor = new UsageType("Тестирование", "TEST");
				em.persist(usageFor);

				Team developerTeam = new Team("Команда Инфраструктуры", "Инфраструктура(java)");
				developerTeam.addProduct(module);
				em.persist(developerTeam);

				Customer customer = new Customer(TEST_CUSTOMER_NAME, TEST_CUSTOMER_NAME, "З_1");
				em.persist(customer);

				ApplicationServer appServer = new ApplicationServer(ApplicationServerStatus.SHUTDOWN, "app-ktp",
						"3.22.22", customer, argusVersion, usageFor, "новый сервер",
						"jboss3", 0, "/jboss_prod");
				appServer.addTeam(developerTeam);
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
