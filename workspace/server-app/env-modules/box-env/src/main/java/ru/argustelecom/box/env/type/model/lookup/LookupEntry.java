package ru.argustelecom.box.env.type.model.lookup;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryLogicalFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "lookup_entry")
public class LookupEntry extends BusinessDirectory {

	private static final long serialVersionUID = -2749508262516279570L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private LookupCategory category;

	@Column(nullable = false)
	private boolean active = true;

	@Column(nullable = false)
	private boolean sys;

	@Column(length = 256)
	private String description;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected LookupEntry() {
		super();
	}

	public LookupEntry(Long id) {
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

	public LookupCategory getCategory() {
		return category;
	}

	public void setCategory(LookupCategory category) {
		this.category = category;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSys() {
		return sys;
	}

	public void setSys(boolean sys) {
		this.sys = sys;
	}

	@Override
	public Boolean getIsSys() {
		return sys;
	}

	public static class LookupEntryQuery<E extends LookupEntry> extends EntityQuery<E> {

		private EntityQueryStringFilter<E> objectName;
		private EntityQueryStringFilter<E> description;
		private EntityQueryEntityFilter<E, LookupCategory> category;
		private EntityQueryLogicalFilter<E> active;
		private EntityQueryLogicalFilter<E> sys;

		public LookupEntryQuery(Class<E> entityClass) {
			super(entityClass);
			objectName = createStringFilter(LookupEntry_.objectName);
			description = createStringFilter(LookupEntry_.description);
			category = createEntityFilter(LookupEntry_.category);
			active = createLogicalFilter(LookupEntry_.active);
			sys = createLogicalFilter(LookupEntry_.sys);
		}

		public EntityQueryStringFilter<E> objectName() {
			return objectName;
		}

		public EntityQueryStringFilter<E> description() {
			return description;
		}

		public EntityQueryEntityFilter<E, LookupCategory> category() {
			return category;
		}

		public EntityQueryLogicalFilter<E> active() {
			return active;
		}

		public EntityQueryLogicalFilter<E> sys() {
			return sys;
		}
	}
}
