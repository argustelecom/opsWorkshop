package ru.argustelecom.box.env.billing.subscription;

import static javax.enterprise.event.Reception.IF_EXISTS;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceAppService;
import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceDto;
import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceDtoTranslator;
import ru.argustelecom.box.env.billing.privilege.PrivilegeSubjectDto;
import ru.argustelecom.box.env.billing.privilege.PrivilegeSubjectDtoTranslator;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionAppService;
import ru.argustelecom.box.env.privilege.PrivilegeChanged;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.env.service.ServiceDto;
import ru.argustelecom.box.env.service.ServiceDtoTranslator;
import ru.argustelecom.box.env.task.TelephonyOptionDtoTranslator;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "subscriptionCardVm")
@PresentationModel
public class SubscriptionCardViewModel extends ViewModel {

	@Inject
	private LongTermInvoiceAppService invoiceAs;

	@Inject
	private SubscriptionRepository subscriptionRepository;

	@Inject
	private LongTermInvoiceDtoTranslator invoiceDtoTr;

	@Inject
	private PrivilegeSubjectDtoTranslator privilegeSubjectDtoTranslator;

	@Inject
	private SubscriptionDtoTranslator subscriptionDtoTr;

	@Inject
	private CurrentSubscription currentSubscription;

	@Inject
	private ServiceDtoTranslator serviceDtoTranslator;

	@Inject
	private TelephonyOptionAppService telephonyOptionAs;

	@Inject
	private TelephonyOptionDtoTranslator telephonyOptionDtoTr;

	@Getter
	private Subscription subscription;

	@Getter
	private SubscriptionDto subscriptionDto;

	private List<LongTermInvoiceDto> invoices;

	@Getter
	private List<ServiceDto> services = new ArrayList<>();

	@PostConstruct
	@Override
	public void postConstruct() {
		super.postConstruct();
		subscription = currentSubscription.getValue();
		subscriptionDto = subscriptionDtoTr.translate(subscription);
		services = serviceDtoTranslator.translate(subscriptionRepository.findServicesBySubscription(subscription));

		unitOfWork.makePermaLong();
	}

	public PrivilegeSubjectDto getPrivilegeSubject() {
		return privilegeSubjectDtoTranslator.translate(subscription);
	}

	public List<LongTermInvoiceDto> getInvoices() {
		if (invoices == null)
			initInvoices();
		return invoices;
	}

	void refreshInvoiceEntries(@Observes(notifyObserver = IF_EXISTS) @PrivilegeChanged Privilege privilege) {
		initInvoices();
		subscriptionDto = subscriptionDtoTr.translate(subscription);
	}

	private void initInvoices() {
		invoices = invoiceAs.findInvoicesForSubscription(subscriptionDto.getId()).stream()
				.sorted((i1, i2) -> i2.getEndDate().compareTo(i1.getEndDate())).map(ie -> invoiceDtoTr.translate(ie))
				.collect(Collectors.toList());
	}

	private static final long serialVersionUID = -455725035051118976L;

}