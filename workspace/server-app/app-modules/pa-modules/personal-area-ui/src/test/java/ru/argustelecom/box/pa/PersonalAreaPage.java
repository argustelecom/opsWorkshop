package ru.argustelecom.box.pa;

import org.jboss.arquillian.graphene.page.Location;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.List;

@Location("")
public class PersonalAreaPage extends PageInf {

    @FindBy(css = "#personal_account_info_form a")
    private List<WebElement> accounts;

    // не можем использовать OutputText т.к. падаем на проверке соответствия html тега компоненту
    @FindByFuzzyId("current_account_number")
    private WebElement currentAccount;

    public void setCurrentAccount(String value) {
        accounts.stream()
                .filter(item -> item.getText().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("accounts"))
                .click();
    }

    public String getCurrentAccount() {
        return currentAccount.getText();
    }

}
