package ru.argustelecom.box.env.party.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.type.model.SupportFiltering;
import ru.argustelecom.box.env.type.model.SupportUniqueProperty;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "customer_type", uniqueConstraints = {
		@UniqueConstraint(name = "uc_party_type_keyword", columnNames = { "keyword" }) })
@SupportFiltering
@SupportUniqueProperty
public class CustomerType extends Type {

	private static final long serialVersionUID = -1772345567959011469L;

	@Getter
	@Setter
	@Column(length = 32, nullable = false)
	@Enumerated(EnumType.STRING)
	private CustomerCategory category;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	private PartyType partyType;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected CustomerType() {
		super();
	}

	/**
	 * Создает экземпляр спецификации. Т.к. спецификация является метаданными, то для ее идентификации необходимо
	 * использовать генератор {@link ru.argustelecom.box.inf.modelbase.MetadataUnit#generateId()} или
	 * {@link ru.argustelecom.box.inf.modelbase.MetadataUnit#generateId(javax.persistence.EntityManager)}. Этот же
	 * идентификатор распространяется на холдера свойств спецификации. Только использование единого генератора для всех
	 * потомков спецификации может гарантированно уберечь от наложения идентификаторов в холдерах
	 *
	 * @param id
	 *            - идентификатор, полученный из генератора идентификаторов метаданных
	 */
	protected CustomerType(Long id) {
		super(id);
	}

	public static class CustomerTypeQuery<T extends CustomerType> extends TypeQuery<T> {

		private EntityQuerySimpleFilter<T, CustomerCategory> category;
		private EntityQueryEntityFilter<T, PartyType> partyType;

		public CustomerTypeQuery(Class<T> entityClass) {
			super(entityClass);
			category = createFilter(CustomerType_.category);
			partyType = createEntityFilter(CustomerType_.partyType);
		}

		public EntityQuerySimpleFilter<T, CustomerCategory> category() {
			return category;
		}

		public EntityQueryEntityFilter<T, PartyType> partyType() {
			return partyType;
		}

	}
}