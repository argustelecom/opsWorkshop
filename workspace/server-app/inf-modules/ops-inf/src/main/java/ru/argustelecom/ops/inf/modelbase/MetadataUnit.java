package ru.argustelecom.ops.inf.modelbase;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.ensure;

import java.io.Serializable;
import java.util.Objects;

import javax.lang.model.SourceVersion;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.google.common.base.Strings;

import ru.argustelecom.system.inf.modelbase.NamedObject;

@MappedSuperclass
@Access(AccessType.FIELD)
@NamedNativeQuery(name = MetadataUnit.GEN_METADATA_UNIT_ID_QUERY, query = "select nextval('system.gen_metadata_unit_id')")
public abstract class MetadataUnit<ID extends Serializable> implements Serializable, NamedObject {

	public static final String GEN_METADATA_UNIT_ID_QUERY = "MetadataUnit.genMetadataUnitId";

	@Transient
	private ID id;

	@Column(length = 128, nullable = false)
	private String name;

	@Column(length = 256)
	private String description;

	@Column(length = 64)
	private String keyword;

	@Column(length = 32, nullable = false)
	@Enumerated(EnumType.STRING)
	private MetadataUnitStatus status = MetadataUnitStatus.ACTIVE;

	@Version
	private Long version;

	protected MetadataUnit() {
	}

	public MetadataUnit(ID id) {
		setId(id);
	}

	public ID getId() {
		return id;
	}

	private void setId(ID id) {
		this.id = checkId(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public MetadataUnitStatus getStatus() {
		return status;
	}

	public void setStatus(MetadataUnitStatus status) {
		this.status = checkNotNull(status);
	}

	@Override
	public String getObjectName() {
		return name;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		if (!(obj instanceof MetadataUnit)) {
			return false;
		}

		MetadataUnit<?> other = (MetadataUnit<?>) obj;
		return Objects.equals(this.getId(), other.getId());
	}

	protected abstract ID checkId(ID id);

	public static String checkKeyword(String keyword) {
		if (Strings.isNullOrEmpty(keyword) || !SourceVersion.isIdentifier(keyword))
			throw new IllegalArgumentException("Metadata keyword should be valid identifier");
		return keyword;
	}

	public static <T extends MetadataUnit<?>> String generateKeyword(Class<T> metadataClass, EntityManager em) {
		return generateKeyword(metadataClass, generateId(em));
	}

	public static <T extends MetadataUnit<?>> String generateKeyword(Class<T> metadataClass, Object id) {
		return UPPER_CAMEL.to(LOWER_CAMEL, metadataClass.getSimpleName()) + id.toString();
	}

	public static Long generateId() {
		return generateId(null);
	}

	public static Long generateId(EntityManager em) {
		Object result = ensure(em).createNamedQuery(GEN_METADATA_UNIT_ID_QUERY).getSingleResult();
		return Long.valueOf(result.toString());
	}

	public enum MetadataUnitStatus {
		ACTIVE, DEPRECATED
	}

	private static final long serialVersionUID = 1650896079026743873L;

}
