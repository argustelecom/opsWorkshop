package ru.argustelecom.box.env.billing.invoice;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.MeasuredProductOffering;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = "id", callSuper = false)
public class MeasuredProductOfferingDto extends ConvertibleDto {
	private Long id;
	private Money price;
	private String objectName;
	private BusinessObjectDto<AbstractPricelist> pricelist;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return MeasuredProductOfferingDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return MeasuredProductOffering.class;
	}
}