package ru.argustelecom.box.env.billing.account;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named(value = "personalAccountsFm")
public class PersonalAccountsFrameModel implements Serializable {

	private static final long serialVersionUID = 8286789004121537049L;

	private static final Map<SubscriptionState, String> STATE_CLASSES;
	static {
		EnumMap<SubscriptionState, String> stateClasses = new EnumMap<>(SubscriptionState.class);
		stateClasses.put(SubscriptionState.FORMALIZATION, "sub-status-formalization");
		stateClasses.put(SubscriptionState.ACTIVATION_WAITING, "sub-status-formalization");
		stateClasses.put(SubscriptionState.ACTIVE, "sub-status-active");
		stateClasses.put(SubscriptionState.SUSPENDED, "sub-status-suspension");
		stateClasses.put(SubscriptionState.SUSPENDED_FOR_DEBT, "sub-status-suspension");
		stateClasses.put(SubscriptionState.SUSPENDED_ON_DEMAND, "sub-status-suspension");
		stateClasses.put(SubscriptionState.SUSPENSION_FOR_DEBT_WAITING, "sub-status-suspension");
		stateClasses.put(SubscriptionState.SUSPENSION_ON_DEMAND_WAITING, "sub-status-suspension");
		stateClasses.put(SubscriptionState.CLOSURE_WAITING, "sub-status-closing");
		stateClasses.put(SubscriptionState.CLOSED, "sub-status-closing");
		STATE_CLASSES = Collections.unmodifiableMap(stateClasses);
	}

	@Inject
	private PersonalAccountDtoTranslator accountDtoTr;

	@Getter
	private List<PersonalAccountDto> accountDtos = new ArrayList<>();

	public void preRender(List<PersonalAccount> accounts) {
		if (accounts == null || accounts.isEmpty()) {
			accountDtos.clear();
		} else {
			accountDtos = accounts.stream().map(accountDtoTr::translate)
					.sorted(comparing(PersonalAccountDto::getNumber)).collect(toList());
		}
	}

	public Map<SubscriptionState, String> getStateClasses() {
		return STATE_CLASSES;
	}

}
