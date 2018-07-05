package ru.argustelecom.box.env.contract.lifecycle.validator;

import static ru.argustelecom.box.env.contract.model.ContractState.INFORCE;
import static ru.argustelecom.box.env.contract.model.ContractState.REGISTRATION;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.nls.ServiceMessagesBundle;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionRepository;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.telephony.tariff.TariffEntryService;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveIntersectedPrefixesInServiceTariffs
		implements LifecycleCdiValidator<ContractState, AbstractContract<?>> {

	@Inject
	private TelephonyOptionRepository telephonyOptionRp;

	@Inject
	private TariffEntryService tariffEntrySrv;

	@Override
	public void validate(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx,
			ValidationResult<Object> result) {
		AbstractContract<?> contract = ctx.getBusinessObject();

		contract.getEntries().stream().flatMap(entry -> entry.getOptions().stream()).forEach(option -> {
			Set<AbstractTariff> tariffs = findServiceTariffs(option.getService());
			if (tariffEntrySrv.isIntersectedPrefixesExists(tariffs)) {
				ServiceMessagesBundle messages = LocaleUtils.getMessages(ServiceMessagesBundle.class);
				result.errorv(contract, messages.intersectedPrefixesExistInTariffs(option.getObjectName()));
			}
		});
	}

	private Set<AbstractTariff> findServiceTariffs(Service service) {
		return telephonyOptionRp.find(service).stream().filter(serviceOption -> {
			AbstractContract<?> optionContract = serviceOption.getSubject().getContract();
			if (optionContract.inState(INFORCE) || optionContract.inState(REGISTRATION)) {
				return true;
			}
			return false;
		}).map(TelephonyOption::getTariff).collect(Collectors.toSet());
	}

}
