package ru.argustelecom.box.env.pricing.lifecycle.validator;

import java.util.Date;

import lombok.val;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.PricelistState;
import ru.argustelecom.box.env.pricing.nls.PricelistMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustStillValid implements LifecycleCdiValidator<PricelistState, AbstractPricelist> {

	@Override
	public void validate(ExecutionCtx<PricelistState, ? extends AbstractPricelist> ctx,
			ValidationResult<Object> result) {
		val pricelist = ctx.getBusinessObject();
		val currentDate = new Date();
		if (pricelist.getValidTo() != null && currentDate.after(pricelist.getValidTo())) {
			PricelistMessagesBundle messages = LocaleUtils.getMessages(PricelistMessagesBundle.class);
			result.error(pricelist, messages.pricelistExpired());
		}
	}
}
