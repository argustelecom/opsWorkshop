package ru.argustelecom.box.env.map.page.mapsearchresult;

import ru.argustelecom.system.inf.map.component.model.MapModel;
import ru.argustelecom.system.inf.page.ObservablePresentationState;
import ru.argustelecom.system.inf.page.PresentationState;
import ru.argustelecom.system.inf.transaction.UnitOfWork;
import ru.argustelecom.system.inf.utils.CDIHelper;

import javax.inject.Inject;

/**
 * Текущий режим просмотра результатов поиска
 * 
 */
@PresentationState
public class CurrentMapSearchResult extends ObservablePresentationState<String> {
	private static final long serialVersionUID = 1L;
	private static final NullMapSearchResult NULL_VIEW_RESULT_MODE = new NullMapSearchResult();
	private static final SearchResultDataChangedEvent dataChangedEvent = new SearchResultDataChangedEvent();

	private String viewResultModeBeanName;

	@Override
	public String getValue() {
		return viewResultModeBeanName;
	}

	@SuppressWarnings("unchecked")
	public <T extends MapSearchResult> T getValueAsObject() {
		return viewResultModeBeanName == null ? (T) NULL_VIEW_RESULT_MODE : (T) CDIHelper.lookupCDIBean(MapSearchResult.class, viewResultModeBeanName);
	}

	@Override
	protected void doSetValue(String value) {
		getValueAsObject().onFinish();
		viewResultModeBeanName = value;
		getValueAsObject().onStart();
	}

	public void updateData() {
		getValueAsObject().loadData();
		fire(dataChangedEvent);
	}

	public String getDataChangedEvent() {
		return SearchResultDataChangedEvent.class.getSimpleName();
	}

	public static class SearchResultDataChangedEvent {}

	public static class NullMapSearchResult extends MapSearchResult {
		private static final long serialVersionUID = 4254688061716986183L;

		private NullMapSearchResult(){}

		@Override
		public void onFinish() {
		}

		@Override
		public boolean isSupportedObject(Object o) {
			return true;
		}

		@Override
		public void loadData() {
		}

		@Override
		public void populateMapModel(MapModel mapModel) {
		}
	}
}
