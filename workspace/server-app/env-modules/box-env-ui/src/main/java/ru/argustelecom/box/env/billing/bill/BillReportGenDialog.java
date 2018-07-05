package ru.argustelecom.box.env.billing.bill;

import static org.apache.commons.io.IOUtils.LINE_SEPARATOR_UNIX;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;

import com.google.common.collect.Lists;

import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.chrono.TZ;

public class BillReportGenDialog implements Serializable {

	private static final String FILE_CHARSET = "UTF-8";

	@Getter
	protected List<BillReportDto> billErrorReportDtoList;

	protected List<BillReportDto> translateBillsToBillErrorReportDtos(BillReportDtoTranslator billReportDtoTranslator,
			List<Bill> bills) {
		return bills.stream().map(billReportDtoTranslator::translate).collect(Collectors.toList());
	}

	protected StreamedContent downloadReport(String reportName, String... comments) throws IOException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			writeFileContent(outputStream, comments);
			return new ByteArrayContent(outputStream.toByteArray(), "plain/txt", generateFileName(reportName));
		}
	}

	private void writeFileContent(OutputStream os, String... comments) throws IOException {
		IOUtils.writeLines(Lists.newArrayList(comments), LINE_SEPARATOR_UNIX, os, FILE_CHARSET);
		IOUtils.writeLines(billErrorReportDtoList, LINE_SEPARATOR_UNIX, os, FILE_CHARSET);
	}

	private String generateFileName(String reportName) {
		return String.format("%s %s.txt",
				DateUtils.format(new Date(), DateUtils.DATETIME_DEFAULT_PATTERN, TZ.getUserTimeZone()), reportName);
	}

	private static final long serialVersionUID = 5930359299619179518L;
}
