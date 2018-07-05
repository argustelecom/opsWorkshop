package ru.argustelecom.box.env.document.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.SummaryBillAnalyticType;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SummaryBillAnalyticTypeDto extends AbstractBillAnalyticTypeDto {

	public SummaryBillAnalyticTypeDto(Long id, String name, String description, boolean availableForCustomPeriod) {
		super(id, name, description, availableForCustomPeriod);
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return SummaryBillAnalyticTypeDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return SummaryBillAnalyticType.class;
	}
}
