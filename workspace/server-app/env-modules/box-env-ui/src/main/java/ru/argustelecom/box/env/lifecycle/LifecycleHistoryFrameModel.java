package ru.argustelecom.box.env.lifecycle;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.history.LifecycleHistoryService;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "lifecycleHistoryFm")
@PresentationModel
public class LifecycleHistoryFrameModel implements Serializable {

	@Inject
	private LifecycleHistoryService historySvc;

	@Inject
	private LifecycleHistoryItemDtoTranslator lifecycleHistoryItemDtoTr;

	private LifecycleObject<?> businessObject;
	private List<LifecycleHistoryItemDto> items;

	public void preRender(LifecycleObject<?> businessObject) {
		if (!Objects.equals(this.businessObject, businessObject)) {
			this.businessObject = businessObject;
			items = null;
			initItems();
		}
	}

	public List<LifecycleHistoryItemDto> getItems() {
		return items;
	}

	private void initItems() {
		if (items == null) {
			items = historySvc.getHistory(businessObject).stream().map(lifecycleHistoryItemDtoTr::translate)
					.filter(item -> !item.getFromState().equals(item.getToState())).collect(toList());
			items.sort(Comparator.comparing(LifecycleHistoryItemDto::getTransitionTime));
		}
	}

	private static final long serialVersionUID = -7143116639828298381L;

}