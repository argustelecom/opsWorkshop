package ru.argustelecom.box.env.contractor;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.party.PartyTypeAppService;
import ru.argustelecom.box.env.party.SupplierAppService;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.system.inf.page.PresentationModel;

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
}