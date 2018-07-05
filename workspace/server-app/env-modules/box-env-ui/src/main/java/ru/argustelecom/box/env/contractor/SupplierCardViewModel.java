package ru.argustelecom.box.env.contractor;

import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.party.CurrentPartyRole;
import ru.argustelecom.box.env.party.model.role.Supplier;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
@Named(value = "supplierCardVm")
public class SupplierCardViewModel extends ViewModel {

	private static final Logger log = Logger.getLogger(SupplierCardViewModel.class);

	public static final String VIEW_ID = "/views/env/contractor/SupplierCardView.xhtml";

	@Getter
	@Inject
	private CurrentPartyRole currentPartyRole;
	@Inject
	private SupplierDtoTranslator supplierDtoTr;
	@Inject
	private SupplierCardViewState viewState;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		unitOfWork.makePermaLong();
	}

	private void refresh() {
		viewState.setSupplierDto(supplierDtoTr.translate((Supplier) currentPartyRole.getValue()));
		log.debugv("postConstruct. supplier_id={0}", currentPartyRole.getValue());
	}

	private static final long serialVersionUID = -6831267722750390954L;
}