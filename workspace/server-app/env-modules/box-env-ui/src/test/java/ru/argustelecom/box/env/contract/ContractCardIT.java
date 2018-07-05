package ru.argustelecom.box.env.contract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.argustelecom.box.env.contract.model.ContractState.REGISTRATION;

import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;

import ru.argustelecom.box.env.contract.lifecycle.ContractLifecycle;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.testdata.ContractInforceProvider;
import ru.argustelecom.box.env.contract.testdata.ContractProvider;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.navigation.LocationParameterProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class ContractCardIT extends AbstractWebUITest implements LocationParameterProvider {

	@Override
	public Map<String, String> provideLocationParameters() {
		Map<String, String> params = new HashMap<>();

		Contract contract = getTestRunContextProperty(ContractProvider.CREATED_CONTRACT_PROP, Contract.class);
		if (testName.getMethodName().equals("shouldCreateContractExtension")) {
			contract = getTestRunContextProperty(ContractInforceProvider.CONTRACT, Contract.class);
		}
		params.put("contract", new EntityConverter().convertToString(contract));
		return params;
	}

	//@formatter:off
	/**
	 * Сценарий id = C269211 Редактирование атрибутов договора
	 * <p>
	 * Предварительные условия: Открыта карточка договора, находящегося в статусе "Оформление".
	 * Цель: проверить отсутствие ошибок при корректном изменении даты. Ввод осуществялется через поле ввода
	 * <p>
	 * Сценарий:
	 * <ol> В блоке "Атрибуты" договора ввести даты начала и окончания
	 * <ol> Проверяем:
	 * <ol> - дата начала < даты окончания
	 * <ol> - указана только дата начала
	 * <ol> - дата начала = даты окончания
	 * <li> Проверить что Внесенные изменения сохранены и отображены корректно.
	 * <p>
	 * Исполнитель: [a.isakov]
	 */
	@Test	
	public void shouldEditContractDateAttributes(
			@InitialPage ContractCardPage page,
			@DataProvider(
					providerClass = ContractProvider.class,
					contextPropertyName = ContractProvider.CREATED_CONTRACT_PROP
			) Contract contract
	) {
		//@formatter:on
		// Проверим первичный ввод дат
		page.setValidFrom("15.01.2017");
		page.setValidTo("29.01.2017");

		assertEquals("15.01.2017", page.validFrom.getValue());
		assertEquals("29.01.2017", page.validTo.getValue());

		// Проверим удаление одной из дат
		page.setValidFrom("15.01.2017");
		page.setValidTo("");

		assertEquals("15.01.2017", page.validFrom.getValue());
		assertEquals("", page.validTo.getValue());

		// Проверим случай когда дата начала равна дате завершения
		page.setValidFrom("29.01.2017");
		page.setValidTo("29.01.2017");

		assertEquals("29.01.2017", page.validFrom.getValue());
		assertEquals("29.01.2017", page.validTo.getValue());

		// см BOX-1882
		page.setValidFrom("01.01.2017");
		page.setValidTo("29.01.2017");

		page.setValidTo("29.08.2017");
		page.setValidFrom("01.08.2017");

		assertEquals("01.08.2017", page.validFrom.getValue());
		assertEquals("29.08.2017", page.validTo.getValue());
	}

	//@formatter:off
	/**
	 * Сценарий id = C100435 Аннулирование и удаление договора 
	 * <p>
	 * Предварительные условия: Открыта карточка договора, находящегося в статусе "Оформление". 
	 * Цель: Проверить аннулирование и удаление договора (дополнительно проверяем отмену действия) 
	 * <p>
	 * Сценарий:
	 * <ol> В блоке "Атрибуты" раскрыть выпадающий список рядом с кнопкой "Активировать".
	 * <ol> Выбрать пункт "Аннулировать".
	 * <ol> Подтвердить аннулирование.
	 * <ol> В блоке "Атрибуты" нажать кнопку "Удалить".
	 * <li> Проверить что договор аннулирован и удален
	 * <p>
	 * Исполнитель: [a.isakov]
	 */
	@Test
	public void shouldCancelAndDeleteContract(
			@InitialPage ContractCardPage contractCard,
			@Page ContractListPage contractList,
			@DataProvider(
					providerClass = ContractProvider.class,
					contextPropertyName = ContractProvider.CREATED_CONTRACT_PROP
			) Contract contract) {
		//@formatter:on

		String number = contract.getDocumentNumber();

		contractCard.lifecycleRoutingBlock.performTransition(ContractLifecycle.Routes.CANCEL.getName());
		contractCard.lifecycleRoutingBlock.setComment("Тестовый комментарий");
		contractCard.lifecycleRoutingBlock.confirmTransition();
		assertEquals(ContractState.CANCELLED.getName(), contractCard.state.getValue());

        contractCard.activityBlock.openHistory();
		assertEquals(
				"Переход из состояния",
				REGISTRATION.getName(),
                contractCard.activityBlock.lifecycleHistoryBlock.getLastLifecycleTransitionFromState()
		);
		assertEquals(
				"Переход в состояние",
				ContractState.CANCELLED.getName(),
                contractCard.activityBlock.lifecycleHistoryBlock.getLastLifecycleTransitionToState()
		);

        contractCard.delete.click();
        // попробуем найти удалённый договор
        contractList.contractFilterBlock.setNumber(number);
        contractList.contractFilterBlock.find();

        // Так как номер содержит псеводослучайное число, то поиск вернёт одну запись, если договор не был удалён,
		// или не вернёт ничего, если договор успешно был удалён
        assertTrue(contractList.searchResults.getRowCount() == 0);
	}

	/**
	 * Сценарий id = C101050 Создание дополнительного соглашения
	 * <p>
	 * Предварительные условия: Открыта карточка договора, находящегося в статусе "Действует".
	 * Цель: Проверить создание дополнительного соглашения
	 * <p>
	 * Сценарий:
	 * <ol> В блоке "Дополнительные соглашения" нажать кнопку "Создать дополнительное соглашение".
	 * <ol> Выбрать "Тип".
	 * <ol> Выбрать "Начало действия".
	 * <ol> Указать номер. (номер не указываю, так как это не обезателено и генериться автоматически)
	 * <ol> Нажать кнопку "Создать".
	 * <ol> Проверить, что в блоке "Дополнительные соглашения" отображена плитка, соответствующая созданному дополнительному соглашению.
	 * <p>
	 * Исполнитель: [v.semchenko]
	 */
	@Test
	//@formatter:off
	public void shouldCreateContractExtension(
			@InitialPage ContractCardPage page,
			@DataProvider(
					providerClass = ContractInforceProvider.class,
					contextPropertyName = ContractInforceProvider.CONTRACT
			) Contract contract
	) {
		//@formatter:on
		String extensionType = getTestRunContextProperty(ContractInforceProvider.EXTENSION_TYPE_NAME, String.class);
		String number = "ТестовоеДС-001";
		String validFrom = "15.02.2018";

		page.contractExtensionBlock.openCreationDialog.click();

		page.contractExtensionBlock.extensionType.select(extensionType);
		page.contractExtensionBlock.validFrom.setValue(validFrom);
		page.contractExtensionBlock.number.input(number);

		page.contractExtensionBlock.create.click();

		assertEquals(REGISTRATION.getName(), page.contractExtensionBlock.getContractExtensionState(number));
		assertEquals(extensionType, page.contractExtensionBlock.getContractExtensionType(number));
		assertEquals(validFrom, page.contractExtensionBlock.getContractExtensionValidFrom(number));
	}
}