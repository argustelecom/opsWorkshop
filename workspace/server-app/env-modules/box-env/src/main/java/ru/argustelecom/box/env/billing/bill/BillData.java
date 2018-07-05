package ru.argustelecom.box.env.billing.bill;

import static java.lang.String.format;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Контейнер, который хранит все проинициализированные данные для генерации
 * {@linkplain ru.argustelecom.box.env.billing.bill.model.Bill счёта}.
 */
@Getter
public class BillData {

	/**
	 * Идентификатор счёта.
	 */
	private Long id;

	/**
	 * Номер счёта.
	 */
	private String number;

	/**
	 * Клиент, для которого формируется квитанция.
	 */
	private Customer customer;

	/**
	 * Договор, по которому сторится счёт. Будет заполнен в том случае если {@link #groupingMethod} равняется способу
	 * группировки по договору.
	 */
	@Setter
	private Contract contract;

	/**
	 * Идентификатор поставщика, от имени которого выставляется счёт. Поставщиком может быть как
	 * {@link ru.argustelecom.box.env.party.model.role.Owner} так и
	 * {@link ru.argustelecom.box.env.party.model.role.Supplier} в зависимости от
	 * {@linkplain ru.argustelecom.box.env.contact.ContactCategory категории договора}.
	 */
	private PartyRole provider;

	/**
	 * Идентификатор брокера, от имени которого выставляется счёт. Значение брокера может быть либо пустым (тогда это
	 * двусторонний договор) либо равняться {@link ru.argustelecom.box.env.party.model.role.Owner}.
	 */
	private Owner broker;

	/**
	 * Лицевой счёт, по которому строится счёт. Будет заполнен в том случае если {@link #groupingMethod} равняется
	 * способу группировки по лицевому счёту.
	 */
	@Setter
	private PersonalAccount personalAccount;

	/**
	 * Способ группировки данных для генерации счёта.
	 */
	private GroupingMethod groupingMethod;

	/**
	 * Условие оплаты.
	 */
	private PaymentCondition paymentCondition;

	/**
	 * Дата выставления счёта.
	 */
	private Date billDate;

	/**
	 * Дата создания счёта. Это системная дата, момента физического создания счёта.
	 */
	private Date creationDate;

	/**
	 * Период, за который выставляется счёт.
	 */
	private BillPeriod period;

	/**
	 * Спецификация счёта.
	 */
	private BillType billType;

	/**
	 * Шаблон печатной формы счёта.
	 */
	private ReportModelTemplate template;

	/**
	 * Список подписок, попавших под условия построения счёта.
	 */
	private List<Subscription> subscriptions;

	private List<Long> shortTermInvoiceIds;

	private List<Long> usageInvoiceIds;

	@Builder
	public BillData(Long id, String number, Customer customer, Contract contract, PersonalAccount personalAccount,
			GroupingMethod groupingMethod, PaymentCondition paymentCondition, Date billDate, Date creationDate,
			BillPeriod period, BillType billType, ReportModelTemplate template, List<Subscription> subscriptions,
			PartyRole provider, Owner broker, List<Long> shortTermInvoiceIds, List<Long> usageInvoiceIds) {
		this.id = id;
		this.number = number;
		this.customer = customer;
		this.contract = contract;
		this.personalAccount = personalAccount;
		this.groupingMethod = groupingMethod;
		this.paymentCondition = paymentCondition;
		this.billDate = billDate;
		this.period = period;
		this.billType = billType;
		this.template = template;
		this.subscriptions = subscriptions;
		this.creationDate = creationDate;
		this.provider = provider;
		this.broker = broker;
		this.shortTermInvoiceIds = shortTermInvoiceIds;
		this.usageInvoiceIds = usageInvoiceIds;
	}

	/**
	 * Возвращает идентификатор группы в зависимости от {@linkplain GroupingMethod типа группировки}.
	 */
	public Long getGroupId() {
		switch (groupingMethod) {
		case CONTRACT:
			return contract.getId();
		case PERSONAL_ACCOUNT:
			return personalAccount.getId();
		default:
			throw new SystemException(format("Unsupported grouping method: '%s'", groupingMethod));
		}
	}

}