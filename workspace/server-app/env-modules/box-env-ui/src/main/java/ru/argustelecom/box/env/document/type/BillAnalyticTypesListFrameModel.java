package ru.argustelecom.box.env.document.type;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import ru.argustelecom.box.env.document.type.BillAnalyticTypeFrameModel.AnalyticTypeRow;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "billAnalyticTypesListFm")
@PresentationModel
public class BillAnalyticTypesListFrameModel implements Serializable {

	public boolean showCategoryName(Map.Entry<String, List<AnalyticTypeRow>> entry) {
		return entry.getValue().stream().anyMatch(AnalyticTypeRow::getSelected);
	}

	private static final long serialVersionUID = 984208845182415743L;
}
