package ru.argustelecom.box.env.measure;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.TreeNode;

import ru.argustelecom.box.env.measure.model.BaseMeasureUnit;
import ru.argustelecom.box.env.measure.model.DerivedMeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.measure.nls.MeasureMessagesBundle;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

// FIXME [localization]

@Named(value = "measureUnitCreationDM")
@PresentationModel
public class MeasureUnitCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 628059865720130288L;

	@Inject
	private MeasureUnitRepository mur;

	private Callback<DerivedMeasureUnit> callback;

	private List<BaseMeasureUnit> baseMeasures;

	private DerivedMeasureUnit newMeasure;
	private String code;
	private String name;
	private BaseMeasureUnit group;
	private Long factor;
	private String symbol;

	public void create() {
		newMeasure = mur.createDerivedMeasureUnit(code, name, factor, group, symbol);
		OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		MeasureMessagesBundle measureMessages = LocaleUtils.getMessages(MeasureMessagesBundle.class);

		Notification.info(
				overallMessages.success(),
				measureMessages.measureCreated(newMeasure.getName())
		);
		callback.execute(newMeasure);
		cancel();
	}

	public void cancel() {
		newMeasure = null;
		code = null;
		name = null;
		group = null;
		factor = null;
		symbol = null;
	}

	public List<BaseMeasureUnit> getGroups() {
		if (baseMeasures == null) {
			baseMeasures = mur.findAllBaseMeasures();
		}
		return baseMeasures;
	}

	public void validateSymbol(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String symbol = (String) value;

		Object oldValue = ((UIInput) component).getValue();
		if (value == null && oldValue == null || Objects.equals(value, oldValue)) {
			return;
		}
		validateSymbol(symbol);
	}

	public void setSelectedNode(TreeNode selectedNode) {
		if (!(selectedNode == null || selectedNode.getType().equals("NoGroup")))
			group = ((MeasureUnit) selectedNode.getData()).getGroup();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void validateSymbol(String value) {
		OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		MeasureMessagesBundle measureMessages = LocaleUtils.getMessages(MeasureMessagesBundle.class);

		if (value == null) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					overallMessages.error(), measureMessages.nameRequired()));
		}

		List<MeasureUnit> allMeasures = mur.findAll();
		for (MeasureUnit measure : allMeasures) {
			if (measure.getSymbol().equals(value)) {
				throw new ValidatorException(
						new FacesMessage(FacesMessage.SEVERITY_ERROR, overallMessages.error(),
								measureMessages.nameShouldBeUnique()));
			}
		}
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public void setCallback(Callback<DerivedMeasureUnit> callback) {
		this.callback = callback;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BaseMeasureUnit getGroup() {
		return group;
	}

	public void setGroup(BaseMeasureUnit group) {
		this.group = group;
	}

	public Long getFactor() {
		return factor;
	}

	public void setFactor(Long factor) {
		this.factor = factor;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}