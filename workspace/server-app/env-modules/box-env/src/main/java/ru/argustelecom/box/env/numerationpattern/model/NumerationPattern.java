package ru.argustelecom.box.env.numerationpattern.model;

import static ru.argustelecom.box.env.numerationpattern.statement.Statement.StatementType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.criteria.Predicate;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.numerationpattern.statement.Statement;
import ru.argustelecom.box.env.numerationpattern.statement.Statement.StatementType.StatementName;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;
import ru.argustelecom.system.inf.exception.SystemException;

@Entity
@Access(AccessType.FIELD)
public class NumerationPattern extends BusinessObject {

	@Getter
	@Setter
	@Column(nullable = false, unique = true)
	private String className;
	@Getter
	@Setter
	@Column(nullable = false)
	private String pattern;
	private String statements;

	protected NumerationPattern() {
	}

	public NumerationPattern(Long id) {
		super(id);
	}

	public List<Statement> getStatements() {
		return Arrays.stream(this.statements.split("(\r\n|\n)")).map(statement -> {
			String[] statementNameValuePair = statement.split(" ");
			StatementType type;
			switch (statementNameValuePair[0]) {
			case StatementName.ADD_LIT:
				type = StatementType.LITERAL;
				break;
			case StatementName.ADD_SEQ:
				type = StatementType.SEQUENCE;
				break;
			case StatementName.ADD_VAR:
				type = StatementType.VARIABLE;
				break;
			default:
				throw new SystemException(String.format("Неизвстная инструкция - %s", statement));
			}
			return new Statement(statementNameValuePair[1], type);
		}).collect(Collectors.toList());
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements.stream().map(Statement::toString).collect(Collectors.joining("\n"));
	}

	// Для тестирования
	public void setStatements(String statements) {
		this.statements = statements;
	}

	public abstract static class AbstractNumerationPatternQuery<T extends NumerationPattern> extends EntityQuery<T> {

		EntityQueryStringFilter<T> classNameFilter = createStringFilter(NumerationPattern_.className);

		public AbstractNumerationPatternQuery(Class<T> entityClass) {
			super(entityClass);
		}

		public Predicate byStatement(String value) {
			return value == null ? null
					: criteriaBuilder().like(root().get(NumerationPattern_.statements), String.format("%%%s%%", value));
		}

		public EntityQueryStringFilter<T> className() {
			return classNameFilter;
		}
	}

	public static class NumerationPatternQuery extends AbstractNumerationPatternQuery<NumerationPattern> {
		public NumerationPatternQuery() {
			super(NumerationPattern.class);
		}
	}

	private static final long serialVersionUID = 3880423434351130393L;
}
