package ru.argustelecom.box.env.billing.provision;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "recurrentTermsCreationDm")
@PresentationModel
public class RecurrentTermsCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -8722951289557774802L;

	@Inject
	private ProvisionTermsAppService provisionTermsAs;

	@Setter
	private Callback<RecurrentTerms> callback;

	@Getter
	private ProvisionTermsCreationDto creationDto;

	public void onDialogOpen() {
		RequestContext.getCurrentInstance().update("recurrent_terms_creation_form-recurrent_terms_creation_dlg");
		RequestContext.getCurrentInstance().execute("PF('recurrentTermsCreationDlgVar').show();");

		creationDto = new ProvisionTermsCreationDto();
	}

	public void create() {
		RecurrentTerms recurrentTerms = provisionTermsAs.createRecurrentTerms(creationDto.getName(),
				creationDto.getDescription());
		callback.execute(recurrentTerms);
		cancel();
	}

	public void cancel() {
		callback = null;
		creationDto = null;
	}

	public PeriodType[] getPeriodTypes() {
		return PeriodType.availableValues();
	}

	public SubscriptionLifecycleQualifier[] getLifecycleQualifiers() {
		return SubscriptionLifecycleQualifier.availableSubscriptionLifecycleQualifiers();
	}

	public PeriodUnit[] getPossibleChargingPeriodUnits() {
		return creationDto.getPeriodType() == null ? null : creationDto.getPeriodType().getChargingPeriodUnits();
	}

}