package ru.argustelecom.box.env.telephony;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionSpecAppService;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionSpec;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@DtoTranslator
public class TelephonyOptionSpecDtoTranslator implements Serializable {

	private static final long serialVersionUID = 8374409154820282009L;

	@Inject
	private TelephonyOptionSpecAppService optionSpecAs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	public TelephonyOptionSpecDto translate(TelephonyOptionSpec spec) {
		BusinessObjectDto<TelephonyOptionType> optionType = businessObjectDtoTr.translate(spec.getType());
		BusinessObjectDto<AbstractTariff> tariff = businessObjectDtoTr.translate(spec.getTariff());
		return new TelephonyOptionSpecDto(spec.getId(), spec.getServiceSpec().getId(), optionType, tariff);
	}

	public List<TelephonyOptionSpecDto> translate(Collection<TelephonyOptionSpec> specs) {
		return specs.stream().map(this::translate).collect(Collectors.toList());
	}

	public List<TelephonyOptionSpecDto> translate(ServiceSpec serviceSpec) {
		List<TelephonyOptionSpec> existentSpecs = optionSpecAs.findByServiceSpec(serviceSpec.getId());
		List<TelephonyOptionType> existentOptionTypes = existentSpecs.stream().map(TelephonyOptionSpec::getType)
				.collect(Collectors.toList());

		Stream<TelephonyOptionSpecDto> newSpecs = serviceSpec.getType().getOptionTypes().stream()
				.filter(optionType -> !existentOptionTypes.contains(optionType)
						&& EntityManagerUtils.initializeAndUnproxy(optionType) instanceof TelephonyOptionType)
				.map(optionType -> {
					TelephonyOptionType telephoneOptionType = (TelephonyOptionType) EntityManagerUtils
							.initializeAndUnproxy(optionType);
					return new TelephonyOptionSpecDto(null, serviceSpec.getId(),
							businessObjectDtoTr.translate(telephoneOptionType), null);
				});

		return Stream.concat(newSpecs, existentSpecs.stream().map(this::translate)).collect(Collectors.toList());

	}
}
