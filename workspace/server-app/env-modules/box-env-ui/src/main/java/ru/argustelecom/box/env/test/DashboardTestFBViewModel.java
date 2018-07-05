package ru.argustelecom.box.env.test;

import ru.argustelecom.box.inf.component.dashboard.model.DashboardColumn;
import ru.argustelecom.box.inf.component.dashboard.model.DashboardColumnWidth;
import ru.argustelecom.box.inf.component.dashboard.model.DashboardModel;
import ru.argustelecom.box.inf.component.dashboard.model.DashboardRow;
import ru.argustelecom.box.inf.component.dashboard.model.DashboardSubColumn;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class DashboardTestFBViewModel extends ViewModel {

	@Override
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	public DashboardModel getModel() {

		DashboardModel model = new DashboardModel();

		{
			DashboardRow row1 = new DashboardRow();
			{
				DashboardColumn row1_col1 = new DashboardColumn();
				row1_col1.setWidgetId("row1_col1");
				row1_col1.setWidth(DashboardColumnWidth.width_10u);
				row1.addColumn(row1_col1);
			}
			model.addRow(row1);
		}

		{
			DashboardRow row2 = new DashboardRow();
			{
				DashboardColumn row2_col1 = new DashboardColumn();
				row2_col1.setWidth(DashboardColumnWidth.width_5u);
				{
					DashboardSubColumn sub11 = new DashboardSubColumn();
					sub11.setWidgetId("row2_col1_sub1");
					row2_col1.addSubColumn(sub11);
				}

				{
					DashboardSubColumn sub212 = new DashboardSubColumn();
					sub212.setWidgetId("row2_col1_sub2");
					row2_col1.addSubColumn(sub212);
				}
				/*{
					DashboardSubColumn sub3 = new DashboardSubColumn();
					sub3.setWidgetId("row2_col1_sub3");
					row2_col1.addSubColumn(sub3);
				}

				{
					DashboardSubColumn sub4 = new DashboardSubColumn();
					sub4.setWidgetId("row2_col1_sub4");
					row2_col1.addSubColumn(sub4);
				}*/
				row2.addColumn(row2_col1);
			}
			{
				DashboardColumn row2_col2 = new DashboardColumn();
				row2_col2.setWidth(DashboardColumnWidth.width_5u);
				{
					DashboardSubColumn sub1 = new DashboardSubColumn();
					sub1.setWidgetId("row2_col2_sub1");
					row2_col2.addSubColumn(sub1);
				}

				{
					DashboardSubColumn sub2 = new DashboardSubColumn();
					sub2.setWidgetId("row2_col2_sub2");
					row2_col2.addSubColumn(sub2);
				}
				
				{
					DashboardSubColumn sub3 = new DashboardSubColumn();
					sub3.setWidgetId("row2_col2_sub3");
					row2_col2.addSubColumn(sub3);
				}

				{
					DashboardSubColumn sub4 = new DashboardSubColumn();
					sub4.setWidgetId("row2_col2_sub4");
					row2_col2.addSubColumn(sub4);
				}
				row2.addColumn(row2_col2);
			}
			model.addRow(row2);
		}

		{
			DashboardRow row3 = new DashboardRow();
			{
				DashboardColumn row3_col1 = new DashboardColumn();
				row3_col1.setWidgetId("row3_col1");
				row3_col1.setWidth(DashboardColumnWidth.width_10u);
				row3.addColumn(row3_col1);
			}
			model.addRow(row3);
		}

		return model;
	}

	private static final long serialVersionUID = 1L;
}
