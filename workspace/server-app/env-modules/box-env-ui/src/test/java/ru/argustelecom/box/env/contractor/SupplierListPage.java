package ru.argustelecom.box.env.contractor;

import org.jboss.arquillian.graphene.page.Location;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/contractor/SupplierListView.xhtml")
public class SupplierListPage extends PageInf {

    @FindByFuzzyId("supplier_search_result_form-create_supplier_button")
    public Button openCreationDialog;

    @FindByFuzzyId("supplier_creation_form-new_legal_name")
    public InputText legalName;

    @FindByFuzzyId("supplier_creation_form-new_brand_name")
    public InputText brandName;

    @FindByFuzzyId("supplier_creation_form-new_party_type_label")
    public ComboBox partyTypes;

    @FindByFuzzyId("supplier_creation_form-create_button")
    public Button create;

}