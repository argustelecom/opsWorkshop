package ru.argustelecom.box.env.address;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.system.inf.page.PresentationState;

@PresentationState
public class AddressDirectoryViewState implements Serializable {

	private static final long serialVersionUID = 6350730434616882455L;

	@Getter
	@Setter
	private Location location;

}