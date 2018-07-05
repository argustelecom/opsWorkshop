package ru.argustelecom.box.env.page.navigation;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Named
@ApplicationScoped
public class CardNavigator implements Serializable {

	private static final long serialVersionUID = 1836636826651451614L;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	public String getOutcome(Identifiable value) {
		Identifiable unproxyValue = unproxy(value);
		return outcomeConstructor.construct(getViewId(unproxyValue), IdentifiableOutcomeParam.of(getParamName(unproxyValue), unproxyValue));
	}

	public boolean isDisabled(Identifiable value) {
		return value == null || !EmployeePrincipal.instance().getPermissionIds().contains(getPermissionId(unproxy(value)));
	}

	private Identifiable unproxy(Identifiable value) {
		return EntityManagerUtils.initializeAndUnproxy(value);
	}

	private String getViewId(Identifiable value) {
		CardType cardType = findCardType(value);
		return cardType.getViewId();
	}

	private String getParamName(Identifiable value) {
		CardType cardType = findCardType(value);
		return cardType.getParamName();
	}

	private String getPermissionId(Identifiable value) {
		CardType cardType = findCardType(value);
		return cardType.getPermissionId();
	}

	private CardType findCardType(Identifiable value) {
		return CardType.findByIdentifiable(value);
	}

}
