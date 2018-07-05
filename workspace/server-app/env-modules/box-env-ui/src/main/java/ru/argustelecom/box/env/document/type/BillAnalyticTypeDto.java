package ru.argustelecom.box.env.document.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType.AnalyticCategory;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class BillAnalyticTypeDto extends AbstractBillAnalyticTypeDto {

	private AnalyticCategory analyticCategory;

	public BillAnalyticTypeDto(Long id, String name, String description, AnalyticCategory analyticCategory,
			boolean availableForCustomPeriod) {
		super(id, name, description, availableForCustomPeriod);
		this.analyticCategory = analyticCategory;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return BillAnalyticTypeDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return BillAnalyticType.class;
	}
}
