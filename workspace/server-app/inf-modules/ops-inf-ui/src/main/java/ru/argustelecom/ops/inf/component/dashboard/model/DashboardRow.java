package ru.argustelecom.ops.inf.component.dashboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import ru.argustelecom.ops.inf.component.dashboard.DashboardUtils;

public class DashboardRow implements Serializable, Iterable<DashboardColumn>, VerticalMovable {

	protected DashboardModel dashboard;
	protected List<DashboardColumn> columns = new ArrayList<>();

	public DashboardModel getDashboard() {
		return dashboard;
	}

	protected void setDashboard(DashboardModel dashboard) {
		if (this.dashboard != null) {
			this.dashboard.rows.remove(this);
		}

		this.dashboard = dashboard;

		if (this.dashboard != null) {
			this.dashboard.rows.add(this);
		}
	}

	@Override
	public Iterator<DashboardColumn> iterator() {
		return columns.iterator();
	}

	public boolean hasColumns() {
		return !columns.isEmpty();
	}

	public List<DashboardColumn> getColumns() {
		return Collections.unmodifiableList(columns);
	}

	public void addColumn(DashboardColumn column) {
		column.setRow(this);
	}

	public void removeColumn(DashboardColumn column) {
		if (Objects.equals(column.getRow(), this))
			column.setRow(null);
	}

	@Override
	public boolean moveUp() {
		return dashboard == null ? false : DashboardUtils.movePrev(dashboard.rows, this);
	}

	@Override
	public boolean moveDown() {
		return dashboard == null ? false : DashboardUtils.moveNext(dashboard.rows, this);
	}

	private static final long serialVersionUID = 1L;
}
