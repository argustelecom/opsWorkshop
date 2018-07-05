package ru.argustelecom.box.env.saldo.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface SaldoImportMessagesBundle {

    @Message("Дата-время")
    String dateTime();

    @Message("Результат предварительной обработки")
    String preliminaryResults();

    @Message("Результат импорта данных")
    String resultsOfImport();

    @Message("Импортировано")
    String imported();

    @Message("Не импортировано")
    String notImported();

    @Message("Причина")
    String reason();

    @Message("Количество")
    String quantity();

    @Message("Номер реестра")
    String registerNumber();

    @Message("Транзакция с данными параметрами уже существует")
    String transactionAlreadyExists();

    @Message("Не выбран файл для обработки")
    String fileIsNotSpecified();

    @Message("Выберите тип импортируемого файла для загрузки информации о платежах")
    String specifyFileType();

    // DefaultItemError
    @Message("Попытка повторного импорта документа")
    String defaultItemErrorTryingToReimport();

    @Message("Не удалось определить лицевой счёт")
    String defaultItemErrorImpossibleDetermineAccount();

    @Message("Некорректный номер лицевого счёта")
    String defaultItemErrorIncorrectAccountNumber();

    @Message("Некорректная сумма платежа")
    String defaultItemErrorIncorrectSum();

    @Message("Некорректный номер платёжного документа")
    String defaultItemErrorIncorrectPaymentNumber();

    @Message("Некорректная дата платёжного документа")
    String defaultItemErrorIncorrectPaymentDocDate();

    // ED108Register
    @Message("Значение общей суммы платежей по всем документам реестра. ")
    String ed108totalAmount();

    @Message("Значение даты платёжного поручения. ")
    String ed108date();

    // PaymentDocReason
    @Message("Основание: Платежный документ. %s от %s")
    String paymentDocReason(String number, String date);

    @Message("Платежный документ")
    String paymentDoc();

    // Register
    @Message("В заголовке имеются некорректные данные: ")
    String headerHasInvalidData();

    // ResultType
    @Message("Непригодные для импорта")
    String notSuitable();

    @Message("Требуется корректировка")
    String requiredCorrections();

    @Message("Готовы к импорту")
    String suitable();

    // SaldoRegister
    @Message("Значение общей суммы платежей по всем документам реестра. ")
    String totalRegisterSumValue();

    @Message("Значение даты формирования реестра. ")
    String registerDate();

    @Message("Значение начальной даты в диапазоне дат документов, входящих в реестр. ")
    String startDateValue();

    @Message("Значение конечной даты в диапазоне дат документов, входящих в реестр. ")
    String endDateValue();

    // ED108ImportService
    @Message("Импорт реестра ED108 %s")
    String ed108registerImport(String number);

    @Message("Кодировка файла не соответствует одной из кодировок WIN1251/KOI8-R/UTF-8.")
    String notKoi8REncoding();

    // RegisterFormat
    @Message("Реестр сальдо")
    String registerFormatSaldo();

    @Message("Платежное поручение на общую сумму с реестром")
    String registerFormatEd108();

    // RegisterImportService
    @Message("В файле есть строки, не соответствующие формату")
    String registerImportFileHasInvalidLines();

    @Message("Не найден ни один платежный документ")
    String registerImportPaymentDocumentNotFound();

    @Message("В файле не должно быть пустых строк")
    String registerImportFileHasEmptyLines();

    @Message("Импорт реестра сальдо %s")
    String saldoRegisterImport(String number);

    @Message("Кодировка файла не соответствует WIN1251")
    String notWin1251Encoding();

}
