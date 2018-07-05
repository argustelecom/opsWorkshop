package ru.argustelecom.box.env.telephony.tariff.lifecycle.action;

import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.common.AbstractActionWriteComment;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TariffState;

import static ru.argustelecom.box.env.telephony.tariff.lifecycle.AbstractTariffLifecycle.Variables;

@LifecycleBean
public class DoWriteComment extends AbstractActionWriteComment<TariffState, AbstractTariff> {

	@Override
	protected String getContent(ExecutionCtx<TariffState, ? extends AbstractTariff> ctx) {
		return ctx.getVariable(Variables.COMMENT);
	}
}
