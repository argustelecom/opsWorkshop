package ru.argustelecom.box.env.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("serviceAttributesFm")
@PresentationModel
public class ServiceAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 6993212771791165356L;

	@Inject
	private ServiceAttributesDtoTranslator translator;

	@Inject
	private CurrentService currentService;

	@Getter
	private ServiceAttributesDto serviceAttributesDto;

	public void preRender() {
		serviceAttributesDto = translator.translate(currentService.getValue());
	}
}
