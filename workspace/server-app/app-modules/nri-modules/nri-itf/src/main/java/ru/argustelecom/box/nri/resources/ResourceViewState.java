package ru.argustelecom.box.nri.resources;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.system.inf.page.PresentationState;

import java.io.Serializable;

/**
 * Created by s.kolyada on 22.09.2017.
 */
@Getter
@Setter
@PresentationState
public class ResourceViewState implements Serializable {

	private static final long serialVersionUID = 1L;

	private ResourceInstance resource;
}
