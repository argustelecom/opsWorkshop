package ru.argustelecom.box.env.pricing.lifecycle.validator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.val;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.PricelistState;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.pricing.model.ProductOffering.ProductOfferingQuery;
import ru.argustelecom.box.env.pricing.nls.PricelistMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustBeEmpty implements LifecycleCdiValidator<PricelistState, AbstractPricelist> {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void validate(ExecutionCtx<PricelistState, ? extends AbstractPricelist> ctx, ValidationResult<Object> result) {
		val pricelist = ctx.getBusinessObject();

		ProductOfferingQuery<ProductOffering> query = new ProductOfferingQuery<>(ProductOffering.class);
		query.and(query.pricelist().equal(pricelist));

		val offeringsCount = query.calcRowsCount(em);
		if (offeringsCount > 0) {
			PricelistMessagesBundle messages = LocaleUtils.getMessages(PricelistMessagesBundle.class);
			result.errorv(pricelist, messages.containsEntries(offeringsCount.toString()));
		}
	}
}
