package ru.argustelecom.box.env.billing.subscription;

import java.io.Serializable;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.pricing.PricelistDtoTranslator;
import ru.argustelecom.box.inf.util.Callback;

public abstract class SubscriptionCreationDialogModel<SCD extends SubscriptionCreationDto> implements Serializable {

	@Inject
	protected ProductAppService productAs;

	@Inject
	protected SubscriptionAppService subscriptionAs;

	@Inject
	protected SubscriptionDtoTranslator subscriptionDtoTr;

	@Inject
	protected PricelistDtoTranslator pricelistDtoTr;

	@Setter
	protected Callback<SubscriptionDto> callback;

	@Setter
	protected PersonalAccount personalAccount;

	@Getter
	protected SCD subscriptionCreationDto;

	public abstract void onCreationDialogOpened();

	public abstract void create();

	public void clean() {
		subscriptionCreationDto = null;
	}

	public Customer getCustomer() {
		return personalAccount.getCustomer();
	}

	private static final long serialVersionUID = -3912229006386602659L;

}