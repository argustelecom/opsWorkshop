package ru.argustelecom.box.env.saldo.imp;

import static org.apache.commons.io.IOUtils.LINE_SEPARATOR_UNIX;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;

import ru.argustelecom.box.env.saldo.imp.RegisterImportViewModel.Step;
import ru.argustelecom.box.env.saldo.imp.model.Container;
import ru.argustelecom.box.env.saldo.imp.model.Register;
import ru.argustelecom.box.env.saldo.imp.model.ResultType;
import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.chrono.TZ;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "registerImportDownloadFM")
@PresentationModel
public class RegisterImportDownloadFrameModel implements Serializable {

	private static final long serialVersionUID = -8283136813869971641L;

	private static final String HEADER_PATTERN = "#%s %s\n";

	private Register register;

	public StreamedContent downloadContainer(Container container, String stepId) throws IOException {
		Date now = new Date();
		return new ByteArrayContent(generateFileContent(container, stepId, now), "txt",
				generateFileName(container, now));
	}

	public StreamedContent downloadAll(String stepId) throws IOException {
		Date now = new Date();
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ZipOutputStream zipStream = new ZipOutputStream(outputStream)) {
			for (Container container : register.getContainers()) {
				ZipEntry zipEntry = new ZipEntry(generateFileName(container, now));
				zipStream.putNextEntry(zipEntry);
				zipStream.write(generateFileContent(container, stepId, now));
				zipStream.closeEntry();
			}

			zipStream.finish();
			return new ByteArrayContent(outputStream.toByteArray(), "zip", generateArchiveName(stepId, now));
		}
	}

	private byte[] generateFileContent(Container container, String stepId, Date date) throws IOException {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			IOUtils.write(generateHeader(container, stepId, date), os, register.getCharset());
			IOUtils.writeLines(container.getInitialData(), LINE_SEPARATOR_UNIX, os, register.getCharset());
			return os.toByteArray();
		}
	}

	private String generateHeader(Container container, String stepId, Date date) {
		SaldoImportMessagesBundle saldoImportMessages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);

		StringBuilder headerBuilder = new StringBuilder();

		headerBuilder.append(String.format(HEADER_PATTERN, saldoImportMessages.dateTime(),
				DateUtils.format(date, DateUtils.DATETIME_DEFAULT_PATTERN, TZ.getUserTimeZone())));
		if (stepId.equals(Step.PARSE.getId())) {
			headerBuilder.append(String.format(HEADER_PATTERN, saldoImportMessages.preliminaryResults(),
					container.getType().getName()));
		} else {
			if (stepId.equals(Step.IMPORT_FINISHED.getId())) {
				String value = container.getType().equals(ResultType.SUITABLE) ?
						saldoImportMessages.imported() :
						saldoImportMessages.notImported();
				headerBuilder.append(String.format(HEADER_PATTERN, saldoImportMessages.resultsOfImport(), value));
			} else {
				throw new SystemException("Register download is not supported on this step");
			}
		}

		headerBuilder.append(String.format(HEADER_PATTERN, saldoImportMessages.reason(), container.getErrorsDescription()))
				.append(String.format(HEADER_PATTERN, saldoImportMessages.quantity(), container.getItems().size()))
				.append(String.format(HEADER_PATTERN, saldoImportMessages.registerNumber(), getRegister().getNumber()));
		return headerBuilder.toString();
	}

	private String generateFileName(Container container, Date date) {
		StringBuilder fileNameBuilder = new StringBuilder();
		fileNameBuilder.append(DateUtils.format(date, DateUtils.DATETIME_DEFAULT_PATTERN, TZ.getUserTimeZone()));
		String errorDescription = container.getErrorsDescription();
		if (!errorDescription.isEmpty()) {
			fileNameBuilder.append(" ").append(errorDescription.substring(0, errorDescription.lastIndexOf(".")));
		}
		fileNameBuilder.append(".txt");
		return fileNameBuilder.toString();
	}

	private String generateArchiveName(String stepId, Date date) {
		SaldoImportMessagesBundle saldoImportMessages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);

		StringBuilder archiveNameBuilder = new StringBuilder();
		if (stepId.equals(Step.PARSE.getId())) {
			archiveNameBuilder.append(DateUtils.format(date, DateUtils.DATETIME_DEFAULT_PATTERN, TZ.getUserTimeZone()))
					.append(" ").append(saldoImportMessages.preliminaryResults()).append(".zip");
		} else {
			if (stepId.equals(Step.IMPORT_FINISHED.getId())) {
				archiveNameBuilder
						.append(DateUtils.format(date, DateUtils.DATETIME_DEFAULT_PATTERN, TZ.getUserTimeZone()))
						.append(" ").append(saldoImportMessages.resultsOfImport()).append(".zip");
			} else {
				throw new SystemException("Register download is not supported on this step");
			}
		}

		return archiveNameBuilder.toString();
	}

	public Register getRegister() {
		return register;
	}

	public void setRegister(Register register) {
		this.register = register;
	}

}
