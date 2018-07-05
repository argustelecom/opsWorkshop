package ru.argustelecom.box.env.contract;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.box.env.activity.ActivityFragment;
import ru.argustelecom.box.env.contract.lifecycle.ContractLifecycle;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.customer.CustomerFragment;
import ru.argustelecom.box.env.lifecycle.LifecycleHistoryFragment;
import ru.argustelecom.box.env.lifecycle.LifecycleRoutingFragment;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Calendar;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.EditableSection;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.comp.OutputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

/**
 * Страница: Домашняя страница -> Клиенты -> <Клиент>
 *
 * @author v.sysoev, a.isakov
 */
@Location("views/env/contract/ContractCardView.xhtml")
public class ContractCardPage extends PageInf {

	@FindByFuzzyId("contract_attributes_form-validity_date_from")
	public EditableSection validFromEditableSection;

	@FindByFuzzyId("contract_attributes_form-validity_date_to")
	public EditableSection validToEditableSection;

	@FindByFuzzyId("contract_attributes_form-number")
	public OutputText number;

	@FindByFuzzyId("contract_attributes_form-type")
	public OutputText type;

	@FindByFuzzyId("contract_attributes_form-state")
	public OutputText state;

	@FindByFuzzyId("contract_attributes_form-valid_from_out")
	public OutputText validFrom;

	@FindByFuzzyId("contract_attributes_form-valid_to_out")
	public OutputText validTo;

	@FindByFuzzyId("contract_attributes_form-valid_from_input")
	private Calendar validFromCalendar;

	@FindByFuzzyId("contract_attributes_form-valid_to_input")
	private Calendar validToCalendar;

	@FindBy(xpath = "//body")
	public CustomerFragment customerBlock;

	@FindBy(xpath = "//body")
	public LifecycleRoutingFragment lifecycleRoutingBlock;

	@FindBy(xpath = "//body")
	public ActivityFragment activityBlock;

	@FindByFuzzyId("contract_attributes_form-delete")
	public Button delete;

	@FindBy(xpath = "//body")
	public ContractExtensionFragment contractExtensionBlock;

	@FindByFuzzyId("contract_attributes_form-broker")
	public OutputText broker;

	public void setValidFrom(String value) {
		validFromEditableSection.edit();
		validFromCalendar.clear().setValue(value);
		validFromEditableSection.save();
	}

	public void setValidTo(String value) {
		validToEditableSection.edit();
		validToCalendar.clear().setValue(value);
		validToEditableSection.save();
	}
}