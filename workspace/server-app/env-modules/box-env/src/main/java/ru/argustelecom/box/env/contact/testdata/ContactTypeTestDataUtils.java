package ru.argustelecom.box.env.contact.testdata;

import ru.argustelecom.box.env.contact.ContactCategory;
import ru.argustelecom.box.env.contact.ContactType;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.dataaccess.cache.DirectoryCacheService;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.List;

public class ContactTypeTestDataUtils implements Serializable {

    private static final long serialVersionUID = -6455378221671803503L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private DirectoryCacheService cacheService;

	private List<ContactType> allContactTypes() {
		return cacheService.getDirectoryObjects(ContactType.class);
	}

	private ContactType findContactType(String contactTypeName, ContactCategory contactCategory) {
		checkArgument(contactTypeName != null, "category is required");
		checkArgument(StringUtils.isNotBlank(contactTypeName), "name is required");

		return allContactTypes().stream().filter(contactType -> contactType.getCategory().equals(contactCategory)
				&& contactType.getName().equals(contactTypeName)).findFirst().orElse(null);
	}

	private ContactType createContactType(String contactTypeName, ContactCategory contactCategory,
			String shortContactTypeName) {
		ContactType newContactType = new ContactType(MetadataUnit.generateId(em));
		newContactType.setCategory(contactCategory);
		newContactType.setName(contactTypeName);
		newContactType.setShortName(shortContactTypeName);
		em.persist(newContactType);
		return newContactType;
	}

	public ContactType findOrCreateContactType(String contactTypeName, ContactCategory contactCategory,
			String shortContactTypeName) {
		ContactType contactType = findContactType(contactTypeName, contactCategory);
		if (contactType == null)
			contactType = createContactType(contactTypeName, contactCategory, shortContactTypeName);
		return contactType;
	}
}