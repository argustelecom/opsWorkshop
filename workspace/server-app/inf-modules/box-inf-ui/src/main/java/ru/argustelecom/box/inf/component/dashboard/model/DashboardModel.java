package ru.argustelecom.box.inf.component.dashboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class DashboardModel implements Serializable, Iterable<DashboardRow> {

	protected List<DashboardRow> rows = new ArrayList<>();

	@Override
	public Iterator<DashboardRow> iterator() {
		return rows.iterator();
	}
	
	public boolean hasRows() {
		return !rows.isEmpty();
	}
	
	public List<DashboardRow> getRows() {
		return Collections.unmodifiableList(rows);
	}

	public void addRow(DashboardRow row) {
		row.setDashboard(this);
	}

	public void removeRow(DashboardRow row) {
		if (Objects.equals(row.getDashboard(), this))
			row.setDashboard(null);
	}

	private static final long serialVersionUID = 1L;
}
