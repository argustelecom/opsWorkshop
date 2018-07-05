package ru.argustelecom.box.env.pricing;

import ru.argustelecom.box.env.pricing.model.MeasuredProductOffering;
import ru.argustelecom.box.env.pricing.model.PeriodProductOffering;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;

public abstract class AbstractProductOfferingDtoTranslator<DTO> {

	void fillVolume(ProductOffering productOffering, DTO productOfferingDto) {
		ProductOffering unproxyProductOffering = EntityManagerUtils.initializeAndUnproxy(productOffering);
		if (unproxyProductOffering instanceof PeriodProductOffering) {
			fillPeriodVolume((PeriodProductOffering) unproxyProductOffering, productOfferingDto);
		} else if (unproxyProductOffering instanceof MeasuredProductOffering) {
			fillMeasureVolume((MeasuredProductOffering) unproxyProductOffering, productOfferingDto);
		} else {
			throw new SystemException(String.format("Unsupported product offering type: '%s'",
					productOffering.getClass().getSimpleName()));
		}
	}

	abstract void fillPeriodVolume(PeriodProductOffering productOffering, DTO productOfferingDto);

	abstract void fillMeasureVolume(MeasuredProductOffering productOffering, DTO productOfferingDto);

}