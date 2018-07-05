package ru.argustelecom.box.env.contact;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.NoSuchElementException;
import ru.argustelecom.box.env.contact.testdata.ContactTypeProvider;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Row;
import ru.argustelecom.system.inf.testframework.it.ui.errordetection.ErrorDetection;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import static org.junit.Assert.*;
import static ru.argustelecom.box.env.UITestUtils.uniqueId;

import org.jboss.arquillian.graphene.page.InitialPage;

import java.util.UUID;

/**
 * Набор тестов, посвящённых проверке работы со справочником "Типы контактов"
 *
 * @author n.isakova
 */
@LoginProvider(providerClass = BoxLoginProvider.class)
public class ContactTypeDirectoryIT extends AbstractWebUITest {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	/**
	 * Сценарий id = 113140 создание типа контакта
	 * <p>
	 * Предварительные условия: В системе заведены категории контактов
	 * <p>
	 * Сценарий:
	 * <ol>
	 * <li>Нажать кнопку "Добавить тип контакта".
	 * <li>В диалоге выбрать Категорию, ввести Название типа контакта, Сокращение.
	 * <li>Нажать кнопку "Создать".
	 * <li>Проверить, что: в таблице появилась новая запись с категорией, названием типа контакта и сокращением, введенными при создании.
	 * <p>
	 * Исполнитель: [n.isakova]
	 */
	@Test
	public void shouldCreateContactTypeThenDelete(@InitialPage ContactTypeDirectoryPage page) {
		String contactCategory = ContactCategory.EMAIL.getName();
		String contactTypeName = uniqueId("Тестовый тип контакта");
		String shortContactTypeName = contactTypeName.substring(0, 10);

		page.openDialogCreateContactType.click();
		page.contactCategory.select(contactCategory);
		page.contactTypeName.input(contactTypeName);
		page.shortContactTypeName.input(shortContactTypeName);
		page.completeCreateContactType.click();

		Row newContactType = page.createdContactTypes.findRow(contactTypeName);

		assertEquals(contactCategory, newContactType.getCell(1).getTextString());
		assertEquals(contactTypeName, newContactType.getCell(2).getTextString());
		assertEquals(shortContactTypeName, newContactType.getCell(3).getTextString());

		page.deleteContactType(contactTypeName);

		expected.expect(NoSuchElementException.class);
		page.createdContactTypes.findRow(contactTypeName);
	}

	/**
	 * Сценарий id = (проверка правил уникальности для типов контактов)
	 * <p>
	 * Предварительные условия: В системе заведен тип контактов
	 * <p>
	 * Сценарий:
	 * <ol>
	 * <li>Нажать кнопку "Добавить тип контакта".
	 * <li>В диалоге выбрать Категорию и ввести Название, аналогичные Категории и Названию уже созданного типа контакта.
	 * <li>Нажать кнопку "Создать".
	 * <li>Проверить, что: выдано сообщение, что тип с заданным названием уже существует.
	 * <li>Проверить, что: тип контакта не добавлен (кол-во строк в таблице не изменилось).
	 * <p>
	 * Исполнитель: [n.isakova]
	 */
	@Test
	//@formatter:off
	public void shouldCreateUniqueContactType(
			@InitialPage ContactTypeDirectoryPage page,
			@DataProvider(
					providerClass = ContactTypeProvider.class,
					contextPropertyName = ContactTypeProvider.CREATED_CONTACT_TYPE_NAME
			) String contactTypeName
	) {
		//@formatter:on
		String contactCategoryName = getTestRunContextProperty(ContactTypeProvider.CREATED_CONTACT_CATEGORY, String.class);

		page.openDialogCreateContactType.click();
		page.contactCategory.select(contactCategoryName);
		page.contactTypeName.input(contactTypeName);

		expected.expect(ErrorDetection.DetectedGrowlErrorsException.class);
		page.completeCreateContactType.click();
	}
}