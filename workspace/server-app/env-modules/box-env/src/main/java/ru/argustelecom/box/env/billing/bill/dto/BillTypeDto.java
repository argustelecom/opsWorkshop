package ru.argustelecom.box.env.billing.bill.dto;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@EqualsAndHashCode(callSuper = true)
public class BillTypeDto extends DocumentTypeDto {

	@Builder
	public BillTypeDto(Long id, String name, List<ReportModelTemplateDto> reportTemplates) {
		super(id, name, reportTemplates);
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return BillTypeDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return BillType.class;
	}

}