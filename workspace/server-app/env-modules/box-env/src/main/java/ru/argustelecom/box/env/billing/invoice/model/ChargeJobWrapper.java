package ru.argustelecom.box.env.billing.invoice.model;

import java.io.Serializable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.billing.invoice.ChargeJobRepository;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapper;
import ru.argustelecom.box.publang.billing.model.IChargeJob;
import ru.argustelecom.box.publang.billing.model.IFilter;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Named(value = IChargeJob.WRAPPER_NAME)
public class ChargeJobWrapper implements EntityWrapper, Serializable {

	@Inject
	private ChargeJobRepository chargeJobRp;

	public IChargeJob wrap(Identifiable entity) {
		checkNotNull(entity);
		ChargeJob chargeJob = (ChargeJob) entity;
		FilterAggData filter = chargeJob.getFilter();

		//@formatter:off
		IFilter iFilter = IFilter.builder()
				.dateFrom(filter.getDateFrom())
				.dateTo(filter.getDateTo())
				.processingStage(filter.getProcessingStage())
				.serviceId(filter.getServiceId())
				.tariffId(filter.getTariffId())
				.build();

		return IChargeJob.builder()
				.mediationId(chargeJob.getMediationId())
				.dataType(chargeJob.getDataType().name())
				.filter(iFilter)
				.build();
		//@formatter:on
	}

	public ChargeJob unwrap(IEntity iEntity) {

		IChargeJob iChargeJob = (IChargeJob) iEntity;

		JobDataType dataType = JobDataType.getByString(iChargeJob.getDataType());

		//@formatter:off
		IFilter iFilter = ofNullable(iChargeJob.getFilter()).orElse(IFilter.builder().build());

		FilterAggData filter = FilterAggData.builder()
					.dateFrom(iFilter.getDateFrom())
					.dateTo(iFilter.getDateTo())
					.processingStage(iFilter.getProcessingStage())
					.serviceId(iFilter.getServiceId())
					.tariffId(iFilter.getTariffId())
					.build();
		//@formatter:on

		ChargeJob chargeJob = chargeJobRp.find(iChargeJob.getMediationId());
		if (chargeJob == null) {
			chargeJob = chargeJobRp.create(iChargeJob.getMediationId(), dataType, filter);
		}
		return chargeJob;
	}

	private static final long serialVersionUID = -8399660898504526476L;

}