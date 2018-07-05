package ru.argustelecom.box.env.billing.account;

import org.jboss.arquillian.graphene.page.Location;

import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InfoBlock;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Table;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

/**
 * Страница: Домашняя страница -> Клиенты -> <Клиент> -> <Лицевой счёт>
 * 
 * @author v.semchenko
 */
@Location("views/env/billing/account/PersonalAccountView.xhtml")
public class PersonalAccountPage extends PageInf {

	@FindByFuzzyId("personal_account_attributes_form")
	private InfoBlock personalAccountAttributesForm;

	@FindByFuzzyId("transaction_list_form-property_managed_buttons")
	private Link openDialogCreateTransaction;

	@FindByFuzzyId("transaction_creation_form-amount")
	private InputText amount;

	@FindByFuzzyId("transaction_creation_form-reason_type")
	private ComboBox reasonType;

	@FindByFuzzyId("transaction_creation_form-reason_number")
	private InputText reasonNumber;

	@FindByFuzzyId("transaction_creation_form-create_button")
	private Button buttonCreateTransaction;

	@FindByFuzzyId("transaction_list_form-transaction_table_data")
	private Table transactionsTable;

	public String getBalance() {
		return personalAccountAttributesForm.getValueAfter("Баланс");
	}

	public void openDialogCreateTransaction() {
		openDialogCreateTransaction.click();
	}

	public void setAmountOfMoney(String input) {
		amount.input(input);
	}

	public void chooseReasonType(String input) {
		reasonType.select(input);
	}

	public void setReasonNumber(String input) {
		reasonNumber.input(input);
	}

	public void clickOnButtonCreateTransaction() {
		buttonCreateTransaction.click();
	}

	/**
	 * Получаем значение ячейки по номеру строки из столбца "Название основания" из таблицы транзакций лицевого счета
	 * 
	 * @param input
	 *            номер строки
	 * @return
	 */
	public String getReasonNumber(int input) {
		return transactionsTable.getRow(input).findCell("Название основания").getTextString();
	}

	/**
	 * Получаем значение ячейки по номеру строки из столбца "Тип основания" из таблицы транзакций лицевого счета
	 * 
	 * @param input
	 *            номер строки
	 * @return
	 */
	public String getReasonType(int input) {
		return transactionsTable.getRow(input).findCell("Тип основания").getTextString();
	}

	/**
	 * Получаем значение ячейки по номеру строки из столбца "Сумма" из таблицы транзакций лицевого счета
	 * 
	 * @param input
	 *            номер строки
	 * @return
	 */
	public String getAmount(int input) {
		return transactionsTable.getRow(input).findCell("Сумма").getTextString();
	}
}
