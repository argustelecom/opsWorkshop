package ru.argustelecom.box.env.contract.lifecycle.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.privilege.model.PrivilegeType;
import ru.argustelecom.box.env.stl.period.PeriodDuration;

@Getter
@AllArgsConstructor
public class SubscriptionCreationPresets {

	private PersonalAccount personalAccount;
	private boolean usePrivilege;
	private PrivilegeType privilegeType;
	private PeriodDuration privilegeDuration;

}