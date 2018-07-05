package ru.argustelecom.box.env.mediation.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.function.Function;

import ru.argustelecom.box.env.mediation.nls.MediationMessagesBundle;

import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;

@Getter
@AllArgsConstructor(access = AccessLevel.MODULE)
public enum MediationError {
	//@formatter:off
	MISS_REQUIRED_DATA_IN_CDR(MediationMessagesBundle::requiredDataInSDRError),
	CAN_NOT_DETERMINE_DIRECTION(MediationMessagesBundle::canNotDetermineDirectionError),
	IMPOSSIBLE_APPLY_ADDITIONAL_CONVERSION_RULES(MediationMessagesBundle::impossibleApplyAdditionalConversionRulesError),
	IMPOSSIBLE_IDENTIFY_CUSTOMER_AND_TARIFF(MediationMessagesBundle::impossibleToIdentifyCustomerError),
	IMPOSSIBLE_IDENTIFY_TARIFF(MediationMessagesBundle::impossibleToIdentifyTariffError),
	DIRECTION_FOR_TARIFF_ENTRY_NOT_FOUND(MediationMessagesBundle::directionForTariffEntryNotFoundError),
	SEVERAL_SUITABLE_DIRECTIONS(MediationMessagesBundle::severalDirectionsError),
	GROOVY(MediationMessagesBundle::groovyError),
	UNKNOWN(MediationMessagesBundle::unknownError);
	//@formatter:on

	private Function<MediationMessagesBundle, String> nameGetter;

	public String getName() {
		return nameGetter.apply(getMessages(MediationMessagesBundle.class));
	}
}
