package ru.argustelecom.box.env.pricing;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;

import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.pricing.model.CommonPricelist;
import ru.argustelecom.box.env.pricing.testdata.CommonPricelistProvider;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.testframework.it.ui.navigation.LocationParameterProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class PricelistCardIT extends AbstractWebUITest implements LocationParameterProvider {

	@Override
	public Map<String, String> provideLocationParameters() {
		Map<String, String> params = new HashMap<>();

		CommonPricelist pricelist = getTestRunContextProperty(
				CommonPricelistProvider.COMMON_PRICELIST_PROP_NAME, CommonPricelist.class);

		params.put("pricelist", new EntityConverter().convertToString(pricelist));
		return params;
	}

	/**
	 * Сценарий id = С259093 Выбор предложений в заявке
	 * <p>
	 * Предварительные условия: Открыта карточка прайс-листа, который требуется отредактировать.
	 * <p>
	 * Сценарий:
	 * <ol>
	 * <li>В блоке "Предложения продуктов" нажать кнопку "Создать предложение продуктов".
	 * <li>В диалоге создания заполнить:
	 * <ul>
	 * <li>Продукт (обязательный параметр)
	 * <li>Условие предоставления (обязательный параметр)
	 * <li>Количество (обязательный параметр)
	 * <li>Единица измерения (обязательный параметр)
	 * <li>Стоимость (обязательный параметр)
	 * </ul>
	 * <li>Нажать кнопку "Сохранить".
	 * <li>Проверить, что в блоке "Предположения продуктов" появилась новая запись, соответствующая созданному.
	 * <p>
	 * Исполнитель: [v.semchenko]
	 */
	@Test
    //@formatter:off
    public void shouldCreatePeriodicProductOffering(
            @InitialPage PricelistCardPage page,
			@DataProvider(
                    providerClass = CommonPricelistProvider.class,
                    contextPropertyName = CommonPricelistProvider.COMMON_PRICELIST_PROP_NAME
            ) CommonPricelist commonPricelist
    ) {
        //@formatter:on
		String productTypeName = getTestRunContextProperty(CommonPricelistProvider.PRODUCT_TYPE, String.class);
		String provisionTermsName = getTestRunContextProperty(CommonPricelistProvider.PROVISION_TERMS, String.class);

		page.openDialogNewProductOffering.click();

		page.productTypes.select(productTypeName);
		page.provisionTerms.select(provisionTermsName);
		page.periodUnits.select(PeriodUnit.MONTH.getName());
		page.priceNewOfferingProduct.input("1200");

		// В зависимости от типа периода это поле то допускает ввод, то нет. Т.к. тип периода не определен - проверям
		if (!page.amount.isDisabled()) {
			page.amount.input("1");
		}

		page.saveNewProductOffering.click();

		page.openLastPaginationPage();
		int lastRowIndex = page.pricelistProductsTable.getRowCount() - 1;

		assertEquals(productTypeName, page.pricelistProductsTable.getRow(lastRowIndex).getCell(1).getTextString());
		assertEquals(provisionTermsName, page.pricelistProductsTable.getRow(lastRowIndex).getCell(2).getTextString());
		assertEquals("1", page.pricelistProductsTable.getRow(lastRowIndex).getCell(3).getTextString());
		assertEquals(PeriodUnit.MONTH.getName(), page.pricelistProductsTable.getRow(lastRowIndex).getCell(4).getTextString());
		// расчет суммы без налога покрыт в ProductOfferingTest
		assertEquals("1200.00", page.pricelistProductsTable.getRow(lastRowIndex).getCell(5).getTextString());
	}

	/**
	 * Сценарий id = C100770 Создание комментария
	 * <p>
	 * Предварительные условия: Открыта карточка прайс-листа, который требуется отредактировать.
	 * <p>
	 * Сценарий:
	 * <ol>
	 * <li>В блок "Активность" нажать на "Добавить комментарий".
	 * <li>В диалоге ввести: "Заголовок"
	 * <li>В диалоге ввести: "Комментарий"
	 * <li>Нажать кнопку "Сохранить".
	 * <li>Проверить, что в списке комментариев отображается созданный.
	 * <p>
	 * Исполнитель: [v.sysoev]
	 */
	//@formatter:off
	@Test
	public void shouldCreateComment(
			@InitialPage PricelistCardPage page,
			@DataProvider(
					providerClass = CommonPricelistProvider.class,
                    contextPropertyName = CommonPricelistProvider.COMMON_PRICELIST_PROP_NAME
            ) CommonPricelist commonPricelist
    ) {
        //@formatter:on
        String header = "Заголовок";
		String body = "Содержание";

		page.activity.openCreateCommentDialog();
		page.activity.setHeader(header);
		page.activity.setBody(body);
		page.activity.createComment();

		assertEquals(body, page.activity.getCommentBody(header));
	}
}