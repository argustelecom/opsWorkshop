package ru.argustelecom.box.env.lifecycle;

import ru.argustelecom.system.inf.testframework.it.ui.comp.Row;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Table;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

public class LifecycleHistoryFragment {

    @FindByFuzzyId("lifecycle_history_table_data")
    private Table lifecycleHistory;

    public String getLastLifecycleTransitionFromState() {
        return getLastLifecycleTransition().getCell(1).getTextString();
    }

    public String getLastLifecycleTransitionToState() {
        return getLastLifecycleTransition().getCell(2).getTextString();
    }

    private Row getLastLifecycleTransition() {
        int rowCount = lifecycleHistory.getRowCount();

        return lifecycleHistory.getRow(rowCount - 1);
    }
}
