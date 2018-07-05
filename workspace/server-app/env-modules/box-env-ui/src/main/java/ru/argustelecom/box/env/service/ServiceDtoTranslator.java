package ru.argustelecom.box.env.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.val;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionRepository;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.task.TelephonyOptionDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ServiceDtoTranslator implements DefaultDtoTranslator<ServiceDto, Service> {

	@Inject
	private TelephonyOptionDtoTranslator telephonyOptionDtoTr;

	@Inject
	private TelephonyOptionRepository telephonyOptionRp;

	@Override
	public ServiceDto translate(Service service) {
		val options = telephonyOptionDtoTr.translate(telephonyOptionRp.find(service));
		return new ServiceDto(service.getId(), service.getObjectName(), service.getState(), options);
	}

	public List<ServiceDto> translate(List<Service> services) {
		return services.stream().map(this::translate).collect(Collectors.toList());
	}

}