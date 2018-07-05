package ru.argustelecom.box.env.product;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;

import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.product.model.ProductTypeGroup;
import ru.argustelecom.box.env.product.testdata.ProductTypeGroupProvider;
import ru.argustelecom.system.inf.testframework.it.ui.navigation.LocationParameterProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

/**
 * ui-тесты на сценарии каталога продуктов
 * <p>
 * 
 * @author kostd
 * @author m.teslenko
 */
@LoginProvider(providerClass = BoxLoginProvider.class)
public class ProductTypeDirectoryIT extends AbstractWebUITest implements LocationParameterProvider {

	@Override
	public Map<String, String> provideLocationParameters() {
		Map<String, String> params = new HashMap<>();
		ProductTypeGroup group = getTestRunContextProperty(ProductTypeGroupProvider.CREATED_GROUP_PROPERTY_NAME,
				ProductTypeGroup.class);
		params.put("selectedProduct", new EntityConverter().convertToString(group));
		return params;
	}

	/**
	 * Сценарий id = C139166 Создание составного продукта
	 * 
	 * <p>
	 * Предварительные условия:
	 * <ul>
	 * <li>Провайдер должен обеспечить: "Каталог продуктов" содержит группу продуктов, в которой создаем составной
	 * продукт
	 * <p>
	 * Сценарий:
	 * <ol>
	 * <li>Выделить в дереве продуктов группу, в которой требуется создать составной продукт (выполняется
	 * автоматически, @see {@link #provideLocationParameters()}).
	 * <li>Нажать кнопку "Добавить"
	 * <li>В выпадающем списке выбрать пункт "Составной продукт".
	 * <li>В диалоге ввести Название
	 * <li>В диалоге ввести Описание
	 * <li>Нажать кнопку "Создать".
	 * <li>Проверяем, что: Продукт добавлен в ранее выбранную группу
	 * <li>Проверяем, что: Продукт имеет имя, указанное при создании
	 * <li>Проверяем, что: Создан именно составной продукт Исполнитель: [m.teslenko]
	 */
	@Test
	public void shouldCreateCompositeProduct(
			@InitialPage ProductTypeDirectoryPage page,
			@DataProvider(
					providerClass = ProductTypeGroupProvider.class,
					contextPropertyName = ProductTypeGroupProvider.CREATED_GROUP_PROPERTY_NAME
			) ProductTypeGroup productTypeGroup
	) {
		// вряд ли в одной группе могут быть несколько продуктов с одинаковым именем. Поэтому добавляем немного рандома
		String productName = "Тестовый продукт " + UUID.randomUUID().toString();
		String productDesc = "Тестовое описание для тестового продукта";

		page.openCreationDialog("Составной продукт");

		page.productName.input(productName);
		page.productDesc.input(productDesc);

		page.createProductButton.click();

		assertEquals("Название группы", page.groupName.getValue(), productTypeGroup.getObjectName());
		assertEquals("Название продукта", page.productName.getValue(), productName);
		assertEquals("Простой / составной продукт", page.categoryName.getValue(), "Составной продукт");
	}
}