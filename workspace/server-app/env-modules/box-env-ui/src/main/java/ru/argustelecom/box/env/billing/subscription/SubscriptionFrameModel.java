package ru.argustelecom.box.env.billing.subscription;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("subscriptionFm")
@PresentationModel
public class SubscriptionFrameModel implements Serializable {

	private static final long serialVersionUID = 8188734854141753081L;

	@Inject
	private SubscriptionDtoTranslator translator;

	@Getter
	private SubscriptionDto subscription;

	public void preRender(Subscription identifiable) {
		subscription = translator.translate(identifiable);
	}
}
