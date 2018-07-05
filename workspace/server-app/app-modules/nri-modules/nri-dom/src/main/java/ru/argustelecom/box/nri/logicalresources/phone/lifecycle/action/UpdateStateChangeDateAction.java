package ru.argustelecom.box.nri.logicalresources.phone.lifecycle.action;

import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;

import java.util.Date;

/**
 * Действие по обновлению времени изменения состояния телефонного номера
 * Created by s.kolyada on 27.10.2017.
 */
@LifecycleBean
public class UpdateStateChangeDateAction implements LifecycleCdiAction<PhoneNumberState, PhoneNumber> {

	@Override
	public void execute(ExecutionCtx<PhoneNumberState, ? extends PhoneNumber> ctx) {
		PhoneNumber invoice = ctx.getBusinessObject();
		invoice.setStateChangeDate(new Date());
	}
}
