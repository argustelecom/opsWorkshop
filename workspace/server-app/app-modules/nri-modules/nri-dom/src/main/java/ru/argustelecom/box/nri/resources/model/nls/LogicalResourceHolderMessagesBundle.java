package ru.argustelecom.box.nri.resources.model.nls;


import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface LogicalResourceHolderMessagesBundle {

	/**
	 * возвращает строку Добавляемый тип логического ресурса не поддерживается данной спецификацией
	 * @return
	 */
	@Message("Добавляемый тип логического ресурса не поддерживается данной спецификацией")
	String addedLogicalResourceTypeUnsupportedByThisSpecification();
}
