package ru.argustelecom.box.env.billing.subscription;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceRepository;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.provision.ProvisionTermsDtoTranslator;
import ru.argustelecom.box.env.billing.subscription.model.PricelistCostCause;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.env.service.ServiceDto;
import ru.argustelecom.box.env.service.ServiceDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class SubscriptionDtoTranslator {

	private static final String PRIVILEGE_INFO_TEMPLATE = "%s c %s по %s";

	@Inject
	private SubjectCauseDtoTranslator subjectCauseDtoTr;

	@Inject
	private ServiceDtoTranslator serviceDtoTr;

	@Inject
	private ProvisionTermsDtoTranslator provisionTermsDtoTr;

	@Inject
	private SubscriptionRepository subscriptionRp;

	@Inject
	private LongTermInvoiceRepository invoiceRp;

	public SubscriptionDto translate(Subscription subscription) {
		PricelistCostCause costCause = (PricelistCostCause) subscription.getCostCause();
		//@formatter:off
		return SubscriptionDto.builder()
					.id(subscription.getId())
					.productName(subscription.getSubject().getObjectName())
					.provisionTerms(provisionTermsDtoTr.translate(subscription.getProvisionTerms()))
					.subjectCause(subjectCauseDtoTr.translate(subscription.getSubjectCause()))
					.cost(subscription.getCost())
					.pricelistId(costCause.getPricelist().getId())
					.costCauseName(costCause.getObjectName())
					.state(subscription.getState())
					.validFrom(subscription.getValidFrom())
					.validTo(subscription.getValidTo())
					.locations(subscription.getLocations().stream().map(Location::getFullName).collect(toList()))
					.services(translateServices(subscription))
					.privilegeInfo(collectPrivilegeInfo(subscription))
				.build();
		//@formatter:on
	}

	private List<ServiceDto> translateServices(Subscription subscription) {
		return subscriptionRp.findServicesBySubscription(subscription).stream().map(serviceDtoTr::translate)
				.sorted(Comparator.comparing(ServiceDto::getName)).collect(Collectors.toList());
	}

	private String collectPrivilegeInfo(Subscription subscription) {
		LongTermInvoice lastInvoice = invoiceRp.findLastInvoice(subscription, false);
		if (lastInvoice == null) {
			return null;
		}
		return Optional.ofNullable(lastInvoice.getPrivilege()).map(Privilege::getObjectName).orElse(null);
	}

}