package ru.argustelecom.box.env.billing.invoice.unsuitable;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.invoice.model.UnsuitableRatedOutgoingCallsView;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class UnsuitableRatedOutgoingCallsViewDto extends ConvertibleDto {

	private Long id;
	private Date callDate;
	private BigDecimal duration;
	private PeriodUnit ratedUnit;
	private Money amount;
	private BusinessObjectDto<AbstractTariff> tariff;
	private String resourceNumber;
	private BusinessObjectDto<Service> service;
	private BusinessObjectDto<PartyRole> supplier;
	private BusinessObjectDto<TelephonyZone> zone;

	@Builder
	public UnsuitableRatedOutgoingCallsViewDto(Long id, Date callDate, BigDecimal duration, PeriodUnit ratedUnit,
			Money amount, BusinessObjectDto<AbstractTariff> tariff, String resourceNumber,
			BusinessObjectDto<Service> service, BusinessObjectDto<PartyRole> supplier,
			BusinessObjectDto<TelephonyZone> zone) {
		this.id = id;
		this.callDate = callDate;
		this.duration = duration;
		this.ratedUnit = ratedUnit;
		this.amount = amount;
		this.tariff = tariff;
		this.resourceNumber = resourceNumber;
		this.service = service;
		this.supplier = supplier;
		this.zone = zone;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return UnsuitableRatedOutgoingCallsViewDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return UnsuitableRatedOutgoingCallsView.class;
	}
}
