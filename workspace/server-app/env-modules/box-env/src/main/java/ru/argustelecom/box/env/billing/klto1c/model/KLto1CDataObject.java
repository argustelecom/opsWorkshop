package ru.argustelecom.box.env.billing.klto1c.model;

import java.util.List;

public interface KLto1CDataObject {

	List<String> getWarnings();

	void addWarning(String warning);

	List<String> getErrors();

	void addError(String error);

}