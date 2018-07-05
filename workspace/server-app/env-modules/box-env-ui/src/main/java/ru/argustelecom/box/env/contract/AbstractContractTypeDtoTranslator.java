package ru.argustelecom.box.env.contract;

import ru.argustelecom.box.env.contract.model.AbstractContractType;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.document.type.ReportModelTemplateDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoConverterUtils;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.utils.CDIHelper;

public abstract class AbstractContractTypeDtoTranslator<T extends AbstractContractTypeDto, I extends AbstractContractType>
		implements DefaultDtoTranslator<T, I> {

	@Override
	public T translate(I contractType) {
		T dtoToFill = getDtoToFill();
		dtoToFill.setId(contractType.getId());
		dtoToFill.setName(contractType.getObjectName());
		dtoToFill.setCustomerTypeDto(
				CDIHelper.lookupCDIBean(CustomerTypeDtoTranslator.class).translate(contractType.getCustomerType()));
		dtoToFill.setDescription(contractType.getDescription());
		dtoToFill.setReportModelTemplates(DefaultDtoConverterUtils
				.translate(CDIHelper.lookupCDIBean(ReportModelTemplateDtoTranslator.class), contractType.getTemplates()));
		return fillDto(dtoToFill, contractType);
	}

	protected abstract T getDtoToFill();
	protected abstract T fillDto(T dtoToFill, I contractType);

}
