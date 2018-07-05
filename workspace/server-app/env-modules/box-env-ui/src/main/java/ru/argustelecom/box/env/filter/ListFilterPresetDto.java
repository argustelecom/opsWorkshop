package ru.argustelecom.box.env.filter;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.filter.model.FilterParam;
import ru.argustelecom.box.env.filter.model.ListFilterPreset;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class ListFilterPresetDto extends ConvertibleDto {

	private Long id;
	private String name;
	private Long ownerId;
	private String page;
	private List<FilterParam> filterParams;

	public ListFilterPresetDto() {
		super();
	}

	@Builder
	public ListFilterPresetDto(Long id, String name, Long ownerId, String page, List<FilterParam> filterParams) {
		this.id = id;
		this.name = name;
		this.ownerId = ownerId;
		this.page = page;
		this.filterParams = filterParams;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return ListFilterPresetDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return ListFilterPreset.class;
	}
}
