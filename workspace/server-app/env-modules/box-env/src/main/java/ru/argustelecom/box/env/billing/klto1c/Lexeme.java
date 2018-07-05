package ru.argustelecom.box.env.billing.klto1c;

import ru.argustelecom.box.env.billing.klto1c.KLto1CLexer.LexemeType;

public class Lexeme {

	private LexemeType type;
	private String value;

	public Lexeme(LexemeType type, String value) {
		this.type = type;
		this.value = value;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public LexemeType getType() {
		return type;
	}

	public void setType(LexemeType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}