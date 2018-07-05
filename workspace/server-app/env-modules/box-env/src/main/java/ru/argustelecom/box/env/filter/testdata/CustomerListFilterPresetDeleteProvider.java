package ru.argustelecom.box.env.filter.testdata;

import com.google.common.collect.Sets;
import ru.argustelecom.box.env.filter.ListFilterPresetRepository;
import ru.argustelecom.box.env.filter.model.FilterParam;
import ru.argustelecom.box.env.filter.model.ListFilterPreset;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.login.EmployeePrincipal;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.google.common.base.Preconditions.checkArgument;

public class CustomerListFilterPresetDeleteProvider implements TestDataProvider {

    public final static String LIST_FILTER_PRESET = "customer.list.filter.preset.provider.preset";

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ListFilterPresetRepository listFilterPresetRepository;

    @Override
    public void provide(TestRunContext testRunContext) {

        EmployeePrincipal principal = EmployeePrincipal.instance();
        checkArgument(principal != null);

        Employee owner = em.getReference(Employee.class, principal.getEmployeeId());
        checkArgument(owner != null);

        // т.к. при запуске теста создается новый пользователь, чтобы эти тесты выполнять, то можно быть точно уверенным,
        // что для этого пользователя не будет никакого фильтра, поэтому искать нет смысла
        ListFilterPreset preset = listFilterPresetRepository.create(
                "Тестовый фильтр",
                owner,
                "CustomerListView.xhtml",
                Sets.newHashSet(FilterParam.create("String", "Object"))
        );
        testRunContext.setBusinessPropertyWithMarshalling(LIST_FILTER_PRESET, preset.getName());
    }
}
