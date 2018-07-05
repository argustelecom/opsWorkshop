package ru.argustelecom.box.env.type.model.properties;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.env.type.model.lookup.LookupCategory;
import ru.argustelecom.box.env.type.model.lookup.LookupEntry;
import ru.argustelecom.box.env.type.nls.TypeMessagesBundle;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@Entity
@Access(AccessType.FIELD)
public abstract class AbstractLookupProperty<V> extends TypeProperty<V> {

	private static final long serialVersionUID = -4564297461996050161L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lkp_category_id")
	private LookupCategory category;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	public AbstractLookupProperty() {
	}

	/**
	 * Конструктор предназначен для инстанцирования свойства его холдером. Не делай этот конструктор публичным. Не делай
	 * других публичных конструкторов. Свойство должны инстанцироваться сугубо холдером или спецификацией (делегирует
	 * холдеру) для обеспецения корректного связывания холдера(спецификации) и свойства.
	 *
	 * @param holder
	 *            - владелец свойства, часть спецификации
	 * @param id
	 *            - уникальный идентификатор свойства. Получается при помощи генератора инкапсулированного в
	 *            MetadataUnit.generateId()
	 * @see TypePropertyHolder#createProperty(Class, String, Long)
	 * @see MetadataUnit#generateId()
	 * @see MetadataUnit#generateId(EntityManager)
	 */
	public AbstractLookupProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	public LookupCategory getCategory() {
		return category;
	}

	public void setCategory(LookupCategory category) {
		setCategory(category, true);
	}

	protected void setCategory(LookupCategory category, boolean clearDefaultWhenCategoriesConflicts) {
		if (!Objects.equals(this.category, category)) {
			this.category = category;
			if (clearDefaultWhenCategoriesConflicts) {
				checkDefaultCategoryAndClearWhenConflicts(this.category);
			}
		}
	}

	protected ValidationResult<TypeProperty<V>> validateEntry(LookupEntry value) {
		ValidationResult<TypeProperty<V>> result = ValidationResult.success();
		if (!isSameCategory(category, value)) {
			TypeMessagesBundle messages = LocaleUtils.getMessages(TypeMessagesBundle.class);

			String defined = category.getObjectName();
			String passed = value.getCategory().getObjectName();
			result.error(this, messages.lookupEntryCategoryMismatchWithDefinition(defined, passed));
		}
		return result;
	}

	protected abstract void checkDefaultCategoryAndClearWhenConflicts(LookupCategory newCategory);

	protected boolean isSameCategory(LookupCategory category, LookupEntry value) {
		return category == null || value == null || Objects.equals(category, value.getCategory());
	}
}
