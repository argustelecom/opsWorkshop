package ru.argustelecom.ops.inf.component.dashboard;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.primefaces.renderkit.CoreRenderer;

import ru.argustelecom.ops.inf.component.dashboard.model.DashboardColumn;
import ru.argustelecom.ops.inf.component.dashboard.model.DashboardModel;
import ru.argustelecom.ops.inf.component.dashboard.model.DashboardRow;
import ru.argustelecom.ops.inf.component.dashboard.model.DashboardSubColumn;
import ru.argustelecom.ops.inf.component.dashboard.model.WidgetContainer;

import com.google.common.base.Strings;

@FacesRenderer(componentFamily = Dashboard.COMPONENT_FAMILY, rendererType = DashboardRenderer.RENDERER_TYPE)
public class DashboardRenderer extends CoreRenderer {

	public static final String RENDERER_TYPE = "ru.argustelecom.ops.component.DashboardRenderer";

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Dashboard dashboard = (Dashboard) component;
		encodeMarkup(facesContext, dashboard);
	}

	protected void encodeMarkup(FacesContext context, Dashboard dashboard) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = dashboard.getClientId(context);

		writer.startElement("div", dashboard);
		writer.writeAttribute("id", clientId, "id");

		String styleClass = "ui-ops-dashboard";
		if (dashboard.getStyleClass() != null) {
			styleClass += " " + dashboard.getStyleClass();
		}
		
		writer.writeAttribute("class", styleClass, "styleClass");

		String style = dashboard.getStyle();
		if (style != null) {
			writer.writeAttribute("style", style, "style");
		}

		DashboardModel model = dashboard.getModel();
		for (DashboardRow row : model) {
			encodeRow(context, dashboard, row);
		}

		writer.endElement("div");
	}

	protected void encodeRow(FacesContext context, Dashboard dashboard, DashboardRow row) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);
		writer.writeAttribute("class", "ui-ops-dashboard-row", null);

		for (DashboardColumn column : row) {
			encodeColumn(context, dashboard, column);
		}

		writer.endElement("div");
	}

	protected void encodeColumn(FacesContext context, Dashboard dashboard, DashboardColumn column) throws IOException {
		if (!column.isEnabled()) {
			return;
		}

		DashboardItem item = findItem(column.getWidgetId(), dashboard);

		if (column.hasSubColumns() || item != null && item.isRendered()) {
			ResponseWriter writer = context.getResponseWriter();
			writer.startElement("div", null);
			writer.writeAttribute("class", getColumnStyleClass(dashboard.isResponsive(), column), null);

			if (column.hasSubColumns()) {
				for (DashboardSubColumn subColumn : column) {
					encodeSubColumn(context, dashboard, subColumn);
				}
			} else {
				encodeItem(context, column, item);
			}

			writer.endElement("div");
		}
	}

	protected void encodeSubColumn(FacesContext context, Dashboard dashboard, DashboardSubColumn subColumn)
			throws IOException {

		if (!subColumn.isEnabled()) {
			return;
		}

		DashboardItem item = findItem(subColumn.getWidgetId(), dashboard);

		if (item != null && item.isRendered()) {
			ResponseWriter writer = context.getResponseWriter();
			writer.startElement("div", null);
			writer.writeAttribute("class", "ui-ops-dashboard-subcolumn", null);

			encodeItem(context, subColumn, item);

			writer.endElement("div");
		}
	}

	protected void encodeItem(FacesContext context, WidgetContainer container, DashboardItem item) throws IOException {
		if (!item.isRendered()) {
			return;
		}

		ResponseWriter writer = context.getResponseWriter();
		{
			// ui-ops-dashboard-indent
			writer.startElement("div", null);
			writer.writeAttribute("class", "ui-ops-dashboard-indent", null);
			{
				// ui-ops-dashboard-item
				String clientId = item.getClientId(context);
				writer.startElement("div", item);
				writer.writeAttribute("id", clientId, "id");

				String styleClass = "ui-ops-dashboard-item";
				if (item.getStyleClass() != null) {
					styleClass += " " + item.getStyleClass();
				}
				writer.writeAttribute("class", styleClass, "styleClass");

				String style = getItemStyle(container, item);
				if (style != null) {
					writer.writeAttribute("style", style, "style");
				}

				{
					// ui-ops-dashboard-item content
					UIComponent content = checkNotNull(item.getContent(), "Facet 'content' not found in " + clientId);
					UIComponent contentAlt = item.getContentAlt();

					if (item.isContentRendered()) {
						content.encodeAll(context);
					} else if (contentAlt != null) {
						contentAlt.encodeAll(context);
					}
				}

				writer.endElement("div");
			}
			writer.endElement("div");
		}
	}

	protected String getColumnStyleClass(boolean responsive, DashboardColumn column) {
		StringBuilder sb = new StringBuilder();
		sb.append("ui-ops-dashboard-column ");
		sb.append(column.getWidth().getStyleClass());
		if (responsive) {
			sb.append(" ui-responsive");
		}
		return sb.toString();
	}

	protected String getItemStyle(WidgetContainer container, DashboardItem item) {
		StringBuilder sb = new StringBuilder();

		if (item.getStyle() != null) {
			String style = item.getStyle().trim();
			sb.append(style);
			if (!style.endsWith(";")) {
				sb.append(";");
			}
		}

		if (container.getHeight() != null) {
			sb.append("height:").append(container.getHeight()).append(";");
		}

		return Strings.emptyToNull(sb.toString());
	}

	protected DashboardItem findItem(String id, Dashboard dashboard) {
		for (DashboardItem item : dashboard.getItems()) {
			if (item.getId().equals(id))
				return item;
		}
		return null;
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		// Rendering happens on encodeEnd
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
