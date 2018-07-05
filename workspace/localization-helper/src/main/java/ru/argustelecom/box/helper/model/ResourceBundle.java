package ru.argustelecom.box.helper.model;

import java.util.Collections;
import java.util.List;

public class ResourceBundle {

	private static final String MESSAGE_BUNDLE_SUFFIX = "MessagesBundle.i18n";
	private static final String MESSAGE_BUNDLE_EXCEL_SUFFIX = "MB";
	private String name;
	private List<Resource> resources;

	private ResourceBundle() {
		super();
	}

	public ResourceBundle(String name, List<Resource> resources) {
		this();
		this.name = name;
		this.resources = resources;
		this.resources.forEach(resource -> resource.setBundle(this));
	}

	public String getName() {
		return name;
	}

	public List<Resource> getResources() {
		return Collections.unmodifiableList(resources);
	}

	public static String getNameByFileName(String fileName) {
		return fileName.substring(0, fileName.indexOf("_"));
	}

	public static String getNameBySheetName(String sheetName) {
		String result = sheetName;
		if (isMessageBundle(sheetName)) {
			result = sheetName.replace(MESSAGE_BUNDLE_EXCEL_SUFFIX, MESSAGE_BUNDLE_SUFFIX);
		}
		return result;
	}

	public static String getSheetNameByName(String name) {
		String result = name;
		if (isMessageBundle(name)) {
			result = name.replace(MESSAGE_BUNDLE_SUFFIX, MESSAGE_BUNDLE_EXCEL_SUFFIX);
		}
		return result;
	}

	public static boolean isMessageBundle(String name) {
		return name.contains(MESSAGE_BUNDLE_SUFFIX) || name.contains(MESSAGE_BUNDLE_EXCEL_SUFFIX);
	}

}
