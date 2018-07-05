package ru.argustelecom.box.env.contract;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

public class ContractExtensionFragment {

    @FindByFuzzyId("cotract_extansions_form-add_contract_extension")
    public Link openCreationDialog;

    @FindByFuzzyId("contract_extension_creation_form-contract_type_label")
    public ComboBox extensionType;

    @FindByFuzzyId("contract_extension_creation_form-new_document_date_input")
    public Calendar validFrom;

    @FindByFuzzyId("contract_extension_creation_form-number")
    public InputText number;

    @FindByFuzzyId("contract_extension_creation_form-create_button")
    public Button create;

    @FindBy(css = ".ui-datascroller-list")
    private WebElement contractExtansionsListContainer;

    public String getContractExtensionState(String contractExtensionNumber) {
        return contractExtansionsListContainer
                .findElement(By.xpath("//a[contains(text(),\"" + contractExtensionNumber + "\")]"))
                .findElement(By.xpath("../../../li[2]/span[2]")).getText();
    }

    public String getContractExtensionType(String contractExtensionNumber) {
        return contractExtansionsListContainer
                .findElement(By.xpath("//a[contains(text(),\"" + contractExtensionNumber + "\")]"))
                .findElement(By.xpath("../../../../../div[2]/ul/li[1]/span")).getText();
    }

    public String getContractExtensionValidFrom(String contractExtensionNumber) {
        return contractExtansionsListContainer
                .findElement(By.xpath("//a[contains(text(),\"" + contractExtensionNumber + "\")]"))
                .findElement(By.xpath("../../../../../div[2]/ul/li[2]/span")).getText();
    }
}
