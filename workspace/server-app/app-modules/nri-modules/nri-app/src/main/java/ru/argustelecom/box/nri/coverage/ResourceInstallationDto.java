package ru.argustelecom.box.nri.coverage;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.building.BuildingElementDto;
import ru.argustelecom.box.nri.coverage.model.ResourceInstallation;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ДТО точки монтажа ресурса
 * Created by s.kolyada on 31.08.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ResourceInstallationDto extends ConvertibleDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Комментарий
	 */
	@Setter
	private String comment;

	/**
	 * Место монтирования
	 */
	@Setter
	private BuildingElementDto installedAt;

	/**
	 * Элементы строения, входящие в зону покрытия
	 */
	@Setter
	private List<BuildingElementDto> cover = new ArrayList<>();

	/**
	 * Ресурс, к которому относится данная установка
	 */
	@Setter
	@Getter
	private ResourceInstanceDto resource;

	/**
	 * Конструктор
	 * @param id идентификатор
	 * @param comment коммент
	 * @param installedAt точка монтажа
	 * @param cover покрываемые элементы
	 * @param resource ресурс который монтируется
	 */
	@Builder
	public ResourceInstallationDto(Long id, String comment, BuildingElementDto installedAt, List<BuildingElementDto> cover, ResourceInstanceDto resource) {
		this.id = id;
		this.comment = comment;
		this.installedAt = installedAt;
		this.resource = resource;
		this.cover = Optional.ofNullable(cover).orElse(new ArrayList<>());
	}

	@Override
	public Class<ResourceInstallation> getEntityClass() {
		return ResourceInstallation.class;
	}

	@Override
	public Class<ResourceInstallationDtoTranslator> getTranslatorClass() {
		return ResourceInstallationDtoTranslator.class;
	}

	public List<BuildingElementDto> getCover() {
		return Collections.unmodifiableList(this.cover);
	}
}
