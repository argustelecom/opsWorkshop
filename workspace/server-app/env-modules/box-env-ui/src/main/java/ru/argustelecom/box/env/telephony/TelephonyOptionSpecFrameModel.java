package ru.argustelecom.box.env.telephony;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.tuple.Pair;
import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionSpecAppService;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("telephonyOptionSpecFm")
@PresentationModel
public class TelephonyOptionSpecFrameModel implements Serializable {

	private static final long serialVersionUID = 3441983806716844680L;

	@Inject
	private TelephonyOptionSpecAppService optionSpecAs;

	@Inject
	private TelephonyOptionSpecDtoTranslator optionSpecDtoTr;

	private Map<Long, List<TelephonyOptionSpecDto>> optionSpecsCache = new HashMap<>();

	public List<TelephonyOptionSpecDto> getOptionSpecs(ServiceSpec serviceSpec) {
		List<TelephonyOptionSpecDto> optionSpecs = optionSpecsCache.get(serviceSpec.getId());
		if (optionSpecs == null) {
			optionSpecs = createAndCache(serviceSpec);
		}

		return optionSpecs;
	}

	public Callback<Pair<Long, List<TelephonyOptionSpecDto>>> getCallback() {
		return serviceSpecOptions -> optionSpecsCache.put(serviceSpecOptions.getKey(), serviceSpecOptions.getValue());
	}

	public void onEditDialogOpen() {
		RequestContext.getCurrentInstance().update("option_spec_edit_form");
		RequestContext.getCurrentInstance().execute("PF('telephonyOptionSpecEditDlg').show();");
	}

	private List<TelephonyOptionSpecDto> createAndCache(ServiceSpec serviceSpec) {
		List<TelephonyOptionSpecDto> optionSpecs = optionSpecDtoTr
				.translate(optionSpecAs.findByServiceSpec(serviceSpec.getId()));
		optionSpecsCache.put(serviceSpec.getId(), optionSpecs);
		return optionSpecs;
	}

}
