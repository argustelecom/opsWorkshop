package ru.argustelecom.box.env.billing.account;

import org.junit.Test;

import ru.argustelecom.system.inf.testframework.it.ui.navigation.LocationParameterProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.account.testdata.PersonalAccountProvider;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.graphene.page.InitialPage;

/**
 * @author v.semchenko
 */
@LoginProvider(providerClass = BoxLoginProvider.class)
public class PersonalAccountIT extends AbstractWebUITest implements LocationParameterProvider {

	/** На странице лицевого счета в таблице транзакций строки по умолчанию сортируется по дате. 
	 *  Поэтому в тесте {@link #shouldCreateTransaction(PersonalAccountPage, PersonalAccount)} данные о добавленной 
	 *  транзакции читаем из первой строки
	 */
	private static final int ROW_NUM_LAST_TRANSACTION = 0;
	
	@Override
	public Map<String, String> provideLocationParameters() {
		Map<String, String> params = new HashMap<>();

		PersonalAccount personalAccount = getTestRunContextProperty(
				PersonalAccountProvider.CREATED_PERSONAL_ACCOUNT_PROP_NAME, PersonalAccount.class);
		params.put("personalAccount", new EntityConverter().convertToString(personalAccount));

		return params;
	}

	/**
	 * Сценарий id = C100408 Создание транзакции
	 * <p>
	 * Предварительные условия: Открыта карточка лицевого счета, на которой требуется создать транзакцию.
	 * <p>
	 * Сценарий:
	 * <ol>
	 * <li>В блоке "Транзакции" нажать "Создать"
	 * <li>В диалоге ввести Сумму: 300.50
	 * <li>В диалоге выбрать Основание: "Кассовый ордер"
	 * <li>В диалоге ввести Название: "111111"
	 * <li>Нажать кнопку "Создать".
	 * <li>Проверить, что в блоке "Транзакции" появилась запись c данными о проведенной транзакции: Название основания -
	 * "111111", Тип основания - "Кассовый ордер", Сумма - "300.50"
	 * <li>Проверить, что баланс лицевого счета равен 300.50
	 * <p>
	 * Исполнитель: [v.semchenko]
	 */
	@Test
	public void shouldCreateTransaction(@InitialPage PersonalAccountPage page,
			@DataProvider(providerClass = PersonalAccountProvider.class, contextPropertyName = PersonalAccountProvider.CREATED_PERSONAL_ACCOUNT_PROP_NAME) PersonalAccount personalAccount) {

		page.openDialogCreateTransaction();
		page.setAmountOfMoney("300.50");
		page.chooseReasonType("Кассовый ордер");
		page.setReasonNumber("111111");
		page.clickOnButtonCreateTransaction();

		assertEquals("В таблице \"Транзакции\" нет транзакции с названием основания \"111111\": ", "111111",
				page.getReasonNumber(ROW_NUM_LAST_TRANSACTION));
		assertEquals("В таблице \"Транзакции\" у добавленной транзакции \"Тип основания\" не соответсвует выбранному: ",
				"Кассовый ордер", page.getReasonType(ROW_NUM_LAST_TRANSACTION));
		assertEquals("В таблице \"Транзакции\" у добавленной транзакции \"Сумма\" не соответсвует введеной: ", "300.50",
				page.getAmount(ROW_NUM_LAST_TRANSACTION));

		assertEquals("Баланс лицевого счета не равен 300.50: ", "300.50", page.getBalance());
	}

}