package ru.argustelecom.box.env.billing.account;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class PersonalAccountDtoTranslator implements DefaultDtoTranslator<PersonalAccountDto, PersonalAccount> {

	@Inject
	private PersonalAccountBalanceService balanceSvc;

	@Inject
	private SubscriptionDtoTranslator subscriptionDtoTr;

	//@formatter:off
	
	@Override
	public PersonalAccountDto translate(PersonalAccount account) {
		List<SubscriptionDto> subscriptions = account.getSubscriptions().stream()
			.filter(s -> !s.inState(SubscriptionState.CLOSED))
			.map(subscriptionDtoTr::translate)
			.collect(toList());
		
		return PersonalAccountDto.builder()
			.id(account.getId())
			.number(account.getShortName())
			.availableBalance(balanceSvc.getAvailableBalance(account))
			.subscriptions(subscriptions)
			.build();
	}
}
