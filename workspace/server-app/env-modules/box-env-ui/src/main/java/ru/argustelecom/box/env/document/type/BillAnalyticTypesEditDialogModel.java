package ru.argustelecom.box.env.document.type;

import static ru.argustelecom.box.env.document.type.BillAnalyticTypeFrameModel.AnalyticTypeRow;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import lombok.Setter;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("billAnalyticTypesEditDlg")
@PresentationModel
public class BillAnalyticTypesEditDialogModel implements Serializable {

	@Setter
	private Map<String, List<AnalyticTypeRow>> rows;

	@Setter
	private Callback<Map<String, List<AnalyticTypeRow>>> billAnalyticTypeCallback;

	public void onSubmit() {
		billAnalyticTypeCallback.execute(rows);
	}

	public void onCancel() {

	}

	private static final long serialVersionUID = 8786349276174599333L;

}
