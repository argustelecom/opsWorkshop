package ru.argustelecom.box.helper.model;

import java.util.Collections;
import java.util.Map;

public class Resource {

	private static final String FILE_NAME_FORMAT = "%s_%s.properties";
	private ResourceBundle bundle;
	private String localeName;
	private Map<String, String> properties;

	private Resource() {
		super();
	}

	public Resource(String localeName, Map<String, String> properties) {
		this();
		this.localeName = localeName;
		this.properties = properties;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public String getLocaleName() {
		return localeName;
	}

	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	public String getResourceFileName() {
		return String.format(FILE_NAME_FORMAT, getBundle().getName(), getLocaleName());
	}

	public static String getLocaleNameByFileName(String fileName) {
		return fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf("."));
	}

}
