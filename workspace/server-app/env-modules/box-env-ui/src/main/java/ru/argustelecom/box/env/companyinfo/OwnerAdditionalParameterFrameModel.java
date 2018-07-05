package ru.argustelecom.box.env.companyinfo;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.party.OwnerParameterAppService;
import ru.argustelecom.box.env.party.model.role.OwnerParameter;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("ownerAdditionalParameterFm")
@PresentationModel
public class OwnerAdditionalParameterFrameModel implements Serializable {

	@Inject
	private OwnerAdditionalParameterFrameState ownerAdditionalParameterFs;

	@Inject
	private OwnerAdditionalParameterDtoTranslator ownerParameterTr;

	@Inject
	private OwnerParameterAppService ownerParameterAs;

	@Getter
	private List<OwnerAdditionalParameterDto> parameters;

	public void preRender(Long ownerId, List<OwnerParameter> parameters) {
		ownerAdditionalParameterFs.setOwnerId(checkNotNull(ownerId));
		//@formatter:off
		this.parameters = checkNotNull(parameters).stream()
				.sorted(Comparator.comparing(OwnerParameter::getOrdinal))
				.map(ownerParameterTr::translate)
				.collect(toList());
		//@formatter:on
	}

	public void remove(OwnerAdditionalParameterDto parameter) {
		ownerParameterAs.remove(ownerAdditionalParameterFs.getOwnerId(), parameter.getId());
		parameters.remove(parameter);
	}

	public Callback<OwnerAdditionalParameterDto> getAddParameterCallback() {
		return parameters::add;
	}

	private static final long serialVersionUID = 4715172240903719556L;
}
