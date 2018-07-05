package ru.argustelecom.box.env.billing.provision.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

/**
 * Справочник условий предоставления
 * <p>
 *
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "provision_terms", uniqueConstraints = @UniqueConstraint(name = "uc_provision_temrs", columnNames = {
		"dtype", "name" }))
public abstract class AbstractProvisionTerms extends BusinessDirectory {

	private static final long serialVersionUID = 1371701325489326604L;

	@Column(length = 512)
	private String description;

	protected AbstractProvisionTerms() {
	}

	protected AbstractProvisionTerms(Long id) {
		super(id);
	}

	@Override
	@Column(name = "name", length = 64)
	@Access(AccessType.PROPERTY)
	public String getObjectName() {
		return super.getObjectName();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public abstract boolean isRecurrent();

	public static class AbstractProvisionTermsQuery<E extends AbstractProvisionTerms> extends EntityQuery<E> {

		private EntityQueryStringFilter<E> name = createStringFilter(AbstractProvisionTerms_.objectName);

		public AbstractProvisionTermsQuery(Class<E> entityClass) {
			super(entityClass);
		}

		public EntityQueryStringFilter<E> name() {
			return name;
		}
	}

}
