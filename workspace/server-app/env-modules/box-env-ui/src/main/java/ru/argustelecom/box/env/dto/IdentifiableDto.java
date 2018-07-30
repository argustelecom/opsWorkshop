package ru.argustelecom.box.env.dto;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.ensure;

import javax.persistence.EntityManager;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.argustelecom.system.inf.modelbase.Identifiable;

// TODO: должен стать типизированным IdentifiableDto<T extends Identifiable>
public interface IdentifiableDto {

	Long getId();

	Class<? extends Identifiable> getEntityClass();

	default String getIdentifiableStringValue() {
		return String.format("%s-%d", getEntityClass().getSimpleName(), getId());
	}

	@JsonIgnore
	default Identifiable getIdentifiable() {
		return getIdentifiable(null);
	}

	default Identifiable getIdentifiable(EntityManager em) {
		return ensure(em).find(getEntityClass(), getId());
	}

}