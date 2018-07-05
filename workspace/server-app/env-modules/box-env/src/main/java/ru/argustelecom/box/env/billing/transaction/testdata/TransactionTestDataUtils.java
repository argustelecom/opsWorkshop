package ru.argustelecom.box.env.billing.transaction.testdata;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.reason.UserReasonTypeRepository;
import ru.argustelecom.box.env.billing.reason.model.UserReasonType;
import ru.argustelecom.box.env.billing.transaction.TransactionRepository;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.env.stl.Money;

import javax.inject.Inject;

import static ru.argustelecom.box.env.util.UITestUtils.getOrElse;
import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class TransactionTestDataUtils {

    @Inject
    private TransactionRepository transactionRp;

    @Inject
    private UserReasonTypeRepository userReasonTypeRp;

    public Transaction createTestTransaction(PersonalAccount personalAccount, Money amount) {
        return transactionRp.createUserTransaction(
                personalAccount, amount, findOrCreateTestUserReasonType(), uniqueId()
        );
    }

    private UserReasonType findOrCreateTestUserReasonType() {
        return getOrElse(userReasonTypeRp.getAllUserReasonTypes(),
                () -> userReasonTypeRp.createUserReasonType("Тестовое основание")
        );
    }
}
