package ru.argustelecom.box.env.commodity;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ListBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Tree;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

@Location("views/env/commodity/CommodityTypeDirectoryView.xhtml")
public class CommodityTypeDirectoryPage extends PageInf {

    @FindBy(css = "#commodity_tree_form-tree_managed_buttons i.fa-edit")
    public Link openCreationDialog;

    @FindBy(css = "#commodity_tree_form-tree_managed_buttons i.fa-trash")
    private Link delete;

    @FindByFuzzyId("commodity_tree_form-category")
    public ListBox commodityTypeCategory;

    @FindBy(xpath = "//body")
    public CreationDialog creationDialog;

    @FindBy(xpath = "//body")
    public Attributes attributes;

    @FindByFuzzyId("commodity_tree_form-commodity_tree")
    public Tree commodityTree;

    @FindBy(xpath = "//body")
    public Options options;

    public void delete() {
        delete.click();
        currentDialog().click("Да");
    }

    @FindBy(xpath = "//body")
    public TelephonyZones telephonyZones;
}