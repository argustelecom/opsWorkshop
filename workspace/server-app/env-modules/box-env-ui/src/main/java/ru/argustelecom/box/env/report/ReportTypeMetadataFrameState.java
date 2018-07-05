package ru.argustelecom.box.env.report;

import java.io.Serializable;

import javax.inject.Named;

import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.system.inf.page.PresentationState;

@Getter
@Setter
@Named("reportTypeMetadataFs")
@PresentationState
public class ReportTypeMetadataFrameState implements Serializable {
	private Integer activeTab;

	public void onTabChange(TabChangeEvent event) {
		TabView tabView = (TabView) event.getComponent();
		activeTab = tabView.getChildren().indexOf(event.getTab());
	}

	private static final long serialVersionUID = -8363743305254876370L;
}
