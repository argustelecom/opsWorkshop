package ru.argustelecom.box.env.telephony.tariff;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntryHistory;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
public class TariffEntryHistoryDto extends ConvertibleDto {
	private Long id;
	private Long version;
	private Date modifiedDate;
	private BusinessObjectDto<Employee> employee;
	private String name;
	private Money chargePerUnit;
	private BusinessObjectDto<TelephonyZone> zone;
	private List<Integer> prefixes;

	@Builder
	public TariffEntryHistoryDto(Long id, Long version, Date modifiedDate, BusinessObjectDto<Employee> employee,
			String name, Money chargePerUnit, BusinessObjectDto<TelephonyZone> zone, List<Integer> prefixes) {
		this.id = id;
		this.version = version;
		this.modifiedDate = modifiedDate;
		this.employee = employee;
		this.name = name;
		this.chargePerUnit = chargePerUnit;
		this.zone = zone;
		this.prefixes = prefixes;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return TariffEntryHistoryDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return TariffEntryHistory.class;
	}
}
