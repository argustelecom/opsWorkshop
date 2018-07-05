package ru.argustelecom.box.env.companyinfo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.IdentifiableDto;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.system.inf.modelbase.Identifiable;

/**
 * <b>Dto</b> для справочника {@linkplain ru.argustelecom.box.env.party.model.role.Owner юридических лиц компании}. В
 * частности для дерева и ФБ атрибутов.
 */
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class CompanyInfoOwnerDto implements IdentifiableDto {

	private Long id;

	@Setter
	private String name;

	private String partyTypeName;

	@Setter
	private boolean principal;

	@Setter
	private String qrCodePattern;

	private BigDecimal taxRate;

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Owner.class;
	}

}