package ru.argustelecom.box.env.pricing;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@NoArgsConstructor
public class PricelistListDto extends ConvertibleDto {
	private Long id;
	private String name;
	private String state;
	private Date validFrom;
	private Date validTo;
	private List<String> segmentNames;
	private CustomerPricelistListDto customerDto;
	private BusinessObjectDto<Owner> owner;

	@Builder
	public PricelistListDto(Long id, String name, String state, Date validFrom, Date validTo, List<String> segmentNames,
			CustomerPricelistListDto customerDto, BusinessObjectDto<Owner> owner) {
		this.id = id;
		this.name = name;
		this.state = state;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.segmentNames = segmentNames;
		this.customerDto = customerDto;
		this.owner = owner;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return PricelistListDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return AbstractPricelist.class;
	}
}