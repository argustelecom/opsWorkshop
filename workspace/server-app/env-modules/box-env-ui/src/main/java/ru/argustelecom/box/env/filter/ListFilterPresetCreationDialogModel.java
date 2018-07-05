package ru.argustelecom.box.env.filter;

import java.io.Serializable;

import javax.inject.Inject;

import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.filter.nls.FilterMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ListFilterPresetCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 8733180355839349883L;

	@Inject
	private ListFilterPresetAppService appService;

	@Inject
	private ListFilterPresetDtoTranslator listFilterPresetDtoTranslator;

	@Getter
	@Setter
	private ListFilterPresetDto listFilterPreset;

	@Setter
	private Callback<ListFilterPresetDto> selectedPresetCallback;

	public void create() {
		if (listFilterPreset.getFilterParams().isEmpty()) {
			FilterMessagesBundle filterMessages = LocaleUtils.getMessages(FilterMessagesBundle.class);
			Notification.error(filterMessages.cannotSavePreset(), filterMessages.noParamsSpecified());
			return;
		}
		ListFilterPresetDto savedPreset = listFilterPresetDtoTranslator
				.translate(appService.createListFilterPreset(listFilterPreset.getName(), listFilterPreset.getOwnerId(),
						listFilterPreset.getPage(), Sets.newHashSet(listFilterPreset.getFilterParams())));
		selectedPresetCallback.execute(savedPreset);
	}

	public void clear() {
		listFilterPreset = null;
	}

}
