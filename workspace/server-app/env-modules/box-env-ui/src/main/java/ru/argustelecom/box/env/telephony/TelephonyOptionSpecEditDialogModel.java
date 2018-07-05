package ru.argustelecom.box.env.telephony;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.tuple.Pair;

import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.commodity.nls.ServiceMessagesBundle;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionSpecAppService;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.telephony.tariff.TariffAppService;
import ru.argustelecom.box.env.telephony.tariff.TariffEntryAppService;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("telephonyOptionSpecEditDm")
@PresentationModel
public class TelephonyOptionSpecEditDialogModel implements Serializable {

	private static final long serialVersionUID = -7456840721704511995L;

	@Inject
	private TelephonyOptionSpecAppService optionSpecAs;

	@Inject
	private TariffAppService tariffAs;

	@Inject
	private TariffEntryAppService tariffEntryAs;

	@Inject
	private TelephonyOptionSpecDtoTranslator optionSpecDtoTr;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	private ServiceSpec serviceSpec;

	@Getter
	private List<TelephonyOptionSpecDto> optionSpecs;

	@Getter
	private List<TelephonyOptionSpecDto> selectedOptionSpecs;

	private List<BusinessObjectDto<CommonTariff>> possibleTariffs;

	@Setter
	private Callback<Pair<Long, List<TelephonyOptionSpecDto>>> callback;

	@Getter
	@Setter
	private boolean singleTariff;

	@Getter
	private BusinessObjectDto<AbstractTariff> selectedSingleTariff;

	public void submit() {
		if (!checkTariff(selectedOptionSpecs) || !checkNotIntersectionsInPrefixes(selectedOptionSpecs)) {
			clear();
			return;
		}

		optionSpecs.stream().filter(spec -> spec.getId() != null && !selectedOptionSpecs.contains(spec))
				.forEach(spec -> optionSpecAs.remove(spec.getId()));

		callback.execute(Pair.of(serviceSpec.getId(), selectedOptionSpecs.stream().map(spec -> {
			if (spec.getId() == null) {
				return optionSpecDtoTr.translate(optionSpecAs.create(spec.getOptionType().getId(),
						spec.getServiceSpecId(), spec.getTariff().getId()));
			} else {
				return optionSpecDtoTr.translate(optionSpecAs.changeTariff(spec.getId(), spec.getTariff().getId()));
			}
		}).collect(Collectors.toList())));

		clear();
	}

	public void clear() {
		serviceSpec = null;
		optionSpecs = null;
		possibleTariffs = null;
		selectedOptionSpecs = null;
		singleTariff = false;
		selectedSingleTariff = null;
	}

	public List<BusinessObjectDto<CommonTariff>> getPossibleTariffs() {
		if (possibleTariffs == null) {
			possibleTariffs = businessObjectDtoTr.translate(tariffAs.findAvailableCommonTariffs());
		}

		return possibleTariffs;
	}

	public void setSelectedSingleTariff(BusinessObjectDto<AbstractTariff> tariff) {
		if (!Objects.equals(selectedSingleTariff, tariff)) {
			selectedSingleTariff = tariff;
			selectedOptionSpecs.forEach(spec -> spec.setTariff(tariff));
		}
	}

	public void setServiceSpec(ServiceSpec serviceSpec) {
		this.serviceSpec = serviceSpec;
		optionSpecs = optionSpecDtoTr.translate(serviceSpec);
		selectedOptionSpecs = optionSpecs.stream().filter(spec -> spec.getId() != null).collect(Collectors.toList());
	}

	public void setSelectedOptionSpecs(List<TelephonyOptionSpecDto> selectedOptionSpecs) {
		this.selectedOptionSpecs = selectedOptionSpecs;
		optionSpecs.stream().filter(optionSpec -> !selectedOptionSpecs.contains(optionSpec)).forEach(this::resetTariff);
	}

	private void resetTariff(TelephonyOptionSpecDto optionSpec) {
		optionSpec.setTariff(null);
	}

	private boolean checkTariff(Collection<TelephonyOptionSpecDto> optionSpecs) {
		Optional<TelephonyOptionSpecDto> optionSpecWithoutTariff = optionSpecs.stream()
				.filter(specOption -> specOption.getTariff() == null).findFirst();
		if (optionSpecWithoutTariff.isPresent()) {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			ServiceMessagesBundle serviceMessages = LocaleUtils.getMessages(ServiceMessagesBundle.class);
			Notification.error(overallMessages.error(), serviceMessages
					.serviceOptionNotSpecifyTariff(optionSpecWithoutTariff.get().getOptionType().getName()));
			return false;
		}
		return true;
	}

	private boolean checkNotIntersectionsInPrefixes(Collection<TelephonyOptionSpecDto> optionSpecs) {
		if (optionSpecs.size() < 2) {
			return true;
		}
		if (tariffEntryAs.isIntersectedPrefixesExists(
				optionSpecs.stream().map(optionSpec -> optionSpec.getTariff().getId()).collect(Collectors.toSet()))) {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			TariffMessagesBundle tariffMessages = LocaleUtils.getMessages(TariffMessagesBundle.class);
			Notification.error(overallMessages.error(), tariffMessages.intersectedPrefixesExistInTariffs());
			return false;
		}
		return true;
	}

}
