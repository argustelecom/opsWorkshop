package ru.argustelecom.box.inf.page.outcome;

public enum ConversationPropagationMode {
	IGNORE(""), CURRENT(""), NONE("none"), BEGIN("begin"), END("end"), NESTED("nested"), JOIN("join");

	private String uriParamValue;

	private ConversationPropagationMode(String uriParamValue) {
		this.uriParamValue = uriParamValue;
	}

	public String getUriParamValue() {
		return uriParamValue;
	}
}