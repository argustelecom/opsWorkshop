package ru.argustelecom.box.env.product.model;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import ru.argustelecom.box.env.commodity.model.CommodityRdo;
import ru.argustelecom.box.env.commodity.model.CommoditySpec;
import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@Entity
@Access(AccessType.FIELD)
public class ProductType extends AbstractProductType {

	private static final long serialVersionUID = -6098512313347513725L;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	//@formatter:off
	@JoinTable(
			schema = "system",
			name = "product_type_entry",
			joinColumns = @JoinColumn(name = "product_type_id"),
			inverseJoinColumns = @JoinColumn(name = "commodity_spec_id")
	)
	//@formatter:on
	private Set<CommoditySpec> entries = new HashSet<>();

	@ManyToMany(mappedBy = "compositeParts")
	private Set<ProductTypeComposite> composites = new HashSet<>();

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ProductType() {
		super();
	}

	/**
	 * Создает экземпляр спецификации. Т.к. спецификация является метаданными, то для ее идентификации необходимо
	 * использовать генератор {@link MetadataUnit#generateId()} или {@link MetadataUnit#generateId(EntityManager)}. Этот
	 * же идентификатор распространяется на холдера свойств спецификации. Только использование единого генератора для
	 * всех потомков спецификации может гарантированно уберечь от наложения идентификаторов в холдерах
	 * 
	 * @param id
	 *            - идентификатор, полученный из генератора идентификаторов метаданных
	 */
	protected ProductType(Long id) {
		super(id);
	}

	public Set<ProductTypeComposite> getComposites() {
		return Collections.unmodifiableSet(composites);
	}

	protected Set<ProductTypeComposite> getMutableComposites() {
		return composites;
	}

	public boolean isCompositePart() {
		return !composites.isEmpty();
	}

	public boolean isCompositePart(ProductTypeComposite composite) {
		return composites.contains(composite);
	}

	public Set<CommoditySpec> getEntries() {
		return Collections.unmodifiableSet(entries);
	}

	protected Set<CommoditySpec> getMutableEntries() {
		return entries;
	}

	public boolean hasEntries() {
		return !entries.isEmpty();
	}

	public boolean hasEntry(CommoditySpec entry) {
		return entries.contains(entry);
	}

	public CommoditySpec getEntry(CommodityType commodityType) {
		return commodityType == null ? null
				: entries.stream().filter(e -> Objects.equals(e.getType(), commodityType)).findFirst().orElse(null);
	}

	public CommoditySpec addEntry(CommoditySpec entry) {
		if (!entries.contains(entry)) {
			entries.add(entry);
		}
		return entry;
	}

	public boolean removeEntry(CommoditySpec entry) {
		return entries.remove(entry);
	}

	public boolean hasCommodityType(CommodityType commodityType) {
		return getEntry(commodityType) != null;
	}

	public boolean removeCommodityType(CommodityType commodityType) {
		Iterator<CommoditySpec> it = entries.iterator();
		while (it.hasNext()) {
			CommoditySpec entry = it.next();
			if (Objects.equals(entry.getType(), commodityType)) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ServiceSpec> collectServiceSpecs() {
		return entries.stream().map(EntityManagerUtils::initializeAndUnproxy)
				.filter(spec -> spec instanceof ServiceSpec).map(spec -> (ServiceSpec) spec).collect(toList());
	}

	@Override
	public ProductRdo createReportData() {
		final ReportDataList<CommodityRdo> commodityRdoList = new ReportDataList<>();

		getEntries().forEach(entry -> {
			CommodityRdo commodityRdo = entry.getType().createReportData();
			commodityRdo.setProperties(entry.getPropertyValueMap());
			commodityRdoList.add(commodityRdo);
		});

		//@formatter:off
		return ProductRdo.builder()
					.id(getId())
					.name(getObjectName())
					.commodities(commodityRdoList)
				.build();
		//@formatter:on
	}

	public static class ProductTypeQuery<T extends ProductType> extends AbstractProductTypeQuery<T> {

		public ProductTypeQuery(Class<T> entityClass) {
			super(entityClass);
		}

	}

}