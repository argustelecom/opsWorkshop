package ru.argustelecom.box.env.commodity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.IdentifiableDto;
import ru.argustelecom.system.inf.modelbase.Identifiable;

/**
 * Dto для дерева типа товаров и услуг.
 */
@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = { "id", "type" })
public class CommodityTypeTreeNodeDto implements IdentifiableDto {

	private Long id;

	@Setter
	private String name;

	private CommodityTypeRef type;

	private CommodityTypeTreeNodeDto parent;

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return type.getClazz();
	}

	public boolean isGroup() {
		return type.equals(CommodityTypeRef.GROUP);
	}

	public Long getParentId() {
		if (parent != null) {
			return parent.getId();
		}
		return null;
	}

	public Long getGroupId() {
		return isGroup() ? id : getParentId();
	}

	@Override
	public String toString() {
		return String.format("%s-%d", type.getClazz().getSimpleName(), id);
	}

}