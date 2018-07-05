package ru.argustelecom.box.env.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionAppService;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionState;
import ru.argustelecom.box.env.contract.ContractEntryAppService;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("serviceOptionsFm")
@PresentationModel
public class ServiceOptionsFrameModel implements Serializable {

	private static final long serialVersionUID = -301707047616283115L;

	@Inject
	private TelephonyOptionAppService telephonyOptionAs;

	@Inject
	private ContractEntryAppService contractEntryAs;

	@Inject
	TelephonyOptionServiceDtoTranslator telephonyOptionServiceDtoTr;

	@Getter
	private List<TelephonyOptionServiceDto> options;

	public void preRender(Service service) {
		options = telephonyOptionServiceDtoTr.translate(telephonyOptionAs.find(service.getId()));
	}

	public Callback<TelephonyOptionServiceDto> getCallback() {
		return optionServiceDto -> {
			options.removeIf(po -> po.getId().equals(optionServiceDto.getId()));
			options.add(optionServiceDto);
		};
	}

	public boolean canEdit(TelephonyOptionServiceDto optionToEdit) {
		return optionToEdit.getState().equals(TelephonyOptionState.INACTIVE.getName());
	}

	public boolean canRemove(TelephonyOptionServiceDto optionToRemove) {
		return !optionToRemove.isCreatedByProduct()
				&& optionToRemove.getContractState().equals(ContractState.REGISTRATION);
	}

	public void remove(TelephonyOptionServiceDto optionToRemove) {
		contractEntryAs.removeEntry(optionToRemove.getSubject().getId());
		options.remove(optionToRemove);
	}
}
