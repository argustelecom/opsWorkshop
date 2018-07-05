package ru.argustelecom.box.env.contractor;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Supplier;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
public class SupplierDto extends ConvertibleDto {
	private Long id;
	private String name;
	private String brandName;
	private String legalName;
	private String partyTypeName;

	@Builder
	public SupplierDto(Long id, String name, String brandName, String legalName, String partyTypeName) {
		this.id = id;
		this.name = name;
		this.brandName = brandName;
		this.legalName = legalName;
		this.partyTypeName = partyTypeName;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return SupplierDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Supplier.class;
	}
}