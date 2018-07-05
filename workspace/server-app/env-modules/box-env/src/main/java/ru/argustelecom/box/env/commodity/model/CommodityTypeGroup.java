package ru.argustelecom.box.env.commodity.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.env.util.HasParent;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

/**
 * Сущность описывающая группу для {@linkplain CommodityType типа товара/услуги}. Группы могуть быть иерархическими.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "commodity_type_group", uniqueConstraints = {
		@UniqueConstraint(name = "uc_commodity_type_group", columnNames = { "name", "parent_id" }),
		@UniqueConstraint(name = "uc_commodity_type_group_keyword", columnNames = { "keyword" }) })
public class CommodityTypeGroup extends BusinessObject implements HasParent<CommodityTypeGroup>, Printable {

	/**
	 * Наименование группы
	 */
	@Getter
	@Setter
	@Column(length = 256)
	private String name;

	/**
	 * Ключевое слово, по которой группа может использоваться в отчётах.
	 */
	@Getter
	@Setter
	@Column(length = 64)
	private String keyword;

	/**
	 * Ссылка на родительскую группу.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private CommodityTypeGroup parent;

	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	private Set<CommodityType> children = new HashSet<>();

	protected CommodityTypeGroup() {
		super();
	}

	public CommodityTypeGroup(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return name;
	}

	@Override
	public CommodityTypeGroup getParent() {
		return parent;
	}

	@Override
	public CommodityGroupRdo createReportData() {
		ReportDataList<CommodityRdo> commodities = new ReportDataList<>();
		getChildren().forEach(child -> commodities.add(child.createReportData()));

		//@formatter:off
		return CommodityGroupRdo.builder()
					.id(getId())
					.name(getObjectName())
					.commodities(commodities)
				.build();
		//@formatter:on
	}

	public void changeParent(CommodityTypeGroup newParent) {
		if (!Objects.equals(parent, newParent)) {
			checkCircularDependency(newParent);
			this.parent = newParent;
		}
	}

	/**
	 * Возвращает не изменяемый набор вложенных в данную группу {@linkplain CommodityType типов товаров/услуг}
	 */
	public Set<CommodityType> getChildren() {
		return Collections.unmodifiableSet(children);
	}

	Set<CommodityType> getMutableChildren() {
		return children;
	}

	public static class CommodityTypeGroupQuery extends EntityQuery<CommodityTypeGroup> {

		private EntityQueryStringFilter<CommodityTypeGroup> name = createStringFilter(CommodityTypeGroup_.name);
		private EntityQueryStringFilter<CommodityTypeGroup> keyword = createStringFilter(CommodityTypeGroup_.keyword);
		private EntityQueryEntityFilter<CommodityTypeGroup, CommodityTypeGroup> parent = createEntityFilter(
				CommodityTypeGroup_.parent);

		public CommodityTypeGroupQuery() {
			super(CommodityTypeGroup.class);
		}

		public EntityQueryStringFilter<CommodityTypeGroup> name() {
			return name;
		}

		public EntityQueryStringFilter<CommodityTypeGroup> keyword() {
			return keyword;
		}

		public EntityQueryEntityFilter<CommodityTypeGroup, CommodityTypeGroup> parent() {
			return parent;
		}

	}

	private static final long serialVersionUID = -6087446549879592020L;

}