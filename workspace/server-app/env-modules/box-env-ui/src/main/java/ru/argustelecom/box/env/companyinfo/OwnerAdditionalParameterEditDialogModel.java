package ru.argustelecom.box.env.companyinfo;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.party.OwnerParameterAppService;
import ru.argustelecom.box.env.party.model.role.OwnerParameter;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("ownerAdditionalParameterEditDm")
@PresentationModel
public class OwnerAdditionalParameterEditDialogModel implements Serializable {

	@Inject
	private OwnerAdditionalParameterFrameState ownerAdditionalParameterFs;

	@Inject
	private OwnerParameterAppService ownerParameterAs;

	@Inject
	private OwnerAdditionalParameterDtoTranslator ownerAdditionalParameterDtoTr;

	@Getter
	@Setter
	private OwnerAdditionalParameterDto parameter = new OwnerAdditionalParameterDto();

	@Setter
	private Callback<OwnerAdditionalParameterDto> addParameterCallback;

	public void submit() {
		if (isNewParam()) {
			OwnerParameter createdParameter = ownerParameterAs.create(ownerAdditionalParameterFs.getOwnerId(),
					parameter.getKeyword(), parameter.getName(), parameter.getValue());
			addParameterCallback.execute(ownerAdditionalParameterDtoTr.translate(createdParameter));
		} else {
			ownerParameterAs.change(parameter.getId(), parameter.getKeyword(), parameter.getName(),
					parameter.getValue());
		}
		reset();
	}

	public void cancel() {
		reset();
	}

	public boolean isNewParam() {
		return parameter.getId() == null;
	}

	private void reset() {
		parameter = new OwnerAdditionalParameterDto();
	}

	private static final long serialVersionUID = 6054324513676575293L;
}