package ru.argustelecom.box.env.product.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "product_type", uniqueConstraints = {
		@UniqueConstraint(name = "uc_product_type_keyword", columnNames = { "keyword" }) })
public abstract class AbstractProductType extends Type implements Printable {

	private static final long serialVersionUID = -6098512313347513725L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private ProductTypeGroup group;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected AbstractProductType() {
		super();
	}

	/**
	 * Создает экземпляр спецификации. Т.к. спецификация является метаданными, то для ее идентификации необходимо
	 * использовать генератор {@link MetadataUnit#generateId()} или {@link MetadataUnit#generateId(EntityManager)} )}.
	 * Этот же идентификатор распространяется на холдера свойств спецификации. Только использование единого генератора
	 * для всех потомков спецификации может гарантированно уберечь от наложения идентификаторов в холдерах
	 * 
	 * @param id
	 *            - идентификатор, полученный из генератора идентификаторов метаданных
	 */
	protected AbstractProductType(Long id) {
		super(id);
	}

	public ProductTypeGroup getGroup() {
		return group;
	}

	public void setGroup(ProductTypeGroup group) {
		if (!Objects.equals(this.group, group)) {
			if (this.group != null) {
				this.group.getMutableTypes().remove(this);
			}
			this.group = group;
			if (this.group != null) {
				this.group.getMutableTypes().add(this);
			}
		}
	}

	/**
	 * Формирует список из типов услуг, входящих в продукт.
	 */
	public abstract List<ServiceSpec> collectServiceSpecs();

	@Override
	public abstract ProductRdo createReportData();

	public static class AbstractProductTypeQuery<T extends AbstractProductType> extends TypeQuery<T> {

		private EntityQueryEntityFilter<T, ProductTypeGroup> group;
		private EntityQueryStringFilter<T> name;

		public AbstractProductTypeQuery(Class<T> entityClass) {
			super(entityClass);
			group = createEntityFilter(AbstractProductType_.group);
			name = createStringFilter(AbstractProductType_.name);
		}

		public EntityQueryEntityFilter<T, ProductTypeGroup> group() {
			return group;
		}

		public EntityQueryStringFilter<T> name() {
			return name;
		}

		public void setName(EntityQueryStringFilter<T> name) {
			this.name = name;
		}
	}
}
