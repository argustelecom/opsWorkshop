package ru.argustelecom.box.env.numerationpattern;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;
import ru.argustelecom.system.inf.page.PresentationState;

import javax.inject.Named;
import java.io.Serializable;

@Named("numerationPatternVs")
@PresentationState
@Getter
@Setter
public class NumerationPatternViewState implements Serializable {
	private Integer activeTab;

	public void onTabChange(TabChangeEvent event) {
		TabView tabView = (TabView) event.getComponent();
		activeTab = tabView.getChildren().indexOf(event.getTab());
	}

	private static final long serialVersionUID = -2616301092836413717L;
}
