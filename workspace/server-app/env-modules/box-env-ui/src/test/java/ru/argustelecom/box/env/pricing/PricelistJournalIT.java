package ru.argustelecom.box.env.pricing;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Ignore;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.pricing.model.PricelistState;
import ru.argustelecom.box.env.pricing.testdata.CommonPricelistCreationProvider;
import ru.argustelecom.box.env.pricing.testdata.CustomPricelistCreationProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static org.junit.Assert.*;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class PricelistJournalIT extends AbstractWebUITest {

    /**
     * Сценарий id = 100440 Создание публичного прайс-листа
     * Сценарий:
     * <ol>
     * <li>Нажать кнопку "Создать прайс-лист", выбрать в списке пункт "Публичный".
     * <li>В диалоге ввести "Название"
     * <li>В диалоге ввести "Действует с"
     * <li>В диалоге ввести "Действует по"
     * <li>В диалоге ввести "Ставка налога"
     * <li>В диалоге ввести "Сегмент"
     * <li>Нажать кнопку "Сохранить".
     * <li>Проверить, что: Поле "Название" заполнено в соответствии с указанными при создании данными
     * <li>Проверить, что: Поле "Действует с" заполнено в соответствии с указанными при создании данными
     * <li>Проверить, что: Поле "Действует по" заполнено в соответствии с указанными при создании данными
     * <li>Проверить, что: Поле "Ставка налога" заполнено в соответствии с указанными при создании данными
     * <li>Проверить, что: Поле "Сегмент" заполнено в соответствии с указанными при создании данными
     */
    //@formatter:off
    @Test
    public void shouldCreateCommonPricelist(
            @InitialPage PricelistJournalPage pricelistJournal,
            @Page PricelistCardPage pricelistCard,
            @DataProvider(
                    providerClass = CommonPricelistCreationProvider.class,
                    contextPropertyName = CommonPricelistCreationProvider.CUSTOMER_SEGMENT_PROP_NAME
            ) CustomerSegment customerSegment
    ) {
        //@formatter:on
        String name = "Физические лица: декабрь 2017";
        String validFrom = "01.12.2017";
        String validTo = "31.12.2017";
        String segment = customerSegment.getObjectName();
        String ownerName = getTestRunContextProperty(CommonPricelistCreationProvider.OWNER_NAME, String.class);

        pricelistJournal.openCreateCommonPricelistDialog("Публичный");
        pricelistJournal.name.input(name);
        pricelistJournal.validFrom.setValue(validFrom);
        pricelistJournal.owner.select(ownerName);
        pricelistJournal.validTo.setValue(validTo);
        pricelistJournal.setSegment(segment);
        pricelistJournal.create.click();

        assertEquals("Название", name, pricelistCard.name.getValue());
        assertTrue("Действует с", pricelistCard.validFrom.getValue().startsWith(validFrom));
        assertTrue("Действует по", pricelistCard.validTo.getValue().startsWith(validTo));
        assertTrue("Сегмент", pricelistCard.getSegments().contains(segment));
        assertEquals("Владелец / компания", ownerName, pricelistCard.owner.getValue());
        assertEquals("Статус ЖЦ", PricelistState.CREATED.getName(), pricelistCard.state.getValue());
    }

    /**
     * Сценарий id = C101045E Создание индивидуального прайс-листа
     * Сценарий:
     * <ol>
     * <li>Нажать кнопку "Создать прайс-лист", выбрать в списке пункт "Публичный".
     * <li>В диалоге ввести "Название"
     * <li>В диалоге ввести "Действует с"
     * <li>В диалоге ввести "Действует по"
     * <li>В диалоге ввести "Ставка налога"
     * <li>В диалоге ввести "Тип клиента"
     * <li>В диалоге ввести "Клиент"
     * <li>Нажать кнопку "Сохранить".
     * <li>Проверить, что: Поле "Название" заполнено в соответствии с указанными при создании данными
     * <li>Проверить, что: Поле "Действует с" заполнено в соответствии с указанными при создании данными
     * <li>Проверить, что: Поле "Действует по" заполнено в соответствии с указанными при создании данными
     * <li>Проверить, что: Поле "Ставка налога" заполнено в соответствии с указанными при создании данными
     */
    //@formatter:off
    @Ignore("Ошибка при поиске клиента не позволяет доделать тест")
    @Test
    public void shouldCreateCustomPricelist(
            @InitialPage PricelistJournalPage pricelistJournal,
            @Page PricelistCardPage pricelistCard,
            @DataProvider(
                    providerClass = CustomPricelistCreationProvider.class,
                    contextPropertyName = CustomPricelistCreationProvider.CUSTOMER_FULLNAME_PROP_NAME
            ) String customer
    ) {
        //@formatter:on
        String name = "Индивидуальное предложение: декабрь 2017";
        String validFrom = "01.12.2017";
        String validTo = "31.12.2017";
        String customerType = getTestRunContextProperty(CustomPricelistCreationProvider.CUSTOMER_TYPE_PROP_NAME, String.class);

        pricelistJournal.openCreateCommonPricelistDialog("Индивидуальный");
        pricelistJournal.name.input(name);
        pricelistJournal.validFrom.setValue(validFrom);
        pricelistJournal.validTo.setValue(validTo);
        pricelistJournal.customerType.select(customerType);
        pricelistJournal.customer.search(customer);
        pricelistJournal.create.click();

        assertEquals("Название", name, pricelistCard.name.getValue());
        assertTrue("Действует с", pricelistCard.validFrom.getValue().startsWith(validFrom));
        assertTrue("Действует по", pricelistCard.validTo.getValue().startsWith(validTo));
        assertEquals("Фамилия пользователя", customer, pricelistCard.customerBlock.lastName.getValue());
    }
}