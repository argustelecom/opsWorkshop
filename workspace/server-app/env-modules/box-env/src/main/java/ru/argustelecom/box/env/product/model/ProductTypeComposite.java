package ru.argustelecom.box.env.product.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import ru.argustelecom.box.env.commodity.model.CommoditySpec;
import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@Entity
@Access(AccessType.FIELD)
public class ProductTypeComposite extends AbstractProductType {

	private static final long serialVersionUID = -6087446549879592020L;

	//@formatter:off
	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable(
			name = "product_type_composite_parts",
			joinColumns = @JoinColumn(name = "product_type_composite_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "product_type_part_id", referencedColumnName = "id")
	)//@formatter:on
	private Set<ProductType> compositeParts = new HashSet<>();

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ProductTypeComposite() {
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
	protected ProductTypeComposite(Long id) {
		super(id);
	}

	public Set<ProductType> getCompositeParts() {
		return Collections.unmodifiableSet(compositeParts);
	}

	protected Set<ProductType> getMutableChildren() {
		return compositeParts;
	}

	public boolean hasCompositeParts() {
		return !compositeParts.isEmpty();
	}

	public boolean hasCompositePart(ProductType productType) {
		return compositeParts.contains(productType);
	}

	public ProductType addCompositePart(ProductType productType) {
		if (productType != null && !hasCompositePart(productType)) {
			compositeParts.add(productType);
			productType.getMutableComposites().add(this);
		}
		return productType;
	}

	public boolean removeProductType(ProductType productType) {
		if (productType != null && hasCompositePart(productType)) {
			compositeParts.remove(productType);
			productType.getMutableComposites().remove(this);
			return true;
		}
		return false;
	}

	public List<CommoditySpec> getAllEntries() {
		List<CommoditySpec> allEntries = new ArrayList<>();
		compositeParts.forEach(part -> allEntries.addAll(part.getEntries()));
		return allEntries;
	}

	@Override
	public List<ServiceSpec> collectServiceSpecs() {
		return getAllEntries().stream().map(EntityManagerUtils::initializeAndUnproxy)
				.filter(spec -> spec instanceof ServiceSpec).map(spec -> (ServiceSpec) spec)
				.collect(Collectors.toList());
	}

	@Override
	public ProductRdo createReportData() {
		return ProductRdo.builder().id(getId()).name(getObjectName()).build();
	}

	public static class ProductTypeCompositeQuery<T extends ProductTypeComposite> extends AbstractProductTypeQuery<T> {

		public ProductTypeCompositeQuery(Class<T> entityClass) {
			super(entityClass);
		}

	}
}
