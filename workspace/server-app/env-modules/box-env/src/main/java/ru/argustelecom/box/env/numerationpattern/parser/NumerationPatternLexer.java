package ru.argustelecom.box.env.numerationpattern.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.primitives.Chars;

import ru.argustelecom.box.env.numerationpattern.nls.NumerationMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.BusinessException;

/**
 * Разбирает маску вида aabbcc$_09{num:yyMMdd}&lt;AB&gt; на лексемы. В результате разбора ожидаем получить 3 лексемы:
 * aabbcc$_09, num:yyMMdd, AB
 */

public class NumerationPatternLexer {

	private PeekingIterator<Character> it;
	private List<NumerationPatternLexeme> lexemes;
	private StringBuilder currentLexeme;

	private NumerationPatternLexer() {
		lexemes = new ArrayList<>();
		currentLexeme = new StringBuilder();
	}

	public static List<NumerationPatternLexeme> scan(String stringToScan) {
		NumerationPatternLexer lexer = new NumerationPatternLexer();
		return !Strings.isNullOrEmpty(stringToScan) ? lexer.getLexemes(stringToScan) : new ArrayList<>();
	}

	private List<NumerationPatternLexeme> getLexemes(String stringToScan) {
		it = Iterators.peekingIterator(Chars.asList(stringToScan.toCharArray()).iterator());

		while (it.hasNext()) {
			if (isAllowed(it.peek())) {
				literalScan();
			} else if (isOpenBrace(it.peek())) {
				variableScan();
			} else if (isOpenAngleBrace(it.peek())) {
				sequenceScan();
			} else {
				NumerationMessagesBundle messages = LocaleUtils.getMessages(NumerationMessagesBundle.class);
				throw new BusinessException(messages.invalidSymbol(strRemains()));
			}
			clearCurrentLexeme();
		}
		return lexemes;
	}

	private void literalScan() {
		while (it.hasNext() && isAllowed(it.peek())) {
			currentLexeme.append(it.next());
		}

		lexemes.add(new NumerationPatternLexeme(NumerationPatternLexeme.LexemeType.LITERAL, currentLexeme.toString()));
	}

	private void variableScan() {
		NumerationMessagesBundle messages = LocaleUtils.getMessages(NumerationMessagesBundle.class);
		StringBuilder varName = new StringBuilder();
		StringBuilder format = new StringBuilder();
		StringBuilder raw = new StringBuilder();

		String delimiter = "";

		boolean inVarName = false;
		boolean inFormat = false;
		boolean isBracesClosed = false;
		boolean hasDelimiter = false;

		while (it.hasNext()) {
			Character current = it.next();

			if (isOpenBrace(current)) {
				inVarName = true;
				raw.append(current);
				continue;
			}

			if (isClosingBrace(current)) {
				isBracesClosed = true;
				raw.append(current);
				break;
			}

			if (inVarName && isIdChar(current)) {
				varName.append(current);
				raw.append(current);
			} else if (inVarName && isFormatDelimiter(current)) {
				inVarName = false;
				inFormat = true;
				hasDelimiter = true;
				delimiter = Character.toString(current);
				raw.append(delimiter);
			} else if (inFormat && isIdChar(current)) {
				format.append(current);
				raw.append(current);
			} else {
				throw new BusinessException(messages.invalidVariable(raw.toString()));
			}
		}

		currentLexeme.append(varName).append(delimiter).append(format);

		if (varName.length() == 0 || (hasDelimiter && format.length() == 0) || !isBracesClosed) {
			throw new BusinessException(messages.invalidVariable(raw.toString()));
		}

		lexemes.add(new NumerationPatternLexeme(NumerationPatternLexeme.LexemeType.VARIABLE, currentLexeme.toString()));
	}

	private void sequenceScan() {
		boolean isAngleBracketsClosed = false;
		StringBuilder raw = new StringBuilder();
		NumerationMessagesBundle messages = LocaleUtils.getMessages(NumerationMessagesBundle.class);

		while (it.hasNext()) {

			Character current = it.next();
			if (isOpenAngleBrace(current)) {
				raw.append(current);
				continue;
			}

			if (isClosingAngleBrace(current)) {
				isAngleBracketsClosed = true;
				raw.append(current);
				break;
			}

			if (isIdChar(current)) {
				currentLexeme.append(current);
				raw.append(current);
			} else {
				throw new BusinessException(messages.invalidSequence(raw.toString()));
			}
		}

		if (!isAngleBracketsClosed) {
			throw new BusinessException(messages.notEnclosedAngleBracket(currentLexeme.toString()));
		}
		if (currentLexeme.length() == 0) {
			throw new BusinessException(messages.sequenceIsNotSpecified());
		}

		lexemes.add(new NumerationPatternLexeme(NumerationPatternLexeme.LexemeType.SEQUENCE, currentLexeme.toString()));
	}

	private String strRemains() {
		StringBuilder result = new StringBuilder();
		while (it.hasNext()) {
			result.append(it.next());
		}
		return result.toString();
	}

	private boolean isAllowed(char c) {
		return !isOpenBrace(c) && !isOpenAngleBrace(c) && !isClosingBrace(c) && !isClosingAngleBrace(c)
				&& !Character.isWhitespace(c);
	}

	private boolean isOpenBrace(char c) {
		return c == '{';
	}

	private boolean isClosingBrace(char c) {
		return c == '}';
	}

	private boolean isOpenAngleBrace(char c) {
		return c == '<';
	}

	private boolean isClosingAngleBrace(char c) {
		return c == '>';
	}

	private boolean isFormatDelimiter(char c) {
		return c == ':';
	}

	private boolean isIdChar(char c) {
		return Character.isLetterOrDigit(c) || c == '_';
	}

	private void clearCurrentLexeme() {
		currentLexeme = new StringBuilder();
	}
}
