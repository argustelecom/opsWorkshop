package ru.argustelecom.box.env.billing.invoice.chargejob;

import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
@Named("chargeJobCardVm")
public class ChargeJobCardViewModel extends ViewModel {

	private static final long serialVersionUID = 3357849459517782795L;

	private static final Logger log = Logger.getLogger(ChargeJobCardViewModel.class);

	public static final String VIEW_ID = "/views/env/billing/invoice/chargejob/ChargeJobCardView.xhtml";

	@Getter
	@Inject
	private CurrentChargeJob currentChargeJob;

	@Inject
	private ChargeJobDtoTranslator chargeJobDtoTr;

	@Inject
	private ChargeJobCardViewState chargeJobCardVs;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		unitOfWork.makePermaLong();
	}

	private void refresh() {
		ChargeJobDto chargeJobDto = chargeJobDtoTr.translate(currentChargeJob.getValue());
		chargeJobCardVs.setChargeJob(chargeJobDto);
		log.debugv("postConstruct. charge_job_id={0}", currentChargeJob.getValue());

		if (chargeJobDto.getFilter().getServiceId() != null) {
			chargeJobCardVs.setService(getServiceById(chargeJobDto.getFilter().getServiceId()));
		}

		if (chargeJobDto.getFilter().getTariffId() != null) {
			chargeJobCardVs.setTariff(getTariffById(chargeJobDto.getFilter().getTariffId()));
		}
	}

	public BusinessObjectDto<AbstractTariff> getTariffById(Long tariffId) {
		return businessObjectDtoTr.translate(em.find(AbstractTariff.class, tariffId));
	}

	public BusinessObjectDto<Service> getServiceById(Long serviceId) {
		return businessObjectDtoTr.translate(em.find(Service.class, serviceId));
	}
}