package ru.argustelecom.box.env.companyinfo;

import java.io.Serializable;

import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

import ru.argustelecom.system.inf.page.PresentationState;

/**
 * <b>Presentation state</b> для справочника {@linkplain ru.argustelecom.box.env.party.model.role.Owner юридических лиц
 * компании}.
 *
 * @see CompanyInformationViewModel
 */
@Named(value = "companyInfoVs")
@PresentationState
public class CompanyInfoViewState implements Serializable {

	@Getter
	@Setter
	private CompanyInfoOwnerDto ownerDto;

	public boolean isEmpty() {
		return ownerDto != null;
	}

	private static final long serialVersionUID = 7681576602676491638L;

}