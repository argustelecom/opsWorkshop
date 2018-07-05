package ru.argustelecom.box.env.filter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import com.beust.jcommander.internal.Lists;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.filter.model.FilterParam;
import ru.argustelecom.box.env.filter.nls.FilterMessagesBundle;
import ru.argustelecom.box.env.login.LoginService;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ListFilterPresetFrameModel implements Serializable {

	private static final long serialVersionUID = -4423475220441690169L;

	@Inject
	private ListFilterPresetRepository repository;

	@Inject
	private ListFilterPresetAppService appService;

	@Inject
	private LoginService loginService;

	@Inject
	private ListFilterPresetDtoTranslator translator;

	@Getter
	@Setter
	private ListFilterPresetDto selectedListFilterPreset;

	private List<ListFilterPresetDto> listFilterPresets;

	private FilterViewState filterViewState;
	@Getter
	private String page;
	private Employee owner;

	public void preRender(FilterViewState filterViewState, String page) {
		this.filterViewState = filterViewState;
		this.page = page;
		this.owner = loginService.getCurrentEmployee();
	}

	public void createListFilterPreset() {
		RequestContext.getCurrentInstance().update("list_filter_preset_creation_form-list_filter_preset_creation_dlg");
		RequestContext.getCurrentInstance().execute("PF('listFilterPresetCreationDlgVar').show()");
	}

	public void createOrSave() {
		if (selectedListFilterPreset == null) {
			createListFilterPreset();
		} else {
			saveListFilterPreset();
		}
	}

	public void saveListFilterPreset() {
		Set<FilterParam> filterParams = filterViewState.getAsFilterParams();
		OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		FilterMessagesBundle filterMessages = LocaleUtils.getMessages(FilterMessagesBundle.class);

		if (filterParams.isEmpty()) {
			Notification.error(filterMessages.cannotSavePreset(), filterMessages.noParamsSpecified());
			return;
		}

		appService.saveListFilterPreset(selectedListFilterPreset.getId(), filterParams);
		Notification.info(overallMessages.success(), filterMessages.presetSaved(selectedListFilterPreset.getName()));
	}

	public void removeListFilterPreset() {
		appService.removeListFilterPreset(selectedListFilterPreset.getId());
		listFilterPresets.remove(selectedListFilterPreset);
		selectedListFilterPreset = null;
		filterViewState.clearParams();
	}

	public ListFilterPresetDto createListFilterPresetDto() {
		Employee owner = loginService.getCurrentEmployee();
		return ListFilterPresetDto.builder().ownerId(owner.getId()).page(page)
				.filterParams(Lists.newArrayList(filterViewState.getAsFilterParams())).build();
	}

	public void applyListFilterPreset() {
		filterViewState.applyFilterParams(selectedListFilterPreset.getFilterParams());
	}

	public List<ListFilterPresetDto> getListFilterPresets() {
		return listFilterPresets = repository.findByPageAndOwner(page, owner).stream()
						.map(listFilterPreset -> translator.translate(listFilterPreset)).collect(Collectors.toList());
	}

	public Callback<ListFilterPresetDto> getSelectedPresetCallback() {
		return presetDto -> selectedListFilterPreset = presetDto;
	}
}
