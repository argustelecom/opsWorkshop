package ru.argustelecom.box.env.telephony.tariff;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;

import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@NoArgsConstructor
public class TariffListDto extends ConvertibleDto {
	private Long id;
	private String name;
	private String state;
	private Date validFrom;
	private Date validTo;
	private BusinessObjectDto<Customer> customer;
	private String typeName;

	@Builder
	public TariffListDto(Long id, String name, String state, Date validFrom, Date validTo,
						 BusinessObjectDto<Customer> customer, String typeName) {
		this.id = id;
		this.name = name;
		this.state = state;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.customer = customer;
		this.typeName = typeName;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return TariffListDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return AbstractTariff.class;
	}
}