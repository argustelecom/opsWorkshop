package ru.argustelecom.box.env.companyinfo;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.OwnerParameter;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class OwnerAdditionalParameterDto extends ConvertibleDto {
	private Long id;
	private String name;
	private String keyword;
	private String value;

	@Builder
	public OwnerAdditionalParameterDto(Long id, String name, String keyword, String value) {
		this.id = id;
		this.name = name;
		this.keyword = keyword;
		this.value = value;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return OwnerAdditionalParameterDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return OwnerParameter.class;
	}
}
