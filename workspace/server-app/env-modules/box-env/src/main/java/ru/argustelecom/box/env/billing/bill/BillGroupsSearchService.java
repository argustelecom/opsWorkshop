package ru.argustelecom.box.env.billing.bill;

import java.util.Date;
import java.util.List;
import java.util.Set;

import ru.argustelecom.box.env.billing.bill.model.BillGroup;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.CustomerType;

/**
 * Сервис для получения групп подписок, по которым будут собираться данные для формирования счета.
 */
public interface BillGroupsSearchService {

	/**
	 * Выбирает все дпанные по начислениям, возвращая их в {@linkplain BillGroup сгруппированном виде}.
	 * <p>
	 * <b>Параметры, которые всегда участвую в выборке:</b><br/>
	 * <ul>
	 * <li>@start</li>
	 * <li>@end</li>
	 * <li>@paymentCondition</li>
	 * </ul>
	 * <br/>
	 * <b>Правила выборки:</b><br/>
	 * <b>1.</b> Если не указан {@linkplain CustomerType тип клиента}, то все остальные условия(кроме основных)
	 * игнорируются<br/>
	 * <b>2.</b> Если указан тип клиента, но не указаны конкретный
	 * {@linkplain ru.argustelecom.box.env.contract.model.Contract
	 * договор}/{@link ru.argustelecom.box.env.billing.account.model.PersonalAccount лицевой счёт}, то ищем
	 * по типу клиента.<br/>
	 * <b>3.</b> Если указан <b>@groupId</b>, то ищем данные конкретной группы. Сервис для поиска по конкретной группе
	 * это класс имплементирующий данный интерфейс:
	 * <ul>
	 * <lo>{@link BillGroupsByContractSearchService} - сервис для поиска по
	 * {@linkplain ru.argustelecom.box.env.contract.model.Contract договору} и всем его
	 * {@linkplain ru.argustelecom.box.env.contract.model.ContractExtension доп. соглашений}.</lo>
	 * <lo>{@link BillGroupsByPersonalAccountSearchService} - сервис для поиска по
	 * {@link ru.argustelecom.box.env.billing.account.model.PersonalAccount лицевому счёту}.</lo>
	 * </ul>
	 * <br/>
	 * Правила группировки так же определяются сервисами описанными в п.3. Т.е. если в сервис не передан
	 * <b>@groupId</b>, то он ищет исходя из остальных параметров, но с разными типами группировок.
	 * </p>
	 *
	 * @param billPeriod
	 *            период счета. Даты интереса могут отличаться от периода счета в том случае, если в счете есть
	 *            аналитики по начислениям за предыдущий/следующий периоды
	 * @param start
	 *            нижнея дата интереса.
	 * @param end
	 *            верхняя дата интереса.
	 * @param paymentCondition
	 *            условие оплаты.
	 * @param customerType
	 *            тип клиента.
	 * @param customerId
	 *            идентификатор клиента.
	 * @param groupId
	 *            идентификатор группирующего объекта (договора/лицевого счёта). Кто именно определяется в потомках.
	 */
	List<BillGroup> find(BillPeriod billPeriod, Date start, Date end, PaymentCondition paymentCondition,
			CustomerType customerType, Long customerId, Long groupId, Set<Long> providerIds);

}