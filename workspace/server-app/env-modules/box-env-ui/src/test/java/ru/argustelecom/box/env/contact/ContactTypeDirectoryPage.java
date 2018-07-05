package ru.argustelecom.box.env.contact;

import org.jboss.arquillian.graphene.page.Location;
import ru.argustelecom.system.inf.page.PageInf;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import static com.google.common.base.Preconditions.checkState;

@Location("views/env/contact/ContactTypeDirectoryView.xhtml")
public class ContactTypeDirectoryPage extends PageInf {

	@FindByFuzzyId("contact_types_form-managed_buttons")
	public Link openDialogCreateContactType;

	@FindByFuzzyId("contact_type_edit_form-new_contact_category_input")
	public ComboBox contactCategory;

	@FindByFuzzyId("contact_type_edit_form-new_contact_name")
	public InputText contactTypeName;

	@FindByFuzzyId("contact_type_edit_form-new_contact_short_name")
	public InputText shortContactTypeName;

	@FindByFuzzyId("contact_type_edit_form-create_button")
	public Button completeCreateContactType;

	@FindByFuzzyId("contact_types_form-contact_types_table_data")
	public Table contactTypes;

	@FindByFuzzyId("contact_types_form-remove_button")
	public WebElement buttonDeleteContactType;

	@FindByFuzzyId("global_msg_warn_container")
	public WebElement warnBlock;

	@FindBy(css=".ui-growl-icon-close")
	public WebElement closeWarnBlock;

	@FindByFuzzyId("contact_type_edit_form-cancel_button")
	public Button cancelCreateContactType;

	@FindByFuzzyId("contact_types_form-contact_types_table")
	public Table createdContactTypes;

	public void deleteContactType(String contactTypeName) {
		Row rowToDelete = createdContactTypes.findRow(contactTypeName);

		rowToDelete.select();
		// переодически теста падает т.к. кнопка "Удалить" недоступна. Я предположил, что это происхоидт из-за того,
		// что некорректно выделяется строка и кнока не нажимается. Чтобы отловить этот кейс поставил проверку
		checkState(rowToDelete.isSelected(), "Строка таблицы оказалалось невыделенной");
		buttonDeleteContactType.click();
		currentDialog().click("Да");
	}
}