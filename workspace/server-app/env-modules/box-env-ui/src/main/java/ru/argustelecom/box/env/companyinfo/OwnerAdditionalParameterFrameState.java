package ru.argustelecom.box.env.companyinfo;

import java.io.Serializable;

import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.system.inf.page.PresentationState;

@Named("ownerAdditionalParameterFs")
@PresentationState
public class OwnerAdditionalParameterFrameState implements Serializable {

	@Getter
	@Setter
	private Long ownerId;

	private static final long serialVersionUID = 780976301763671002L;
}
