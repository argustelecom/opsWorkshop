package ru.argustelecom.box.env.address;

import java.io.Serializable;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "addressFm")
@PresentationModel
public class AddressFrameModel implements Serializable {

	private static final String EMPTY_COVERAGE_COLOR = "red";

	@Inject
	private AddressDtoTranslator addressDtoTr;

	private Location location;

	@Getter
	private AddressDto address;

	public void preRender(Location location) {
		if (!Objects.equals(this.location, location))
			refresh(location);
	}

	public boolean hasCoverage() {
		return address != null && address.getCoverageId() != null;
	}

	public String getAddressTitle() {
		if (hasCoverage()) {
			return address.getCoverageStateName();
		} else {
			return LocaleUtils.getLocalizedMessage("{ProvisionAddressBundle:box.provision.address.empty}", getClass());
		}
	}

	public String addressColor() {
		return hasCoverage() ? address.getCoverageStateColor() : EMPTY_COVERAGE_COLOR;
	}

	private void refresh(Location location) {
		this.location = location;
		address = addressDtoTr.translate(location);
	}

	private static final long serialVersionUID = -4311497703566594485L;

}