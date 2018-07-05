package ru.argustelecom.box.env.billing.subscription;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.pricing.PricelistAppService;
import ru.argustelecom.box.env.pricing.PricelistDto;
import ru.argustelecom.box.env.pricing.ProductOfferingDto;
import ru.argustelecom.box.env.pricing.ProductOfferingDtoTranslator;
import ru.argustelecom.system.inf.page.PresentationModel;

/**
 * <b>Не использовать</b>. От создания подписок на основании заявки пока что отказались: BOX-1290. Специально сделан
 * Deprecated.
 */
@Deprecated
@Named(value = "subscriptionCreationByOrderDm")
@PresentationModel
public class SubscriptionCreationByOrderDialogModel
		extends SubscriptionCreationDialogModel<SubscriptionCreationByOrderDto> {

	@Inject
	private PricelistAppService pricelistAs;

	@Inject
	protected ProductOfferingDtoTranslator productOfferingDtoTr;

	private List<PricelistDto> pricelists;

	@Getter
	protected List<ProductOfferingDto> productOfferings;

	public void onPricelistSelected() {
		productOfferings = productAs.findRecurrentProductsByPricelist(subscriptionCreationDto.getPricelist().getId())
				.stream().map(pe -> productOfferingDtoTr.translate(pe)).collect(Collectors.toList());
	}

	@Override
	public void onCreationDialogOpened() {
		RequestContext.getCurrentInstance().update("subscription_creation_by_order_form");
		RequestContext.getCurrentInstance().execute("PF('subscriptionCreationByOrderDlgVar').show()");
	}

	@Override
	public void create() {
		Subscription newSubscription = subscriptionAs.createSubscriptionByOrder(personalAccount.getId(),
				subscriptionCreationDto.getProductOffering().getId(), subscriptionCreationDto.getValidFrom(),
				subscriptionCreationDto.getValidTo());
		callback.execute(subscriptionDtoTr.translate(newSubscription));
		clean();
	}

	@Override
	public void clean() {
		super.clean();
		productOfferings = null;
	}

	public List<PricelistDto> getPricelists() {
		if (pricelists == null) {
			pricelists = pricelistAs.findCommonPricelists(getCustomer().getId()).stream()
					.map(pl -> pricelistDtoTr.translate(pl)).collect(Collectors.toList());
			pricelists.addAll(pricelistAs.findCustomPricelists(getCustomer().getId()).stream()
					.map(pl -> pricelistDtoTr.translate(pl)).collect(Collectors.toList()));
		}
		return pricelists;
	}

	@Override
	public SubscriptionCreationByOrderDto getSubscriptionCreationDto() {
		if (subscriptionCreationDto == null)
			subscriptionCreationDto = new SubscriptionCreationByOrderDto();
		return subscriptionCreationDto;
	}

	private static final long serialVersionUID = 403998914736165898L;

}