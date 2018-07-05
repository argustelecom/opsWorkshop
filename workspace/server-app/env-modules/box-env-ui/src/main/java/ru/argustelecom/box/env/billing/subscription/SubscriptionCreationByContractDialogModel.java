package ru.argustelecom.box.env.billing.subscription;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "subscriptionCreationByContractDm")
@PresentationModel
public class SubscriptionCreationByContractDialogModel
		extends SubscriptionCreationDialogModel<SubscriptionCreationDto> {

	@Inject
	private ContractEntryDtoTranslator contractEntryDtoTr;

	@Getter
	List<ContractEntryDto> contractEntries;

	public void onSubjectCauseSelected() {
		contractEntries = productAs
				.findContractEntriesWithoutSubs(subscriptionCreationDto.getSubjectCause().getCauseId()).stream()
				.map(pe -> contractEntryDtoTr.translate(pe)).collect(toList());
	}

	@Override
	public void onCreationDialogOpened() {
		RequestContext.getCurrentInstance().reset("subscription_creation_by_contract_form");
		RequestContext.getCurrentInstance().update("subscription_creation_by_contract_form");
		RequestContext.getCurrentInstance().execute("PF('subscriptionCreationByContractDlgVar').show()");
	}

	@Override
	public void create() {
		Subscription newSubscription = subscriptionAs.createSubscriptionByContract(personalAccount.getId(),
				subscriptionCreationDto.getContractEntry().getId(), subscriptionCreationDto.getValidFrom(),
				subscriptionCreationDto.getValidTo());
		callback.execute(subscriptionDtoTr.translate(newSubscription));
		clean();
	}

	@Override
	public void clean() {
		super.clean();
		contractEntries = null;
	}

	@Override
	public SubscriptionCreationDto getSubscriptionCreationDto() {
		if (subscriptionCreationDto == null)
			subscriptionCreationDto = new SubscriptionCreationDto();
		return subscriptionCreationDto;
	}

	private static final long serialVersionUID = -3912229006386602659L;

}