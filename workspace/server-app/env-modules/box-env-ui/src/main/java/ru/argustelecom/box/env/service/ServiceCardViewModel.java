package ru.argustelecom.box.env.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionAppService;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.task.TelephonyOptionDto;
import ru.argustelecom.box.env.task.TelephonyOptionDtoTranslator;
import ru.argustelecom.box.integration.nri.SerivicesResourceActionRestrictions;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVATION_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSED;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSURE_WAITING;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.FORMALIZATION;

@Named("serviceCardVm")
@PresentationModel
public class ServiceCardViewModel extends ViewModel {

	private static final long serialVersionUID = 1179211890100601978L;

	@Inject
	private SubscriptionRepository subscriptionRepository;

	@Inject
	private TelephonyOptionAppService telephonyOptionAs;

	@Inject
	private TelephonyOptionDtoTranslator telephonyOptionDtoTr;

	@Inject
	private CurrentService currentService;

	private Location location;
	private Subscription subscription;
	private SerivicesResourceActionRestrictions restrictions;

	private List<TelephonyOptionDto> options;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	public Customer getCustomer() {
		return currentService.getValue().getSubject().getContract().getCustomer();
	}

	public Location getLocation() {
		if (location == null) {
			return location = currentService.getValue().getSubject().getLocations().stream()
					.min(Comparator.comparing(Location::getId)).get();
		}
		return location;
	}

	public Subscription getSubscription() {
		if (subscription == null) {
			return subscription = subscriptionRepository.findSubscription(currentService.getValue().getSubject());
		}
		return subscription;
	}

	public SerivicesResourceActionRestrictions getRestrictions() {
		if (restrictions == null) {
			return restrictions = new ServicesResourceActionRestrictionsImpl(getSubscription());
		}
		return restrictions;
	}

	public List<TelephonyOptionDto> getOptions() {

		options = telephonyOptionDtoTr.translate(telephonyOptionAs.find(currentService.getValue().getId()));

		return options;
	}

	public class ServicesResourceActionRestrictionsImpl implements SerivicesResourceActionRestrictions {

		private Subscription subscription;

		ServicesResourceActionRestrictionsImpl(Subscription subscription) {
			this.subscription = subscription;
		}

		public boolean canBeBooked() {
			return subscription == null || !(subscription.inState(Arrays.asList(CLOSURE_WAITING, CLOSED)));
		}

		public boolean canBeUnbooked() {
			return true;
		}

		public boolean canBeLoaded() {
			return subscription != null && !(subscription
					.inState(Arrays.asList(FORMALIZATION, ACTIVATION_WAITING, CLOSURE_WAITING, CLOSED)));
		}

		public boolean canBeUnloaded() {
			return true;
		}
	}

}
