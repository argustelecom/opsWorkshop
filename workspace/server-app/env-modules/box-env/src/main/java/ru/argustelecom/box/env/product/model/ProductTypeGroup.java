package ru.argustelecom.box.env.product.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "product_type_group")
public class ProductTypeGroup extends BusinessDirectory {

	private static final long serialVersionUID = -7426798854134644791L;

	@Column(name = "description", length = 256)
	private String description;

	@OrderBy("name")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
	private List<AbstractProductType> types = new ArrayList<>();

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ProductTypeGroup() {
		super();
	}

	/**
	 * Создает экземпляр спецификации. Т.к. спецификация является метаданными, то для ее идентификации необходимо
	 * использовать генератор {@link MetadataUnit#generateId()} или
	 * {@link ru.argustelecom.box.env.idsequence.IdSequenceService#nextValue(Class)}. Этот же идентификатор
	 * распространяется на холдера свойств спецификации. Только использование единого генератора для всех потомков
	 * спецификации может гарантированно уберечь от наложения идентификаторов в холдерах
	 * 
	 * @param id
	 *            - идентификатор, полученный из генератора идентификаторов метаданных
	 */
	public ProductTypeGroup(Long id) {
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

	public List<AbstractProductType> getTypes() {
		return Collections.unmodifiableList(types);
	}

	protected List<AbstractProductType> getMutableTypes() {
		return types;
	}

	public static class ProductTypeGroupQuery<E extends ProductTypeGroup> extends EntityQuery<E> {

		private EntityQueryStringFilter<E> objectName;
		private EntityQueryStringFilter<E> description;

		public ProductTypeGroupQuery(Class<E> entityClass) {
			super(entityClass);
			objectName = createStringFilter(ProductTypeGroup_.objectName);
			description = createStringFilter(ProductTypeGroup_.description);
		}

		public EntityQueryStringFilter<E> objectName() {
			return objectName;
		}

		public EntityQueryStringFilter<E> description() {
			return description;
		}
	}
}
