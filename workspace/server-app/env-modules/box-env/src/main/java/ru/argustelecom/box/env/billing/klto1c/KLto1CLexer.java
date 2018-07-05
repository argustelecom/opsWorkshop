package ru.argustelecom.box.env.billing.klto1c;

import static ru.argustelecom.box.env.billing.klto1c.KLto1CLexer.State.SECTION_BEGIN;
import static ru.argustelecom.box.env.billing.klto1c.KLto1CLexer.State.SECTION_END;
import static ru.argustelecom.box.env.billing.klto1c.KLto1CLexer.State.SECTION_TYPE;
import static ru.argustelecom.box.env.billing.klto1c.KLto1CLexer.State.SKIP;
import static ru.argustelecom.box.env.billing.klto1c.KLto1CLexer.State.START;
import static ru.argustelecom.box.env.billing.klto1c.KLto1CLexer.State.VALUE;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Лексер, которые разбирает исходную выгрузку "KL to 1C" на {@linkplain Lexeme лексемы}.
 */
public class KLto1CLexer {

	private Set<Character> escapeChars = new HashSet<>();
	private Set<Character> endLineChars = new HashSet<>();

	{
		escapeChars.add('\r');
		escapeChars.add('\n');
		escapeChars.add('\t');
		escapeChars.add(' ');

		endLineChars.add('\r');
		endLineChars.add('\n');
	}

	private InputStreamReader reader;

	private State state;
	private char currentValue;

	private List<Lexeme> lexemes = new ArrayList<>();
	private StringBuilder lexemeBuilder = new StringBuilder();

	public void init(InputStream inputStream, String encoding)
			throws FileNotFoundException, UnsupportedEncodingException {
		reader = new InputStreamReader(inputStream, encoding);
	}

	public void scan() throws IOException {
		state = START;
		int value;
		while ((value = reader.read()) > -1) {
			currentValue = (char) value;
			switch (state) {
			case START:
				scanStart();
				break;
			case SECTION_BEGIN:
				scanSectionBegin();
				break;
			case SECTION_END:
				scanSectionEnd();
				break;
			case SECTION_TYPE:
				scanSectionType();
				break;
			case VALUE:
				scanValue();
				break;
			case SKIP:
				skip();
				break;
			}
		}

		lexemes.add(new Lexeme(LexemeType.DOCUMENT_END, lexemeBuilder.toString()));
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	/**
	 * Первоначальное сканирование, когда непонятно что считываем.
	 */
	private void scanStart() {
		if (currentValue != '=') {
			if (escapeChars.contains(currentValue)) {
				addLexeme(LexemeType.DOCUMENT_START, SKIP);
				return;
			}

			lexemeBuilder.append(currentValue);

			if (lexemeBuilder.toString().startsWith("Секция"))
				state = SECTION_BEGIN;
			if (lexemeBuilder.toString().startsWith("Конец"))
				state = SECTION_END;
		} else {
			addLexeme(LexemeType.KEY, VALUE);
		}
	}

	/**
	 * Сканируем элемент открытия секции. После себя секция может содержать тип (в случае документа). Сканируем до конца
	 * строки или до символа "=".
	 */
	private void scanSectionBegin() throws IOException {
		lexemeBuilder.append(currentValue);

		int value;
		while ((value = reader.read()) > -1) {
			currentValue = (char) value;
			if (!endLineChars.contains(currentValue)) {
				if (currentValue != '=')
					lexemeBuilder.append(currentValue);
				else {
					addLexeme(LexemeType.SECTION_START, SECTION_TYPE);
					break;
				}
			} else {
				addLexeme(LexemeType.SECTION_START, START);
				break;
			}
		}
	}

	/**
	 * Сканируем элемент закрытия секции. Сканируем до конца строки.
	 */
	private void scanSectionEnd() throws IOException {
		lexemeBuilder.append(currentValue);

		int value;
		while ((value = reader.read()) > -1) {
			currentValue = (char) value;
			if (!endLineChars.contains(currentValue))
				lexemeBuilder.append(currentValue);
			else {
				addLexeme(LexemeType.SECTION_END, START);
				break;
			}
		}
	}

	/**
	 * Сканируем тип секции. Сканируем до конца строки.
	 */
	private void scanSectionType() throws IOException {
		lexemeBuilder.append(currentValue);

		int value;
		while ((value = reader.read()) > -1) {
			currentValue = (char) value;
			if (!endLineChars.contains(currentValue))
				lexemeBuilder.append(currentValue);
			else {
				addLexeme(LexemeType.SECTION_TYPE, START);
				break;
			}
		}
	}

	/**
	 * Сканируем значения свойства. Сканируем до конца строки.
	 */
	private void scanValue() throws IOException {
		lexemeBuilder.append(currentValue);

		int value;
		while ((value = reader.read()) > -1) {
			currentValue = (char) value;
			if (!endLineChars.contains(currentValue))
				lexemeBuilder.append(currentValue);
			else {
				addLexeme(LexemeType.VALUE, SKIP);
				break;
			}
		}
	}

	private void skip() {
		if (!escapeChars.contains(currentValue)) {
			lexemeBuilder.append(currentValue);
			state = START;
		}
	}

	/**
	 * Добовляет прочитанную лексему.
	 *
	 * @param lexemeType
	 *            тип лексемы.
	 * @param nextState
	 *            состояние, в которое надо перейти после добавления лексемы.
	 */
	private void addLexeme(LexemeType lexemeType, State nextState) {
		if (lexemeBuilder.length() != 0)
			lexemes.add(new Lexeme(lexemeType, lexemeBuilder.toString()));
		lexemeBuilder = new StringBuilder();
		state = nextState;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public List<Lexeme> getLexemes() {
		return lexemes;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	enum LexemeType {

		//@formatter:off
		DOCUMENT_START	("DocumentStart", "Начало документа"),
		DOCUMENT_END	("DocumentEnd", "Окончание документа"),
		SECTION_START	("SectionStart", "Начало секции"),
		SECTION_END		("SectionEnd", "Окончание секции"),
		SECTION_TYPE	("SectionType", "Тип секции"),
		KEY				("Key", "Ключ"),
		VALUE			("Value", "Значение");
		//@formatter:on

		private String value;
		private String description;

		LexemeType(String value, String description) {
			this.value = value;
			this.description = description;
		}

		public String getValue() {
			return value;
		}

		public String getDescription() {
			return description;
		}

	}

	enum State {

		//@formatter:off
		START,
		SECTION_BEGIN,
		SECTION_END,
		SECTION_TYPE,
		VALUE,
		SKIP
		//@formatter:on

	}

}