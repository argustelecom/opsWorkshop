package ru.argustelecom.box.nri.loading;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.loading.model.ResourceLoading;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;
import java.util.List;

/**
 * ДТО нагрузки на ресурс
 * Created by s.kolyada on 06.02.2018.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ResourceLoadingDto  extends ConvertibleDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Имя нагрузки
	 */
	private String loadingName;

	/**
	 * Нагружаемые ресурсы
	 */
	private List<LogicalResourceDto> resources;

	@Builder
	public ResourceLoadingDto(Long id, String loadingName, List<LogicalResourceDto> resources) {
		this.id = id;
		this.loadingName = loadingName;
		this.resources = resources;
	}

	@Override
	public Class<ResourceLoadingDtoTranslator> getTranslatorClass() {
		return ResourceLoadingDtoTranslator.class;
	}

	@Override
	public Class<ResourceLoading> getEntityClass() {
		return ResourceLoading.class;
	}

	@Override
	public String getObjectName() {
		return loadingName;
	}
}
