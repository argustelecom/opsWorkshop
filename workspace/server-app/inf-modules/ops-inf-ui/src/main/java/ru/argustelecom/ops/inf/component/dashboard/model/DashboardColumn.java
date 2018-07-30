package ru.argustelecom.ops.inf.component.dashboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import ru.argustelecom.ops.inf.component.dashboard.DashboardUtils;

public class DashboardColumn extends WidgetContainer
		implements Serializable, Iterable<DashboardSubColumn>, HorizontalMovable {

	protected DashboardRow row;
	protected List<DashboardSubColumn> subColumns = new ArrayList<>();

	public DashboardRow getRow() {
		return row;
	}

	protected void setRow(DashboardRow row) {
		if (this.row != null) {
			this.row.columns.remove(this);
		}

		this.row = row;

		if (this.row != null) {
			this.row.columns.add(this);
		}
	}

	@Override
	public Iterator<DashboardSubColumn> iterator() {
		return subColumns.iterator();
	}

	public boolean hasSubColumns() {
		return !subColumns.isEmpty();
	}

	public List<DashboardSubColumn> getSubColumns() {
		return Collections.unmodifiableList(subColumns);
	}

	public void addSubColumn(DashboardSubColumn subColumn) {
		subColumn.setColumn(this);
	}

	public void removeSubColumn(DashboardSubColumn subColumn) {
		if (Objects.equals(subColumn.getColumn(), this))
			subColumn.setColumn(null);
	}

	@Override
	public boolean moveLeft() {
		return row == null ? false : DashboardUtils.movePrev(row.columns, this);
	}

	@Override
	public boolean moveRight() {
		return row == null ? false : DashboardUtils.moveNext(row.columns, this);
	}

	private static final long serialVersionUID = 1L;
}
