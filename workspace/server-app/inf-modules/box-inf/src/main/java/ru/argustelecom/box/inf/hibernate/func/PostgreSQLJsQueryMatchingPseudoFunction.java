package ru.argustelecom.box.inf.hibernate.func;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class PostgreSQLJsQueryMatchingPseudoFunction implements SQLFunction {

	@Override
	@SuppressWarnings("rawtypes")
	public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory) throws QueryException {
		if (args.size() != 2) {
			throw new QueryException("jsq() requires two arguments; found : " + args.size());
		}

		String field = args.get(0).toString();
		String query = args.get(1).toString();

		return field + " @@ CAST(" + query + " AS JSQUERY)";
	}

	@Override
	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
		return StandardBasicTypes.BOOLEAN;
	}

	@Override
	public boolean hasArguments() {
		return true;
	}

	@Override
	public boolean hasParenthesesIfNoArguments() {
		return true;
	}

}
