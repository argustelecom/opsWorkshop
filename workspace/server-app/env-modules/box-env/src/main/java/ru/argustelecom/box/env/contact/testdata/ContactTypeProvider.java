package ru.argustelecom.box.env.contact.testdata;

import java.util.UUID;

import javax.inject.Inject;

import ru.argustelecom.box.env.contact.ContactCategory;
import ru.argustelecom.box.env.contact.ContactType;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

/**
 * Предоставляет тип контакта для проверки правил уникальности типов контакта
 * ContactTypeDirectoryIT#shouldCreateUniqueContactType
 */

public class ContactTypeProvider implements TestDataProvider {

	public static final String CREATED_CONTACT_CATEGORY = "contact.type.provider.contact.category";
	public static final String CREATED_CONTACT_TYPE_NAME = "contact.type.provider.contact.type.name";
		
	@Inject
	private ContactTypeTestDataUtils contactTypeTestDataUtils;

	@Override
	public void provide(TestRunContext testRunContext) {
		String contactTypeName = "Тест " + UUID.randomUUID().toString().substring(0, 16);
		ContactType contactType = contactTypeTestDataUtils.findOrCreateContactType(contactTypeName, ContactCategory.EMAIL, contactTypeName.substring(0, 10));
		String contactCategory = contactType.getCategory().getName();
		testRunContext.setBusinessPropertyWithMarshalling(CREATED_CONTACT_CATEGORY, contactCategory);
		testRunContext.setBusinessPropertyWithMarshalling(CREATED_CONTACT_TYPE_NAME, contactTypeName);
	}
}