package ru.argustelecom.box.env.contract;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.ArrayList;
import java.util.List;

/**
 * Страница: Домашняя страница -> Клиенты
 *
 * @author v.sysoev, a.isakov
 */
@Location("views/env/contract/ContractListView.xhtml")
public class ContractListPage extends PageInf {

    @FindBy(xpath = "//body")
    public ContractFilterFragment contractFilterBlock;

    @FindByFuzzyId("contract_search_result_form-personnel_table")
    public Table searchResults;

    @FindByFuzzyId("contract_search_result_form-create_contract_button")
    public Button openDialogCreateContract;

    @FindByFuzzyId("contract_creation_form-contract_type_label")
    public ComboBox type;

    @FindByFuzzyId("contract_creation_form-customer_input")
    public AutoComplete customer;

    @FindByFuzzyId("contract_creation_form-valid_from")
    public Calendar validFrom;

    @FindByFuzzyId("contract_creation_form-valid_to_input")
    public Calendar validTo;

    @FindByFuzzyId("contract_creation_form-number")
    public InputText number;

    @FindByFuzzyId("contract_creation_form-create_button")
    public Button create;

    @FindByFuzzyId("contract_creation_form-payment_condition_label")
    public ComboBox paymentConditions;

    @FindByFuzzyId("contract_creation_form-broker_label")
    public ComboBox broker;

}
