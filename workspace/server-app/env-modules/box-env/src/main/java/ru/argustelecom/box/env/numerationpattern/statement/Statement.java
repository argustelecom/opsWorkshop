package ru.argustelecom.box.env.numerationpattern.statement;

import static ru.argustelecom.box.env.numerationpattern.statement.Statement.StatementType.StatementName.ADD_LIT;
import static ru.argustelecom.box.env.numerationpattern.statement.Statement.StatementType.StatementName.ADD_SEQ;
import static ru.argustelecom.box.env.numerationpattern.statement.Statement.StatementType.StatementName.ADD_VAR;

import lombok.Getter;

@Getter
public class Statement {
	private String value;
	private StatementType type;

	public Statement(String value, StatementType type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public String toString() {
		return type.getName() + " " + value;
	}

	public enum StatementType {

		LITERAL(ADD_LIT), SEQUENCE(ADD_SEQ), VARIABLE(ADD_VAR);

		private String name;

		StatementType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static class StatementName {
			public static final String ADD_LIT = "addLit";
			public static final String ADD_SEQ = "addSeq";
			public static final String ADD_VAR = "addVar";
		}
	}
}
