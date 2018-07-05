package ru.argustelecom.box.env.type.model.lookup;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.ensure;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;

import ru.argustelecom.box.env.type.model.lookup.LookupEntry.LookupEntryQuery;
import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "lookup_category")
public class LookupCategory extends BusinessDirectory {

	private static final long serialVersionUID = 2389724931408952042L;

	@Column(length = 256)
	private String description;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected LookupCategory() {
		super();
	}

	public LookupCategory(Long id) {
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

	public List<LookupEntry> getPossibleValues(EntityManager em) {
		LookupEntryQuery<LookupEntry> query = new LookupEntryQuery<>(LookupEntry.class);
		query.and(query.category().equal(this), query.active().equal(true));
		return query.createTypedQuery(ensure(em)).getResultList();
	}

	public static class LookupCategoryQuery<E extends LookupCategory> extends EntityQuery<E> {

		private EntityQueryStringFilter<E> objectName;
		private EntityQueryStringFilter<E> description;

		public LookupCategoryQuery(Class<E> entityClass) {
			super(entityClass);
			objectName = createStringFilter(LookupCategory_.objectName);
			description = createStringFilter(LookupCategory_.description);
		}

		public EntityQueryStringFilter<E> objectName() {
			return objectName;
		}

		public EntityQueryStringFilter<E> description() {
			return description;
		}
	}
}
