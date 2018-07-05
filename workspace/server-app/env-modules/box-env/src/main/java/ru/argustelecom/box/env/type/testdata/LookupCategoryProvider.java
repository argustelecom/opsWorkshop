package ru.argustelecom.box.env.type.testdata;

import lombok.val;
import ru.argustelecom.box.env.type.LookupRepository;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class LookupCategoryProvider implements TestDataProvider {

    public static final String CATEGORY_NAME = "LookupCategoryProvider.category";

    @Inject
    private LookupRepository lookupRp;

    @Override
    public void provide(TestRunContext testRunContext) {
        val category = lookupRp.createLookupCategory(uniqueId("Тестовая категория"), "Тестовое описание");
        testRunContext.setBusinessPropertyWithMarshalling(CATEGORY_NAME, category.getObjectName());
    }
}