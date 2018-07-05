package ru.argustelecom.box.env.billing.provision;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

import ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "provisionTermsId")
public class RecurrentTermsParamsDto {

	private Long provisionTermsId;
	private RecurrentTermsState state;
	private PeriodType periodType;
	private PeriodUnit periodUnit;
	private Integer amount;
	private SubscriptionLifecycleQualifier lifecycleQualifier;
	private Boolean reserveFunds;
	private RoundingPolicy roundingPolicy;
	private Boolean manualControl = false;

	public void setLifecycleQualifier(SubscriptionLifecycleQualifier lifecycleQualifier) {
		this.lifecycleQualifier = lifecycleQualifier;

		if (!Objects.equals(lifecycleQualifier, SubscriptionLifecycleQualifier.FULL)) {
			manualControl = false;
		}
	}
}