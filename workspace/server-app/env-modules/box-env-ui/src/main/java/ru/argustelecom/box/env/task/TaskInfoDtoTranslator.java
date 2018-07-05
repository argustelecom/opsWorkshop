package ru.argustelecom.box.env.task;

import java.util.stream.Collectors;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class TaskInfoDtoTranslator {

	public TaskInfoDto translate(Task task) {
		Subscription subscription = task.getSubscription();
		PersonalAccount personalAccount = subscription.getPersonalAccount();
		//@formatter:off
		return TaskInfoDto.builder()
				.customer(personalAccount.getCustomer().getObjectName())
				.personalAccountNumber(personalAccount.getNumber())
				.product(subscription.getSubject().getObjectName())
				.addresses(subscription.getLocations().stream().map(Location::getFullName).collect(Collectors.toList()))
			.build();
		//@formatter:on
	}

}
