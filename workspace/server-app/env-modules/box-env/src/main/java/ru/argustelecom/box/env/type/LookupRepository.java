package ru.argustelecom.box.env.type;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.type.model.lookup.LookupCategory;
import ru.argustelecom.box.env.type.model.lookup.LookupEntry;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class LookupRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public LookupCategory createLookupCategory(String name, String description) {
		LookupCategory category = new LookupCategory(idSequence.nextValue(LookupCategory.class));
		category.setObjectName(name);
		category.setDescription(description);

		em.persist(category);
		return category;
	}

	public void removeLookupCategory(LookupCategory category) {
		em.remove(category);
	}

	public LookupEntry createLookupEntry(String name, String description, LookupCategory category) {
		LookupEntry entry = new LookupEntry(idSequence.nextValue(LookupEntry.class));
		entry.setObjectName(name);
		entry.setDescription(description);
		entry.setCategory(category);

		em.persist(entry);
		return entry;
	}
}
