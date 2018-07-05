package ru.argustelecom.box.env.commodity.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface CommodityMessagesBundle {

	@Message(value = "Группа")
	String group();

	@Message(value = "Создание группы")
	String groupCreation();

	@Message(value = "Тип услуги")
	String serviceType();

	@Message(value = "Создание типа услуги")
	String serviceTypeCreation();

	@Message(value = "Тип товара")
	String goodsType();

	@Message(value = "Создание типа товара")
	String goodsTypeCreation();

	@Message(value = "Тип опции")
	String optionType();

	@Message(value = "Создание типа опции")
	String optionTypeCreation();
}
