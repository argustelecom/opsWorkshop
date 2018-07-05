package ru.argustelecom.box.env.party.model.role;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Определяет дополнительные параметры для {@link ru.argustelecom.box.env.party.model.role.Owner}
 */
@Getter
@Setter
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", uniqueConstraints = @UniqueConstraint(name = "uc_owner_parameter_ordinal", columnNames = {
		"owner_id", "ordinal" }))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnerParameter extends BusinessObject {

	/**
	 * Ключевое слово
	 */
	@Column(nullable = false)
	private String keyword;

	/**
	 * Значение параметра
	 */
	private String value;

	/**
	 * Порядковый номер
	 */
	@Column(nullable = false)
	private Integer ordinal;

	public OwnerParameter(Long id, Owner owner) {
		super(id);
		checkNotNull(owner).addAdditionalParameter(this);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "name")
	public String getObjectName() {
		return super.getObjectName();
	}

	private static final long serialVersionUID = 4080938354436972755L;
}
