package ru.argustelecom.box.env.billing.provision;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.stl.period.PeriodType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class ProvisionTermsDto extends ConvertibleDto {

	private Long id;
	private String name;
	private ProvisionTermsType type;
	private PeriodType periodType;
	private String description;
	private boolean reserveFunds;

	@Override
	public Class<AbstractProvisionTerms> getEntityClass() {
		return AbstractProvisionTerms.class;
	}

	@Override
	public Class<ProvisionTermsDtoTranslator> getTranslatorClass() {
		return ProvisionTermsDtoTranslator.class;
	}

	public enum ProvisionTermsType {
		RECURRENT, NON_RECURRENT
	}

}