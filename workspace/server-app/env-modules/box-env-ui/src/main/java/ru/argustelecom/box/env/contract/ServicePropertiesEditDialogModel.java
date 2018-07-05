package ru.argustelecom.box.env.contract;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.service.ServiceDto;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "servicePropertiesEditDm")
@PresentationModel
public class ServicePropertiesEditDialogModel implements Serializable {

	@Getter
	@Setter
	private List<ServiceDto> services;

	public void onDialogOpened() {
		RequestContext.getCurrentInstance().update("service_spec_edit_form-service_spec_edit_dlg");
		RequestContext.getCurrentInstance().execute("PF('serviceSpecEditDlg').show()");
	}

	private static final long serialVersionUID = 4975469688677783273L;

}