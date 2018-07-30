package ru.argustelecom.box.env.contact;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.cache.DirectoryCacheService;

@Repository
public class ContactTypeRepository implements Serializable {

	private static final long serialVersionUID = 8880395264498828710L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private DirectoryCacheService cacheService;

	public ContactType createContactType(ContactCategory category, String name, String shortName) {
		checkArgument(category != null, "category is required");
		checkArgument(StringUtils.isNotBlank(name), "name is required");
		checkState(findContactType(category, name) == null, "contactType already exists");

		ContactType newContactType = new ContactType(idSequence.nextValue(ContactType.class));
		newContactType.setCategory(category);
		newContactType.setName(name);
		newContactType.setShortName(shortName);
		em.persist(newContactType);
		return newContactType;
	}

	public List<ContactType> allContactTypes() {
		return cacheService.getDirectoryObjects(ContactType.class);
	}

	public List<ContactType> findContactTypes(ContactCategory category) {
		Predicate<ContactType> condition = contactType -> Objects.equal(contactType.getCategory(), category);
		return cacheService.getDirectoryObjects(ContactType.class, condition);
	}

	public ContactType findContactType(ContactCategory category, String name) {
		checkArgument(category != null, "category is required");
		checkArgument(StringUtils.isNotBlank(name), "name is required");

		return allContactTypes().stream()
				.filter(contactType -> contactType.getCategory().equals(category) && contactType.getName().equals(name))
				.findFirst().orElse(null);
	}

}