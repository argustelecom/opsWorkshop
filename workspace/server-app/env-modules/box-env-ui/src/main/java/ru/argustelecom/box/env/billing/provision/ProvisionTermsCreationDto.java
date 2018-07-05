package ru.argustelecom.box.env.billing.provision;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;

@Getter
@Setter
@NoArgsConstructor
public class ProvisionTermsCreationDto {

	private String name;
	private String description;
	private SubscriptionLifecycleQualifier lifecycleQualifier;
	private PeriodType periodType;
	private PeriodUnit periodUnit;
	private Integer amount;

}