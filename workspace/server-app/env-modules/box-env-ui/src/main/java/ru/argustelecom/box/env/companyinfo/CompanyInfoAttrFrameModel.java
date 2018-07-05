package ru.argustelecom.box.env.companyinfo;

import java.io.Serializable;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

/**
 * <b>Presentation model</b> для функционального блока атрибутов юр. лица.
 */
@Named(value = "companyInfoAttrFm")
@PresentationModel
public class CompanyInfoAttrFrameModel implements Serializable {

	@Inject
	private OwnerAppService ownerAs;

	@Getter
	private CompanyInfoOwnerDto owner;

	private Callback<CompanyInfoOwnerDto> callbackAfterChangePrincipal;

	public void preRender(CompanyInfoOwnerDto owner, Callback<CompanyInfoOwnerDto> callbackAfterChangePrincipal) {
		if (!Objects.equals(this.owner, owner)) {
			this.owner = owner;
		}

		if (!Objects.equals(this.callbackAfterChangePrincipal, callbackAfterChangePrincipal)) {
			this.callbackAfterChangePrincipal = callbackAfterChangePrincipal;
		}
	}

	public void onNameChanged() {
		ownerAs.changeName(owner.getId(), owner.getName());
	}

	public void onQrCodePatternChanged() {
		ownerAs.changeQrCodePattern(owner.getId(), owner.getQrCodePattern());
	}

	public void markPrincipal() {
		ownerAs.markPrincipal(owner.getId());
		owner.setPrincipal(true);
		callbackAfterChangePrincipal.execute(owner);
	}

	public String getTooltip() {
		return ownerAs.getQrCodePatternTooltipHint();
	}

	private static final long serialVersionUID = 5646516483640783642L;

}