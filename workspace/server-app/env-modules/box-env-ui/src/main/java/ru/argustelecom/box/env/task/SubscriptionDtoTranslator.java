package ru.argustelecom.box.env.task;

import java.util.List;
import java.util.stream.Collectors;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class SubscriptionDtoTranslator {

	public SubscriptionDto translate(Subscription subscription) {
		//@formatter:off
		return SubscriptionDto.builder()
				.client(subscription.getPersonalAccount().getCustomer().getObjectName())
				.number(subscription.getPersonalAccount().getNumber())
				.product(subscription.getSubject().getObjectName())
				.locations(getLocations(subscription.getLocations()))
				.build();
		//@formatter:on
	}

	private List<String> getLocations(List<Location> locations) {
		return locations.stream().map(Location::getFullName).collect(Collectors.toList());
	}

}