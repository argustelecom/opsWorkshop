package ru.argustelecom.box.env.pricing;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.PricelistState;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@EqualsAndHashCode(of = { "id", "state" }, callSuper = false)
public class PricelistAttributesDto extends ConvertibleDto {

	private Long id;
	@Setter
	private String name;
	@Setter
	private Date validFrom;
	@Setter
	private Date validTo;
	private PricelistState state;
	@Setter
	private Owner owner;
	private Class<? extends AbstractPricelist> clazz;

	@Builder
	public PricelistAttributesDto(Long id, String name, Date validFrom, Date validTo, PricelistState state, Owner owner,
								  Class<? extends AbstractPricelist> clazz) {
		this.id = id;
		this.name = name;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.state = state;
		this.owner = owner;
		this.clazz = clazz;
	}
	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return PricelistAttributesDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return AbstractPricelist.class;
	}

}
