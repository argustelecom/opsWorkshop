package ru.argustelecom.box.env.saldo.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface SaldoExportMessagesBundle {

    // calculation type
    @Message("В конце периода на следующий период")
    String atTheEndOfPeriodForNextPeriod();

    // SaldoExportEventState
    @Message("Успешно")
    String eventStateSuccessfully();

    @Message("Неуспешно")
    String eventStateUnsuccessfully();

    // SaldoExportEventType
    @Message("Выгрузка данных")
    String eventTypeExportData();

    @Message("Отправка выгрузки")
    String eventTypeSend();

    @Message("Информирование об ошибке")
    String eventTypeSendErrorMessage();

    // SaldoExportIssueState
    @Message("Выгружена")
    String issueStateExported();

    @Message("Ошибки при формировании выгрузки")
    String issueStateFaulted();

    @Message("Ожидает обработки")
    String issueStateWaiting();

    @Message("Повторная выгрузка")
    String issueStateRestored();

    // ErrorInfoSendingQueueHandler
    @Message("Необходимо заполнить список адресатов для получения информации об ошибках при выгрузке реестров сальдо")
    String specifyMailList();

    @Message("Ошибка при выгрузке реестра сальдо")
    String saldoExportError();

    // SaldoExportQueueHandler
    @Message("Период: %s. %s.")
    String saldoPeriod(String periodUnit, String calculationType);

    // SaldoSendingQueueHandler
    @Message("Нет ни одного адресата для отправки выгрузки реестров сальдо")
    String noRecipientFound();

    @Message("Выгрузка реестра сальдо")
    String saldoExport();

    // SaldoExportService
    @Message("Содержит реестр сальдо по лицевым счетам АСР Argus Box")
    String defaultNote();
}
