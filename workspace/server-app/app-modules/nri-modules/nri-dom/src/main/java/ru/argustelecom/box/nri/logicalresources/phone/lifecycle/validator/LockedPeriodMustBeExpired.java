package ru.argustelecom.box.nri.logicalresources.phone.lifecycle.validator;

import lombok.val;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.validator.nls.LockedPeriodMustBeExpiredMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.system.inf.validation.ValidationResult;

import java.util.Date;

/**
 * Проверка, что прошёл период блокировки номера, после того, как он был забран у клиента
 * Created by s.kolyada on 27.10.2017.
 */
@LifecycleBean
public class LockedPeriodMustBeExpired implements LifecycleCdiValidator<PhoneNumberState, PhoneNumber> {


	@Override
	public void validate(ExecutionCtx<PhoneNumberState, ? extends PhoneNumber> ctx, ValidationResult<Object> result) {
		val phoneNumber = ctx.getBusinessObject();
		Date stateChanged = phoneNumber.getStateChangeDate();
		// TODO настраиваемые правила для периода разблокировки
		Days period = Days.daysBetween(LocalDate.fromDateFields(stateChanged), LocalDate.fromDateFields(new Date()));

		if (phoneNumber.getSpecInstance() != null &&
				phoneNumber.getSpecInstance().getType() != null &&
				phoneNumber.getSpecInstance().getType().getBlockedInterval() != 0 &&
				phoneNumber.getState() == PhoneNumberState.LOCKED &&
				period.getDays() < phoneNumber.getSpecInstance().getType().getBlockedInterval()) {
			result.error(phoneNumber, LocaleUtils.getMessages(LockedPeriodMustBeExpiredMessagesBundle.class).message());
		}
	}
}
