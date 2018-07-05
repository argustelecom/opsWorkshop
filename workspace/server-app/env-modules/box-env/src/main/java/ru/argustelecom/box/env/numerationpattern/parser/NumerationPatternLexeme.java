package ru.argustelecom.box.env.numerationpattern.parser;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.numerationpattern.nls.NumerationMessagesBundle;
import ru.argustelecom.box.env.numerationpattern.statement.Statement.StatementType;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@Getter
@AllArgsConstructor
public class NumerationPatternLexeme {

	private LexemeType type;
	private String value;

	@Getter
	@AllArgsConstructor(access = AccessLevel.MODULE)
	public enum LexemeType {
		LITERAL(StatementType.LITERAL), SEQUENCE(StatementType.SEQUENCE), VARIABLE(StatementType.VARIABLE);

		private StatementType statementType;

		public String getName() {
			NumerationMessagesBundle messages = LocaleUtils.getMessages(NumerationMessagesBundle.class);

			switch (this) {
				case LITERAL:
					return messages.lexemeTypeLiteral();
				case VARIABLE:
					return messages.lexemeTypeVariable();
				case SEQUENCE:
					return messages.lexemeTypeSequence();
					default:
						throw new SystemException("Unsupported NumerationPatternLexeme.LexemeType");
			}
		}
	}
}
