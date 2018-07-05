package ru.argustelecom.box.env.order;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Ignore;
import org.junit.Test;

import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.order.testdata.OrderFormalizationProvider;
import ru.argustelecom.system.inf.testframework.it.ui.navigation.LocationParameterProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class OrderCardIT extends AbstractWebUITest implements LocationParameterProvider {

    @Override
    public Map<String, String> provideLocationParameters() {
        Map<String, String> params = new HashMap<>();
        
        Order order = null;
        //для получения от OrderFormalizationProvider страницу созданног order
        if (testName.getMethodName().equals("shouldMoveOrderToWork"))
        	order = getTestRunContextProperty(OrderFormalizationProvider.ORDER_FORMALIZATION_PROP_NAME, Order.class);
        	params.put("order", new EntityConverter().convertToString(order));

        return params;
    }

    /**
     * Сценарий id = C269544 Выбор предложений в заявке
     * <p>
     * Предварительные условия: В заявке, в которой требуется выбрать предложения, были указаны требования клиента.
     * Открыта карточка заявки, в которой требуется выбрать предложения.
     * FIXME должен предоставлять провайдер
     * Должен создавать заявку
     * Должен создавать тип товара / услуги, продукт
     * Должен создавать и активировать прайс-лист, который будет публичным с этим продуктом     *
     * <p>
     * Сценарий:
     * <ol>
     * <li>В блоке "Выбранные предложения" нажать кнопку "Добавить предложение".
     * <li>В диалоге в блоке "Фильтр по требованиям" отметить фильтры.
     * <li>В диалоге в блоке "Подходящие предложения" отметить предложения.
     * <li>Нажать кнопку "Добавить".
     * <li>Проверить, что в блоке "Выбранные предложения" корректно отображены выбранные предложения.
     * <p>
     * Исполнитель: [v.sysoev]
     */
    @Ignore // FIXME до готовности провайдера
    @Test
    public void shouldSelectOffers(@InitialPage OrderCardPage page) {
        String requirement = "Маршрутизатор";
        String offer = "Интернет 100 + Роутер (аренда)";
        String price = "800.00";

        page.orderOffersBlock.openSelectRequirementsDialog();
        page.orderOffersBlock.chooseRequirement(requirement);
        page.orderOffersBlock.selectOffers(offer);
        page.orderOffersBlock.addOffers();

        assertEquals(price, page.orderOffersBlock.getChosenOfferPrice(offer));
    }
    
    /**
     * Сценарий id = C128796 Взятие заявки в работу
     * <p>
     * Предварительные условия: Открыта заявка в статусе "Оформление", причем указан адрес предоставления и контакт.
     * <p>
     * Сценарий:
     * <ol>
     * <li>В блоке "Атрибуты" нажать кнопку "Взять в работу".
     * <li>В вести комментарий: "Тестовая заяка в работе"
     * <li>Нажать кнопку "В работу".
     * <li>Проверить, что "Состояние" заявки изменено на "В работе".
     * <p>
     * Исполнитель: [v.semchenko]
     */
    @Test
    //@formatter:off
    public void shouldMoveOrderToWork(
            @InitialPage OrderCardPage page,
            @DataProvider(
                    providerClass = OrderFormalizationProvider.class,
					contextPropertyName = OrderFormalizationProvider.ORDER_FORMALIZATION_PROP_NAME
            ) Order order
    ) {
        //@formatter:on
        page.lifecycleRoutingBlock.performTransition("В работу");
        page.lifecycleRoutingBlock.setComment("Тестовая заявка взята в работу");
        page.lifecycleRoutingBlock.confirmTransition();

        assertEquals("В работе", page.state.getValue());
    }
}
