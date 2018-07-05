package ru.argustelecom.box.env.billing.bill.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Враппер, объединяющий перечеть данных для формирования счета в разрезе определённой {@linkplain GroupingMethod
 * группы}. По ним будет строиться {@linkplain Bill счет}.
 */
@Getter
@AllArgsConstructor
public class BillGroup {

	/**
	 * Идентификатор группирующего объекта.
	 */
	private Long id;

	/**
	 * Тип группирующего объекта.
	 */
	private GroupingMethod type;

	/**
	 * Идентификатор поставщика - в договоре.
	 */
	private Long providerId;

	/**
	 * Идентификатор посредника(агента) - в договоре.
	 */
	private Long brokerId;

	/**
	 * Идентификатор клиента.
	 */
	private Long customerId;

	/**
	 * Список идентификаторов подписок.
	 */
	private List<Long> subscriptionIds;

	/**
	 * Список идентификаторов инвойсов по фактам использования.
	 */
	private List<Long> usageInvoiceIds;

	/**
	 * Список идентификаторов инвойсов по единовременным начислениям
	 */
	private List<Long> shortTermInvoiceIds;

}