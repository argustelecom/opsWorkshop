package ru.argustelecom.box.env.companyinfo;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/companyinfo/CompanyInfoView.xhtml")
public class CompanyInfoPage extends PageInf {

    @FindByFuzzyId("company_info_attributes_form-mark_principal")
    public Button makePrincipal;

    @FindByFuzzyId("owner_tree_form-owner_tree")
    public Tree owners;

    @FindBy(css = "#owner_tree_form-tree_managed_buttons a:nth-child(1)")
    public Link openCreationDialog;

    @FindBy(xpath = "//body")
    public CreationDialog creationDialog;

    class CreationDialog {

        @FindByFuzzyId("owner_creation_form-party_type_label")
        public ComboBox partyTypes;

        @FindByFuzzyId("owner_creation_form-name")
        public InputText name;

        @FindByFuzzyId("owner_creation_form-tax_rate")
        public InputText taxRate;

        @FindByFuzzyId("owner_creation_form-principal")
        public Checkbox isPrincipal;

        @FindByFuzzyId("owner_creation_form-create_button")
        public Button create;
    }

    @FindBy(xpath = "//body")
    public Attributes attributes;

    class Attributes {

        @FindByFuzzyId("company_info_attributes_form-name_out")
        public OutputText name;

        @FindByFuzzyId("company_info_attributes_form-party_type")
        public OutputText partyType;

        @FindByFuzzyId("company_info_attributes_form-tax_rate")
        public OutputText taxRate;
    }

    @FindBy(css = "#owner_additional_params_form a")
    public Link openAdditionalParamCreationDialog;

    @FindBy(xpath = "//body")
    public AdditionalParamCreationDialog additionalParamCreationDialog;

    class AdditionalParamCreationDialog {

        @FindByFuzzyId("owner_additional_params_edit_form-param_name")
        public InputText name;

        @FindByFuzzyId("owner_additional_params_edit_form-param_keyword")
        public InputText keyword;

        @FindByFuzzyId("owner_additional_params_edit_form-param_value")
        public InputText value;

        @FindByFuzzyId("owner_additional_params_edit_form-submit_button")
        public Button create;
    }

    @FindByFuzzyId("#owner_additional_params_form-owner_additional_params_table")
    public Table additionalParams;
}