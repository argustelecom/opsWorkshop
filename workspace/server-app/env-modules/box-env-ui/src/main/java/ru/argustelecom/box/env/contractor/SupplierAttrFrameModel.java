package ru.argustelecom.box.env.contractor;

import lombok.Getter;
import java.io.Serializable;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.party.SupplierAppService;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named(value = "supplierAttrFm")
public class SupplierAttrFrameModel implements Serializable {

	@Inject
	private SupplierAppService supplierAs;

	@Getter
	private SupplierDto supplier;

	public void preRender(SupplierDto supplier) {
		if (!Objects.equals(this.supplier, supplier)) {
			this.supplier = supplier;
		}
	}

	public void onNameChanged() {
		supplierAs.changeLegalName(supplier.getId(), supplier.getLegalName());
		supplierAs.changeBrandName(supplier.getId(), supplier.getBrandName());
	}

	private static final long serialVersionUID = 8972439212250130219L;
}