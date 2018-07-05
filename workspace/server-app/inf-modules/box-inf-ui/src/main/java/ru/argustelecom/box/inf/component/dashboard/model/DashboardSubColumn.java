package ru.argustelecom.box.inf.component.dashboard.model;

import java.io.Serializable;

import ru.argustelecom.box.inf.component.dashboard.DashboardUtils;

public class DashboardSubColumn extends WidgetContainer implements Serializable, VerticalMovable {

	protected DashboardColumn column;

	public DashboardColumn getColumn() {
		return column;
	}

	protected void setColumn(DashboardColumn item) {
		if (this.column != null) {
			this.column.subColumns.remove(this);
		}

		this.column = item;

		if (this.column != null) {
			this.column.subColumns.add(this);
		}
	}

	@Override
	public boolean moveUp() {
		return column == null ? false : DashboardUtils.movePrev(column.subColumns, this);
	}

	@Override
	public boolean moveDown() {
		return column == null ? false : DashboardUtils.moveNext(column.subColumns, this);
	}

	private static final long serialVersionUID = 1L;
}
