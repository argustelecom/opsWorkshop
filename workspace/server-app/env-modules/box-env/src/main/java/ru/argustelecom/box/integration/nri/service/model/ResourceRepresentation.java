package ru.argustelecom.box.integration.nri.service.model;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.integration.nri.nls.IntegrationNriMessageBundle;

import java.io.Serializable;

/**
 * Представления о ресурсах для интеграции ТУ с внешними системами
 * см. BOX-2738
 * Created by s.kolyada on 11.04.2018.
 */
@Getter
public class ResourceRepresentation implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Тип ресурса
	 */
	private ResourceType resourceType;

	/**
	 * Идентификатор ресурса в системе ТУ
	 */
	private Long nriId;

	/**
	 * Наименование ресурса в системе ТУ
	 */
	private String name;

	/**
	 * Конструктор
	 * @param resourceType тип ресурса
	 * @param nriId идентификатор в ТУ
	 * @param name имя в ТУ
	 */
	public ResourceRepresentation(ResourceType resourceType, Long nriId, String name) {
		IntegrationNriMessageBundle messages = LocaleUtils.getMessages(IntegrationNriMessageBundle.class);
		Validate.notNull(resourceType, messages.resourceTypeParamShouldNotBeEmpty());
		Validate.notNull(nriId, messages.idParamShouldNotBeEmpty());
		Validate.notEmpty(name, messages.nameParamShouldNotBeEmpty());

		this.resourceType = resourceType;
		this.nriId = nriId;
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ResourceRepresentation)) return false;

		ResourceRepresentation that = (ResourceRepresentation) o;

		if (resourceType != that.resourceType) return false;
		if (!nriId.equals(that.nriId)) return false;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = resourceType.hashCode();
		result = 31 * result + nriId.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("ResourceRepresentation{");
		sb.append("resourceType=").append(resourceType);
		sb.append(", nriId=").append(nriId);
		sb.append(", name='").append(name).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
