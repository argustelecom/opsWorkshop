package ru.argustelecom.box.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ru.argustelecom.box.helper.model.Resource;
import ru.argustelecom.box.helper.model.ResourceBundle;

public class ExcelService {

	private static final int HEADER_ROW_NUMBER = 0;
	private static final String KEYWORD_HEADER = "keyword";
	private static final int KEYWORD_COLUMN_NUM = 0;

	public void write(Collection<ResourceBundle> bundles, Path path) {
		Workbook book = new XSSFWorkbook();
		bundles.stream().sorted((b1, b2) -> b1.getName().compareTo(b2.getName()))
				.forEach(bundle -> writeBundle(book, bundle));
		try {
			book.write(new FileOutputStream(path.toFile()));
			book.close();
			System.out.println("Ресурсы сохранены в файл: " + path.toRealPath());
		} catch (IOException e) {
			throw new RuntimeException("Невозможно сохранить ресурсы в excel: ", e);
		}
	}

	public List<ResourceBundle> read(Path path) {
		List<ResourceBundle> bundles = new ArrayList<>();
		try (Workbook book = new XSSFWorkbook(path.toString())) {
			for (int i = 0; i < book.getNumberOfSheets(); i++) {
				bundles.add(readBundle(book.getSheetAt(i)));
			}
		} catch (IOException e) {
			throw new RuntimeException("Невозможно загрузить ресурсы из excel: ", e);
		}

		return bundles;
	}

	private void writeBundle(Workbook book, ResourceBundle bundle) {
		Sheet sheet = book.createSheet(ResourceBundle.getSheetNameByName(bundle.getName()));
		CellStyle headerStyle = createHeaderStyle(book);
		int rowNum = HEADER_ROW_NUMBER;
		int cellNum = KEYWORD_COLUMN_NUM;

		Row row = sheet.createRow(rowNum);
		Cell name = row.createCell(cellNum);
		name.setCellValue(KEYWORD_HEADER);
		name.setCellStyle(headerStyle);

		for (Resource resource : bundle.getResources()) {
			Cell locale = row.createCell(++cellNum);
			locale.setCellValue(resource.getLocaleName());
			locale.setCellStyle(headerStyle);
			for (Entry<String, String> property : resource.getProperties().entrySet()) {
				Row propRow = findRow(sheet, property.getKey());
				if (propRow == null) {
					propRow = sheet.createRow(++rowNum);
					Cell propKeyword = propRow.createCell(KEYWORD_COLUMN_NUM);
					propKeyword.setCellValue(property.getKey());
				}
				Cell propValue = propRow.createCell(cellNum);
				propValue.setCellValue(property.getValue());
			}
			sheet.autoSizeColumn(cellNum);
		}

		sheet.autoSizeColumn(KEYWORD_COLUMN_NUM);
	}

	private ResourceBundle readBundle(Sheet sheet) {
		List<Resource> resources = new ArrayList<>();
		Row headerRow = sheet.getRow(HEADER_ROW_NUMBER);
		List<String> keywords = new ArrayList<>();
		sheet.forEach(row -> keywords.add(CellUtil.getCell(row, KEYWORD_COLUMN_NUM).getStringCellValue()));
		headerRow.forEach(cell -> {
			if (cell.getColumnIndex() != KEYWORD_COLUMN_NUM) {
				String localeName = cell.getStringCellValue();
				Map<String, String> properties = new HashMap<>();
				sheet.forEach(row -> {
					if (row.getRowNum() != HEADER_ROW_NUMBER) {
						String keyword = keywords.get(row.getRowNum());
						String value = CellUtil.getCell(row, cell.getColumnIndex()).getStringCellValue();
						properties.put(keyword, value);
					}
				});
				resources.add(new Resource(localeName, properties));
			}
		});
		return new ResourceBundle(ResourceBundle.getNameBySheetName(sheet.getSheetName()), resources);
	}

	private CellStyle createHeaderStyle(Workbook book) {
		CellStyle headerStyle = book.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		Font headerFont = book.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		return headerStyle;
	}

	private Row findRow(Sheet sheet, String cellContent) {
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
					return row;
				}
			}
		}

		return null;
	}

}
