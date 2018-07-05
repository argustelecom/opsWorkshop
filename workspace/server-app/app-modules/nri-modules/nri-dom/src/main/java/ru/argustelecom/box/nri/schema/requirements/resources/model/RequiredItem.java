package ru.argustelecom.box.nri.schema.requirements.resources.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.model.RequirementType;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Элемент требования к ресурсу
 * Базовый класс для реализаций вариантов требований к ресурсам
 * Включает в себя требование к ресурсу, его параметрам и такие же требования его дочерним ресурсам
 * Created by s.kolyada on 18.09.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "required_item")
@Getter
@Setter
public class RequiredItem extends ResourceRequirement {

	private static final long serialVersionUID = 1L;

	/**
	 * Спецификация требуемого ресурса
	 */
	@ManyToOne
	@JoinColumn(name = "resource_specification_id", nullable = false)
	private ResourceSpecification resourceSpecification;

	/**
	 * Родительское требование
	 */
	@ManyToOne
	@JoinColumn(name = "parent_required_item_id", referencedColumnName = "id")
	private RequiredItem parent;

	/**
	 * Схема, к которой относится данное требование
	 */
	@ManyToOne
	@JoinColumn(name = "tp_schema_id")
	protected ResourceSchema tpSchema;

	/**
	 * Дочерние требования
	 */
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RequiredItem> children = new ArrayList<>();

	/**
	 * Требуемые параметры
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "requiredItem")
	private List<RequiredParameterValue> requiredParameters = new ArrayList<>();

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected RequiredItem() {
		super(RequirementType.PHYSICAL_RESOURCE_REQUIREMENT);
	}

	/**
	 * Конструктор
	 *
	 * @param id                    идентификатор
	 * @param resourceSchema        схема подключения
	 * @param resourceSpecification спецификация ресурса
	 * @param parent                родительское требование
	 * @param children              дочерние требования
	 * @param requiredParameters    требуемые параметры
	 */
	@Builder
	public RequiredItem(Long id, ResourceSchema resourceSchema, ResourceSpecification resourceSpecification,
						RequiredItem parent, List<RequiredItem> children,
						List<RequiredParameterValue> requiredParameters) {
		super(id, RequirementType.PHYSICAL_RESOURCE_REQUIREMENT);
		this.tpSchema = resourceSchema;
		this.resourceSpecification = resourceSpecification;
		this.parent = parent;
		this.name = type.name() + " " + (resourceSpecification == null ? "" : resourceSpecification.getName()) + " " + new Date();
		if (CollectionUtils.isEmpty(children)) {
			this.children = new ArrayList<>();
		} else {
			this.children = children;
		}
		if (CollectionUtils.isEmpty(requiredParameters)) {
			this.requiredParameters = new ArrayList<>();
		} else {
			this.requiredParameters = requiredParameters;
		}
	}

	/**
	 * Список дочерних требований
	 *
	 * @return Список дочерних требований
	 */
	public List<RequiredItem> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Список требований к параметрам
	 *
	 * @return Список требований к параметрам
	 */
	public List<RequiredParameterValue> getRequiredParameters() {
		return Collections.unmodifiableList(requiredParameters);
	}

	/**
	 * Удалить дочерний элемент
	 *
	 * @param item дочерний элемент
	 * @return истина, если удалось, иначе ложь
	 */
	public Boolean removeChild(RequiredItem item) {
		return this.children.remove(item);
	}

	/**
	 * Добавить дочерний элемент
	 *
	 * @param child дочерний элемент
	 * @return истина, если удалось, иначе ложь
	 */
	public Boolean addChild(RequiredItem child) {
		child.setParent(this);
		return this.children.add(child);
	}

	/**
	 * Добавить треб значение параметр а
	 *
	 * @param param треб значение параметр а
	 * @return истина, если удалось, иначе ложь
	 */
	public Boolean addParameter(RequiredParameterValue param) {
		return requiredParameters.add(param);
	}

	/**
	 * Удалить треб значение параметр а
	 *
	 * @param param треб значение параметр а
	 * @return истина, если удалось, иначе ложь
	 */
	public Boolean removeParameter(RequiredParameterValue param) {
		return requiredParameters.remove(param);
	}

	@Override
	public ResourceSchema getSchema() {
		return tpSchema == null ? bookSchema : tpSchema;
	}
}
