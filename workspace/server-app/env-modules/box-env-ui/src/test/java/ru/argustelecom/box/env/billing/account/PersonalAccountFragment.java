package ru.argustelecom.box.env.billing.account;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class PersonalAccountFragment {

    @FindByFuzzyId("add_personal_account")
    private Link openCreateDialog;

    @FindByFuzzyId("personal_account_creation_form-number")
    private InputText number;

    @FindByFuzzyId("personal_account_creation_form-create_button")
    private Button create;

    @FindBy(css = " #personal_account_info_form-accounts_accordion_view .ui-accordion-header")
    private List<WebElement> personalAccounts;

    public void openCreateDialog() {
        openCreateDialog.click();
    }

    public void setNumber(String value) {
        number.input(value);
    }

    public void create() {
        create.click();
    }

    public List<String> getPersonalAccounts() {
        List<String> personalAccountNumbers = new ArrayList<>();

        personalAccounts.forEach(item -> {
            String number = item.findElement(By.cssSelector(".personal-account-title")).getText();
            // для ЛС хардкордно присоединяется приставка "НЛС "
            checkArgument(number.startsWith("НЛС "), "Ожидали увидеть приставку \"НЛС\" в названии ЛС");
            personalAccountNumbers.add(number.replace("НЛС ", ""));
        });

        return personalAccountNumbers;
    }

    public String getBalance(String number) {

        WebElement personalAccount = personalAccounts.stream()
                .filter(item -> {
                    String rawNumber = item.findElement(By.cssSelector(".personal-account-title")).getText();
                    // для ЛС хардкордно присоединяется приставка "НЛС "
                    checkArgument(rawNumber.startsWith("НЛС "), "Ожидали увидеть приставку \"НЛС\" в названии ЛС");
                    return rawNumber.replace("НЛС ", "").equals(number);
                })
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("personalAccounts"));

        return personalAccount
                .findElement(By.cssSelector(".money-component"))
                .getText();
    }

}
