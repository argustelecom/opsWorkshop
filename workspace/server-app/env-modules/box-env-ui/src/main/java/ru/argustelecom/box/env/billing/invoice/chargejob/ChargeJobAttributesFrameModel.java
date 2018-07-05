package ru.argustelecom.box.env.billing.invoice.chargejob;

import lombok.Getter;
import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.lifecycle.impl.LifecycleHistoryAppService;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("chargeJobAttrFm")
public class ChargeJobAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 75671777633420812L;

	@Getter
	private ChargeJobDto chargeJob;

	@Inject
	private LifecycleHistoryAppService lifecycleHistoryAs;

	public void preRender(ChargeJobDto chargeJob) {
		this.chargeJob = chargeJob;
	}
}
