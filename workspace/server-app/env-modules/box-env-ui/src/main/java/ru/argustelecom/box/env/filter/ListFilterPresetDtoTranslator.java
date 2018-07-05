package ru.argustelecom.box.env.filter;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.filter.model.ListFilterPreset;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ListFilterPresetDtoTranslator implements DefaultDtoTranslator<ListFilterPresetDto, ListFilterPreset> {

	public ListFilterPresetDto translate(ListFilterPreset listFilterPreset) {
		//@formatter:off
		return ListFilterPresetDto.builder()
				.id(listFilterPreset.getId())
				.name(listFilterPreset.getName())
				.ownerId(listFilterPreset.getOwner().getId())
				.page(listFilterPreset.getPage())
				.filterParams(Lists.newArrayList(listFilterPreset.getFilterParams()))
			.build();
		//@formatter:on
	}
}
