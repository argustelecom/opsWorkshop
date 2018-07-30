package ru.argustelecom.box.env.dto;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.ensure;

import javax.persistence.EntityManager;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * DTO, который представляет собой некий {@linkplain ru.argustelecom.box.inf.modelbase.BusinessObject бизнес объект} или
 * {@linkplain ru.argustelecom.box.inf.modelbase.BusinessDirectory объект справочника}. Преднозначен для использования в
 * тех местах, где нужно просто выбрать объект(выпадающие списки, пик листы, прочее), а также где об объекте нужно знать
 * только имя и уменять получить из DTO доменный объект.
 */
// TODO: должен наследовать IdentifiableDto(который д.б. типизированным) или даже ConvertibleDto, не стал делать во
// время спринта, так как много зависимостей. Так же нужно объединить BusinessObjectDtoTranslator и DefaultDtoTranslator
// Когда будет рефакторинг, пофиксить ru.argustelecom.box.env.filter.FilterViewState:83,
// ru.argustelecom.box.env.filter.FilterViewState:129, ru.argustelecom.box.env.service.ServiceListFilterViewState
@Getter
// ДЛЯ JSF, в коде не вызывать, используйте транслятор
@NoArgsConstructor
@EqualsAndHashCode(of = { "id", "entityClass" })
public class BusinessObjectDto<T extends Identifiable & NamedObject> implements NamedObject {

	private Long id;
	private String name;
	private Class<T> entityClass;

	@SuppressWarnings("unchecked")
	BusinessObjectDto(T entity) {
		this.id = entity.getId();
		this.name = entity.getObjectName();
		this.entityClass = (Class<T>) entity.getClass();
	}

	@Override
	public String getObjectName() {
		return name;
	}

	@Override
	public final String toString() {
		return entityClass != null ? String.format("%s-%d", entityClass.getSimpleName(), getId()) : null;
	}

	@JsonIgnore
	public T getIdentifiable() {
		return getIdentifiable(null);
	}

	public T getIdentifiable(EntityManager em) {
		return ensure(em).find(entityClass, getId());
	}

}