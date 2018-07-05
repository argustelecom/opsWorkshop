package ru.argustelecom.box.env.billing.account;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class SubscriptionDtoTranslator implements DefaultDtoTranslator<SubscriptionDto, Subscription> {

	//@formatter:off
	
	@Override
	public SubscriptionDto translate(Subscription subscription) {
		return SubscriptionDto.builder()
			.id(subscription.getId())
			.state(subscription.getState())
			.productName(subscription.getSubject().getObjectName())
			.build();
	}
}
