package ru.argustelecom.box.env.billing.invoice.provision;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.box.env.lifecycle.LifecycleRoutingFragment;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/billing/provision/ProvisionTermsDirectoryView.xhtml")
public class ProvisionTermsDirectoryPage {

    @FindBy(css = "#provision_terms_tree_form-provision_terms_tree_managed_buttons i.fa-edit")
    public Link openCreationDialog;

    @FindBy(xpath = "//body")
    public CreationDialog creationDialog;

    @FindByFuzzyId("provision_terms_attributes_form-name_out")
    public OutputText name;

    @FindByFuzzyId("provision_terms_attributes_form-description_out")
    public OutputText description;

    @FindBy(xpath = "//body")
    public ParametersBlock parameters;

    @FindBy(xpath = "//body")
    public LifecycleRoutingFragment lifecycleRoutingBlock;

    @FindByFuzzyId("provision_terms_tree_form-provision_terms_tree")
    public Tree provisionTermsTree;

    class CreationDialog {

        @FindByFuzzyId("recurrent_terms_creation_form-new_name")
        public InputText name;

        @FindByFuzzyId("recurrent_terms_creation_form-new_description")
        public InputText description;

        @FindByFuzzyId("recurrent_terms_creation_form-create_button")
        public Button create;
    }

    class ParametersBlock {

        @FindByFuzzyId("provision_terms_properties_form-charging_period")
        public EditableSection chargingPeriod;

        @FindByFuzzyId("provision_terms_properties_form-ptype_label")
        public ComboBox periodType;

        @FindByFuzzyId("provision_terms_properties_form-units_label")
        public ComboBox periodUnit;

        @FindByFuzzyId("provision_terms_properties_form-number")
        public InputText amount;

        @FindByFuzzyId("provision_terms_properties_form-reserve_funds_rule")
        public EditableSection reserveFundsRule;

        @FindByFuzzyId("provision_terms_properties_form-reserve_funds")
        public Checkbox reserveFunds;

        @FindByFuzzyId("provision_terms_properties_form-rounding_policy_rule")
        public EditableSection roundingPolicyRule;

        @FindByFuzzyId("provision_terms_properties_form-rounding_policy_label")
        public ComboBox roundingPolicy;

        @FindByFuzzyId("provision_terms_properties_form-lifecycle_qualifier")
        public EditableSection lifecycleQualifierParameter;

        @FindByFuzzyId("provision_terms_properties_form-lcpt_label")
        public ComboBox lifecycleQualifier;
    }
}
