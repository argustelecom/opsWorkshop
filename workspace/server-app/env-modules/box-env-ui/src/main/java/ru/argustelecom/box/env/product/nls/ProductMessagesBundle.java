package ru.argustelecom.box.env.product.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface ProductMessagesBundle {

	@Message("Группа")
	String groupProduct();

	@Message("Простой продукт")
	String plainProduct();

	@Message("Составной продукт")
	String compositeProduct();

	@Message("Создание группы продуктов")
	String productGroupCreation();

	@Message("Создание простого продукта")
	String productPlainCreation();

	@Message("Создание составного продукта")
	String productCompositeCreation();

}
