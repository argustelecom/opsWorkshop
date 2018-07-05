package ru.argustelecom.box.helper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ru.argustelecom.box.helper.model.ResourceBundle;

public class Application {

	private static final String DEFAULT_EXCEL_FILE_NAME = "ResourceBundles.xlsx";

	public static void main(String[] args) {
		Mode mode = Mode.lookup(args[0].toUpperCase());
		Path resourcesRootPath = Paths.get(args[1]);
		Path excelPath = args.length > 2 ? Paths.get(args[2]) : Paths.get("..").resolve(DEFAULT_EXCEL_FILE_NAME);
		switch (mode) {
		case BUILD_EXCEL:
			buildExcel(resourcesRootPath, excelPath);
			break;
		case SAVE_DATA:
			saveData(resourcesRootPath, excelPath);
			break;
		default:
			throw new RuntimeException("Неизвестный режим: " + mode);
		}
	}

	private static void buildExcel(Path resourcesRootDir, Path excelPath) {
		List<ResourceBundle> bundles = new ResourceFilesService().read(resourcesRootDir);
		new ExcelService().write(bundles, excelPath);
	}

	private static void saveData(Path resourcesRootDir, Path excelPath) {
		List<ResourceBundle> bundles = new ExcelService().read(excelPath);
		new ResourceFilesService().write(bundles, resourcesRootDir);
	}

	private enum Mode {
		BUILD_EXCEL, SAVE_DATA;

		public static Mode lookup(String value) {
			try {
				return Enum.valueOf(Mode.class, value);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Неизвестное значение " + value, e);
			}
		}
	}
}
