package ru.argustelecom.box.env.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;

import ru.argustelecom.box.env.address.DirectoryViewModel;
import ru.argustelecom.box.env.type.model.lookup.LookupCategory;
import ru.argustelecom.box.env.type.model.lookup.LookupEntry;
import ru.argustelecom.system.inf.dataaccess.cache.DirectoryCacheService;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class LookupEntriesFrameModel implements Serializable, DirectoryViewModel<LookupEntry>{

	private static final long serialVersionUID = 1L;

	@Inject
	private DirectoryCacheService directoryCacheService;

	@Inject
	private LookupRepository repository;

	@PersistenceContext
	private EntityManager em;

	@Inject
	CurrentLookupCategory currentCategory;

	private LookupCategory category;

	private List<LookupEntry> selectedEntries = new ArrayList<>();

	private boolean showAllEntries;

	@PostConstruct
	public void postConstruct() {
		preRender();
	}

	public void preRender() {
		if (currentCategory.changed(category)) {
			category = currentCategory.getValue();
			selectedEntries = null;
		}
	}

	@Override
	public List<LookupEntry> getTypes() {
		if (category != null) {
			if (showAllEntries) {
				Predicate<LookupEntry> predicate = le -> Objects.equal(le.getCategory(), category);
				return directoryCacheService.getDirectoryObjects(LookupEntry.class, predicate);
			}
			return category.getPossibleValues(em);
		}
		return Collections.emptyList();
	}

	@Override
	public List<LookupEntry> getSelectedTypes() {
		return selectedEntries;
	}

	@Override
	public void remove(LookupEntry directory) {
		directory.setActive(false);
	}

	public void toggleActive() {
		selectedEntries.forEach(e -> e.setActive(!e.isActive()));
		selectedEntries.clear();
	}

	public void setSelectedTypes(List<LookupEntry> selectedEntries) {
		this.selectedEntries = selectedEntries;
	}

	public boolean isShowAllEntries() {
		return showAllEntries;
	}

	public void setShowAllEntries(boolean showAllEntries) {
		this.showAllEntries = showAllEntries;
	}



	/*Creation dialog*/
	private String name;

	private String description;

	@Override
	public void create() {
		repository.createLookupEntry(name, description, category);
		cleanCreationParams();
	}

	@Override
	public void cleanCreationParams() {
		name = null;
		description = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String entryName) {
		this.name = entryName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
