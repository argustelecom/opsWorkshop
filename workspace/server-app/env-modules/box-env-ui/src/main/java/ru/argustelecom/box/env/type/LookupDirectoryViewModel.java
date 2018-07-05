package ru.argustelecom.box.env.type;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.address.DirectoryViewModel;
import ru.argustelecom.box.env.type.model.lookup.LookupCategory;
import ru.argustelecom.system.inf.dataaccess.cache.DirectoryCacheService;
import ru.argustelecom.system.inf.page.ViewModel;

public class LookupDirectoryViewModel extends ViewModel implements DirectoryViewModel<LookupCategory> {

	private static final long serialVersionUID = 1L;

	@Inject
	private DirectoryCacheService directoryCacheService;

	@Inject
	private LookupRepository repository;

	@Inject
	private CurrentLookupCategory currentCategory;

	private List<LookupCategory> selectedCategories = new ArrayList<>();

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();

		if (currentCategory.getValue() != null) {
			selectedCategories.add(currentCategory.getValue());
		}
	}

	@Override
	public List<LookupCategory> getTypes() {
		return directoryCacheService.getDirectoryObjects(LookupCategory.class);
	}

	@Override
	public List<LookupCategory> getSelectedTypes() {
		return selectedCategories;
	}

	public void setSelectedTypes(List<LookupCategory> selectedCategory) {
		this.selectedCategories = selectedCategory;
		if (selectedCategories.size() == 1) {
			currentCategory.setValue(selectedCategories.get(0));
		} else {
			currentCategory.setValue(null);
		}
	}

	/* Creation Category Dialog */
	private String name;

	private String description;

	@Override
	public void create() {
		repository.createLookupCategory(name, description);
		cleanCreationParams();
	}

	@Override
	public void remove(LookupCategory directory) {
		currentCategory.setValue(null);
		repository.removeLookupCategory(directory);
	}

	@Override
	public void cleanCreationParams() {
		name = null;
		description = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String categoryName) {
		this.name = categoryName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String categoryDescription) {
		this.description = categoryDescription;
	}
}
