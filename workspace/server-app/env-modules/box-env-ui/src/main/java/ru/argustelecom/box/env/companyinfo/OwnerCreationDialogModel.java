package ru.argustelecom.box.env.companyinfo;

import static ru.argustelecom.box.env.party.PartyCategory.COMPANY;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.box.env.party.PartyTypeAppService;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

/**
 * <p>
 * Presentation model
 * </p>
 * для диалога создания {@linkplain Owner юридического лица компании}.
 */
@Named(value = "ownerCreationDm")
@PresentationModel
public class OwnerCreationDialogModel implements Serializable {

	@Inject
	private OwnerAppService ownerAs;

	@Inject
	private PartyTypeAppService partyAs;

	@Inject
	private CompanyInfoOwnerDtoTranslator companyInfoOwnerDtoTr;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Setter
	private Callback<CompanyInfoOwnerDto> callbackAfterCreation;

	@Getter
	private OwnerCreationDto ownerCreationDto = new OwnerCreationDto();

	private List<BusinessObjectDto<PartyType>> partyTypes;

	public void openDialog() {
		RequestContext.getCurrentInstance().execute("PF('ownerCreationDlgVar').show()");
		ownerCreationDto.setPartyType(!getPartyTypes().isEmpty() ? getPartyTypes().get(0) : null);
	}

	public void create() {
		//@formatter:off
		Owner owner = ownerAs.create(
							ownerCreationDto.getPartyType().getId(),
							ownerCreationDto.getName(),
							ownerCreationDto.getTaxRate(),
							ownerCreationDto.getQrCodePattern(),
							ownerCreationDto.isPrincipal()
						);
		//@formatter:on
		callbackAfterCreation.execute(companyInfoOwnerDtoTr.translate(owner));
		reset();
	}

	public void cancel() {
		reset();
	}

	private void reset() {
		ownerCreationDto = new OwnerCreationDto();
		callbackAfterCreation = null;
	}

	public List<BusinessObjectDto<PartyType>> getPartyTypes() {
		if (partyTypes == null) {
			partyTypes = businessObjectDtoTr.translate(partyAs.findPartyTypeByCategory(COMPANY));
		}
		return partyTypes;
	}

	public String getTooltip() {
		return ownerAs.getQrCodePatternTooltipHint();
	}

	private static final long serialVersionUID = 5312561607430688894L;
}