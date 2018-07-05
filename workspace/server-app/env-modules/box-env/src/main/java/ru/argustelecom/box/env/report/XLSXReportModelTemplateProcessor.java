package ru.argustelecom.box.env.report;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.report.api.ReportTemplateFormat.XLSX;
import static ru.argustelecom.box.env.report.api.ReportTemplateFormat.getReportTemplateFormatBy;
import static ru.argustelecom.system.inf.configuration.ServerRuntimeProperties.instance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;

import ru.argustelecom.box.env.report.api.ReportModelTemplateProcessor;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.system.inf.exception.BusinessException;

public class XLSXReportModelTemplateProcessor implements ReportModelTemplateProcessor {

	private static Pattern PARAMETER = Pattern.compile("\\$\\{[a-zA-Z0-9_.]+}");

	private ReportModelTemplate template;
	private XSSFWorkbook workBook;

	public XLSXReportModelTemplateProcessor(ReportModelTemplate template) {
		checkNotNull(template);
		checkState(XLSX.equals(getReportTemplateFormatBy(template.getMimeType())));

		this.template = template;
	}

	@Override
	public void process() {
		init();

		clean();

		saveAndClose();
	}

	protected void init() {
		try (InputStream is = template.getBinaryStream()) {
			workBook = new XSSFWorkbook(is);
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	protected void clean() {
		removeWhiteSpaceFromCellWithParameter();
	}

	protected void saveAndClose() {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			workBook.write(os);
			workBook.close();
			template.setTemplate(createBlob(os.toByteArray()));
		} catch (IOException e) {
			throw new BusinessException();
		}
	}

	protected void removeWhiteSpaceFromCellWithParameter() {
		XSSFFormulaEvaluator evaluator = workBook.getCreationHelper().createFormulaEvaluator();
		DataFormatter dataFormatter = new DataFormatter();

		for (Sheet sheet : workBook) {
			for (Row row : sheet) {
				for (Cell cell : row) {
					evaluator.evaluate(cell);
					String value = dataFormatter.formatCellValue(cell, evaluator);
					String trimmedValue = value.trim();
					if (!value.equals(trimmedValue) && PARAMETER.matcher(trimmedValue).matches()) {
						cell.setCellValue(trimmedValue);
					}
				}
			}
		}
	}

	protected Blob createBlob(byte[] body) {
		return ((Session) instance().lookupEntityManager().getDelegate()).getLobHelper().createBlob(body);
	}
}
