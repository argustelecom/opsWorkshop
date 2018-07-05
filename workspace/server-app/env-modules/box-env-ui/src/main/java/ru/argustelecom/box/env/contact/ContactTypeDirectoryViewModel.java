package ru.argustelecom.box.env.contact;

import static ru.argustelecom.system.inf.modelbase.NamedObject.BY_OBJECT_NAME;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "contactTypeDirectoryVM")
@PresentationModel
public class ContactTypeDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = 1539341276729086000L;

	@Inject
	private ContactTypeRepository contactTypeRepository;

	private List<ContactType> types;
	private List<ContactType> selectedTypes;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	public List<ContactType> getTypes() {
		if (types == null)
			types = contactTypeRepository.allContactTypes();
		return types;
	}

	public void remove() {
		selectedTypes.forEach(type -> {
			em.remove(type);
			types.remove(type);
		});
	}

	public List<ContactType> getSelectedTypes() {
		return selectedTypes;
	}

	public Callback<ContactType> getCallback() {
		return newContactType -> {
			types.add(newContactType);
			Collections.sort(types, BY_OBJECT_NAME);
		};
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public void setSelectedTypes(List<ContactType> selectedTypes) {
		this.selectedTypes = selectedTypes;
	}

}