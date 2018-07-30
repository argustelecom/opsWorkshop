package ru.argustelecom.box.env.party.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.box.env.party.PartyCategory;
import ru.argustelecom.box.env.type.model.SupportFiltering;
import ru.argustelecom.box.env.type.model.SupportUniqueProperty;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "party_type", uniqueConstraints = {
		@UniqueConstraint(name = "uc_party_type_keyword", columnNames = { "keyword" }) })
@SupportFiltering
@SupportUniqueProperty
public class PartyType extends Type {

	private static final long serialVersionUID = 7133283790078882747L;

	@Column(length = 32, nullable = false)
	@Enumerated(EnumType.STRING)
	private PartyCategory category;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected PartyType() {
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
	protected PartyType(Long id) {
		super(id);
	}

	public PartyCategory getCategory() {
		return category;
	}

	public void setCategory(PartyCategory category) {
		this.category = category;
	}

	public static class PartyTypeQuery<T extends PartyType> extends TypeQuery<T> {

		private EntityQuerySimpleFilter<T, PartyCategory> category;

		public PartyTypeQuery(Class<T> entityClass) {
			super(entityClass);
			category = createFilter(PartyType_.category);
		}

		public EntityQuerySimpleFilter<T, PartyCategory> category() {
			return category;
		}
	}
}