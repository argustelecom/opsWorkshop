package ru.argustelecom.box.env.billing.account.model;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSED;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.FORMALIZATION;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.billing.model.IPersonalAccount;
import ru.argustelecom.system.inf.utils.CDIHelper;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", uniqueConstraints = {
		@UniqueConstraint(name = "uc_personal_number", columnNames = "number") })
@NamedEntityGraph(name = PersonalAccount.FOR_BILL_GRAPH_NAME, attributeNodes = {
		@NamedAttributeNode(value = "subscriptions", subgraph = "subscriptionGraph"),
		@NamedAttributeNode(value = "customer") }, subgraphs = {
				@NamedSubgraph(name = "subscriptionGraph", attributeNodes = {
						@NamedAttributeNode(value = "subjectCause"), @NamedAttributeNode(value = "costCause") }) })
@EntityWrapperDef(name = IPersonalAccount.WRAPPER_NAME)
public class PersonalAccount extends BusinessObject implements LifecycleObject<PersonalAccountState>, Printable {

	public static final String FOR_BILL_GRAPH_NAME = "forBillGraph";

	public static final int MAX_NUMBER_LENGTH = 64;

	@Column(nullable = false, updatable = false, length = MAX_NUMBER_LENGTH)
	private String number;

	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customer;

	@Enumerated(EnumType.STRING)
	private PersonalAccountState state;

	@OneToMany(mappedBy = "personalAccount")
	private List<Subscription> subscriptions = new ArrayList<>();

	@Column(length = 16)
	private String currency;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "amount", column = @Column(name = "threshold")) })
	private Money threshold;

	protected PersonalAccount() {
	}

	public PersonalAccount(Long id) {
		super(id);
		setState(PersonalAccountState.ACTIVE);
	}

	@Override
	public PersonalAccountRdo createReportData() {
		PersonalAccountBalanceService balanceService = CDIHelper.lookupCDIBean(PersonalAccountBalanceService.class);

		//@formatter:off
		return PersonalAccountRdo.builder()
					.id(getId())
					.number(getNumber())
					.availableBalance(balanceService.getAvailableBalance(this).getRoundAmount())
					.balance(balanceService.getBalance(this).getRoundAmount())
					.threshold(getThreshold().getRoundAmount())
					.customer(getCustomer().createReportData())
					.state(getState().getName())
				.build();
		//@formatter:on
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void addSubscription(Subscription subscription) {
		checkState(Objects.equals(this, subscription.getPersonalAccount()));
		checkState(!subscriptions.contains(subscription));

		subscriptions.add(subscription);
	}

	public void removeSubscription(Subscription subscription) {
		checkState(Objects.equals(this, subscription.getPersonalAccount()));
		checkState(subscriptions.contains(subscription));

		subscriptions.remove(subscription);
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public List<Subscription> getActiveSubscriptions() {
		return subscriptions.stream().filter(subscription -> !subscription.getState().equals(FORMALIZATION)
				&& !subscription.getState().equals(CLOSED)).collect(Collectors.toList());
	}

	protected void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public Currency getCurrency() {
		return Currency.getInstance(currency);
	}

	public void setCurrency(Currency currency) {
		this.currency = currency.getCurrencyCode();
	}

	public Money getThreshold() {
		return threshold;
	}

	public void setThreshold(Money threshold) {
		this.threshold = threshold;
	}

	@Override
	public PersonalAccountState getState() {
		return state;
	}

	@Override
	public void setState(PersonalAccountState state) {
		this.state = state;
	}

	@Override
	public String getObjectName() {
		return "Лицевой счёт " + number;
	}

	public String getShortName() {
		return "НЛС " + number;
	}

	public String getBeautifulNumber() {
		StringBuilder beautifulNumberBuilder = new StringBuilder();
		char[] numberBytes = number.toCharArray();
		for (int i = 0; i < numberBytes.length; i++) {
			beautifulNumberBuilder.append(numberBytes[i]);
			if ((i + 1) % 3 == 0) {
				beautifulNumberBuilder.append(" ");
			}
		}
		return beautifulNumberBuilder.toString().trim();
	}

	private static final long serialVersionUID = -7226009870726769546L;

}