package ru.argustelecom.box.env.privilege.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface PrivilegeMessagesBundle {

    @Message("Доверительный период с %s по %s")
    String trustPeriodFromTo(String from, String to);
    
    @Message("Пробный период с %s по %s")
    String trialPeriodFromTo(String from, String to);

    @Message("%s для клиента %s")
    String privilegeForCustomer(String trustPeriod, String customer);

    @Message("%s для лицевого счёта %s")
    String privilegeForPersonalAccount(String trustPeriod, String account);

    @Message("%s для подписки %s")
    String privilegeForSubscription(String trustPeriod, String subscription);

}