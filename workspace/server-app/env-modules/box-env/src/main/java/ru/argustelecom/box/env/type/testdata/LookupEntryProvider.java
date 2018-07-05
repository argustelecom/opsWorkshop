package ru.argustelecom.box.env.type.testdata;

import lombok.val;
import ru.argustelecom.box.env.type.LookupRepository;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class LookupEntryProvider implements TestDataProvider {

    public static final String CATEGORY_NAME = "LookupCategoryProvider.category";
    public static final String ENTRY_NAME = "LookupEntryProvider.entry";

    @Inject
    private LookupRepository lookupRp;

    @Override
    public void provide(TestRunContext testRunContext) {
        val category = lookupRp.createLookupCategory(uniqueId("Тестовая категория"), "Тестовое описание");
        val entry = lookupRp.createLookupEntry(uniqueId("Тестовая категория"), "Тестовое описание", category);

        testRunContext.setBusinessPropertyWithMarshalling(CATEGORY_NAME, category.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(ENTRY_NAME, entry.getObjectName());
    }
}