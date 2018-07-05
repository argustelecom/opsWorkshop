package ru.argustelecom.box.env.commodity.model;

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

import lombok.Getter;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * Сущность описывающая типы товаров и услуг.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "commodity_type", uniqueConstraints = {
		@UniqueConstraint(name = "uc_commodity_type_keyword", columnNames = { "keyword" }) })
public abstract class CommodityType extends Type implements Printable {

	@Getter
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "group_id", nullable = false)
	private CommodityTypeGroup group;

	private static final long serialVersionUID = -6098512313347513725L;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected CommodityType() {
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
	protected CommodityType(Long id) {
		super(id);
	}

	/**
	 * Свойста commodity должны заполняться обладателем его инстанса
	 */
	@Override
	public CommodityRdo createReportData() {
		//@formatter:off
		return CommodityRdo.builder()
					.id(getId())
					.name(getObjectName())
					.categoryName(getGroup().getObjectName())
				.build();
		//@formatter:on
	}

	public void changeGroup(CommodityTypeGroup group) {
		if (!Objects.equals(this.group, group)) {
			if (this.group != null) {
				this.group.getMutableChildren().remove(this);
			}
			this.group = group;
			if (this.group != null) {
				this.group.getMutableChildren().add(this);
			}
		}
	}

	public static class CommodityTypeQuery<T extends CommodityType> extends TypeQuery<T> {

		private EntityQueryEntityFilter<T, CommodityTypeGroup> group = createEntityFilter(CommodityType_.group);

		public CommodityTypeQuery(Class<T> entityClass) {
			super(entityClass);
		}

		public EntityQueryEntityFilter<T, CommodityTypeGroup> group() {
			return group;
		}

	}

}