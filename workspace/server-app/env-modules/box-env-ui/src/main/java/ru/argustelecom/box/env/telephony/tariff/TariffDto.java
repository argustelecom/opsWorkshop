package ru.argustelecom.box.env.telephony.tariff;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.CommonTariff;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
public class TariffDto extends ConvertibleDto {
	private Long id;
	private String name;
	private String type;
	private String state;
	private Date validFrom;
	private Date validTo;
	private PeriodUnit ratedUnit;
	private RoundingPolicy roundingPolicy;
	private List<TariffEntryDto> entries;
	private BusinessObjectDto<Customer> customer;
	private BusinessObjectDto<CommonTariff> parent;
	private List<TariffEntryDto> parentEntries;

	@Builder
	public TariffDto(Long id, String name, String type, String state, Date validFrom, Date validTo,
			PeriodUnit ratedUnit, RoundingPolicy roundingPolicy, List<TariffEntryDto> entries,
			BusinessObjectDto<Customer> customer, BusinessObjectDto<CommonTariff> parent,
			List<TariffEntryDto> parentEntries) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.state = state;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.ratedUnit = ratedUnit;
		this.roundingPolicy = roundingPolicy;
		this.entries = entries;
		this.customer = customer;
		this.parent = parent;
		this.parentEntries = parentEntries;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return TariffDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return AbstractTariff.class;
	}
}
