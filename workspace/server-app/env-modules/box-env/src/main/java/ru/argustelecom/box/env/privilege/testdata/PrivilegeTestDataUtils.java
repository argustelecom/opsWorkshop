package ru.argustelecom.box.env.privilege.testdata;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.privilege.PrivilegeRepository;
import ru.argustelecom.box.env.privilege.model.SubscriptionPrivilege;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;

import static ru.argustelecom.box.env.privilege.model.PrivilegeType.TRIAL_PERIOD;

public class PrivilegeTestDataUtils {

    @Inject
    private PrivilegeRepository privilegeRp;

    public SubscriptionPrivilege createTestTrustPeriod(Date validFrom, Date validTo, Subscription subscription) {
        return privilegeRp.createTrustPeriod(validFrom, validTo, subscription);
    }

    public SubscriptionPrivilege createTestTrialPeriod(Date validFrom, Date validTo, Subscription subscription) {
        return privilegeRp.createPrivilege(validFrom, validTo, subscription, TRIAL_PERIOD);
    }

}