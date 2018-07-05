package ru.argustelecom.box.env.task.testdata;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.testdata.SubscriptionProvider;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class TaskProvider implements TestDataProvider {

    public static final String TASK = "task.provider.task";
    public static final String ASSIGNEE_NAME = "task.provider.assignee";

    @PersistenceContext
    private EntityManager em;

    @Override
    public void provide(TestRunContext testRunContext) {

        Subscription subscription =
                (Subscription) testRunContext.getBusinessPropertyWithUnmarshalling(SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME);

        String employeeName =
                (String) testRunContext.getBusinessPropertyWithUnmarshalling(BoxLoginProvider.EMPLOYEE_NAME);

        List<Task> tasks = em.createQuery("from Task where subscription = :subscription", Task.class)
                .setParameter("subscription", subscription)
                .getResultList();

        checkArgument(!tasks.isEmpty());

        testRunContext.setBusinessPropertyWithMarshalling(TASK, tasks.get(0));
        testRunContext.setBusinessPropertyWithMarshalling(ASSIGNEE_NAME, employeeName);
    }
}