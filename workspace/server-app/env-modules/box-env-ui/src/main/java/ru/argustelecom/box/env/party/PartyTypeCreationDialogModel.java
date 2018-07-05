package ru.argustelecom.box.env.party;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "partyTypeCreationDm")
@PresentationModel
public class PartyTypeCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -6080540983649523620L;

	@Inject
	private PartyTypeRepository partyTypeRepository;

	private PartyCategory newCategory;
	private String newName;
	private String newKeyword;
	private String newDescription;

	public void onDialogOpen() {
		RequestContext.getCurrentInstance().execute("PF('partyTypeCreationPanelVar').hide()");
		RequestContext.getCurrentInstance().update("party_type_creation_form");
		RequestContext.getCurrentInstance().execute("PF('partyTypeCreationDlgVar').show()");
	}

	public PartyType create() {
		PartyType nePartyType = partyTypeRepository.createPartyType(newName, newCategory, newKeyword, newDescription);
		cleanParams();
		return nePartyType;
	}

	public void cleanParams() {
		newCategory = null;
		newName = null;
		newKeyword = null;
		newDescription = null;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public PartyCategory getNewCategory() {
		return newCategory;
	}

	public void setNewCategory(PartyCategory newCategory) {
		this.newCategory = newCategory;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewKeyword() {
		return newKeyword;
	}

	public void setNewKeyword(String newKeyword) {
		this.newKeyword = newKeyword;
	}

	public String getNewDescription() {
		return newDescription;
	}

	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

}