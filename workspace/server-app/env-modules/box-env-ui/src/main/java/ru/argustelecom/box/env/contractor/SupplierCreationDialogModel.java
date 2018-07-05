package ru.argustelecom.box.env.contractor;

import lombok.Getter;
import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.PartyCategory;
import ru.argustelecom.box.env.party.PartyTypeAppService;
import ru.argustelecom.box.env.party.SupplierAppService;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.box.env.party.model.role.Supplier;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.page.PresentationModel;

import static ru.argustelecom.box.env.contractor.SupplierCardViewModel.*;

@PresentationModel
@Named(value = "supplierCreationDm")
public class SupplierCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -5390308860072437190L;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private SupplierAppService supplierAs;

	@Inject
	private PartyTypeAppService partyTypeAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Getter
	private SupplierCreationDto newSupplierDto;

	private List<BusinessObjectDto<PartyType>> possiblePartyTypes;

	public void onCreationDialogOpen() {
		if (newSupplierDto == null) {
			newSupplierDto = new SupplierCreationDto();
		}
		RequestContext.getCurrentInstance().execute("PF('supplierCreationDlgVar').show()");
	}

	public String onSupplierCreated() {
		return outcomeConstructor.construct(VIEW_ID,
				IdentifiableOutcomeParam.of("supplier", createSupplier()));
	}

	private Supplier createSupplier() {
		return supplierAs.create(newSupplierDto.getLegalName(), newSupplierDto.getBrandName(),
				newSupplierDto.getType().getId());
	}

	public List<BusinessObjectDto<PartyType>> getPossiblePartyTypes() {
		if (possiblePartyTypes == null) {
			possiblePartyTypes = businessObjectDtoTr.translate(partyTypeAs.findPartyTypeByCategory(PartyCategory.COMPANY));
		}
		return possiblePartyTypes;
	}
}