package ru.argustelecom.box.env.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class QueryWrapper<T> {
	private AtomicInteger index = new AtomicInteger();

	private static final String SELECT = "select";
	private static final String FROM = "from";
	private static final String WHERE = "where";
	private static final String ORDER_BY = "order by";
	private static final String COUNT_ALL = "count(*)";
	private static final String DELIMITER = " ";
	private static final String PARAMETER_PREFIX = ":";
	private static final String COMMA = ",";


	@Getter
	private String projection;
	@Getter
	private String from;
	private Predicate firstPredicate;
	private Predicate lastPredicate;
	private List<OrderBy> orderByList = new ArrayList<>();
	private Class<T> clazz;

	protected QueryWrapper(Class<T> clazz, String projection, String from) {
		this.clazz = clazz;
		this.projection = projection;
		this.from = from;
	}

	public void and(Predicate... predicates) {
		for (Predicate predicate : predicates) {
			addPredicate(LogicOperator.AND, predicate);
		}
	}

	public void or(Predicate... predicates) {
		for (Predicate predicate : predicates) {
			addPredicate(LogicOperator.OR, predicate);
		}
	}

	private void addPredicate(LogicOperator logicOperator, Predicate predicate) {
		if (firstPredicate == null) {
			firstPredicate = predicate;
			lastPredicate = firstPredicate;
		} else {
			lastPredicate.compound(predicate, logicOperator);
			lastPredicate = predicate;
		}
	}

	public void addOrderBy(OrderBy orderBy) {
		orderByList.add(orderBy);
	}

	public TypedQuery<T> createTypedQuery(EntityManager em) {
		TypedQuery<T> query = em.createQuery(generateQuery(), clazz);
		applyParameters(query);
		return query;
	}

	public Long countAll(EntityManager em) {
		TypedQuery<Long> query = em.createQuery(generateCountQuery(), Long.class);
		applyParameters(query);
		return query.getSingleResult();
	}

	private <R> void applyParameters(TypedQuery<R> query) {
		if (firstPredicate != null) {
			Predicate next = firstPredicate;
			while (next != null) {
				query.setParameter(next.parameter, next.parameterValue);
				next = next.next;
			}
		}
	}

	public String generateQuery() {
		return generateQuery(projection, true);
	}

	private String generateQuery(String projection, boolean addOrderBy) {
		StringBuilder builder = new StringBuilder();
		//@formatter:off
		builder.append(SELECT).append(DELIMITER).append(projection).append(DELIMITER)
				.append(FROM).append(DELIMITER).append(from).append(DELIMITER);
		if (firstPredicate != null) {
			builder.append(WHERE).append(DELIMITER).append(firstPredicate.toString());
		}
		if (!orderByList.isEmpty() && addOrderBy) {
			builder.append(ORDER_BY).append(DELIMITER)
					.append(orderByList.stream().map(OrderBy::toString).collect(Collectors.joining(COMMA + DELIMITER)));
		}
		//@formatter:on
		return builder.toString();
	}

	private String generateCountQuery() {
		return generateQuery(COUNT_ALL, false);
	}

	public Predicate equals(String operand, Object value) {
		return of(operand, Operator.EQUAL, value);
	}

	public Predicate notEqual(String operand, Object value) {
		return of(operand, Operator.NOT_EQUAL, value);
	}

	public Predicate greaterThen(String operand, Object value) {
		return of(operand, Operator.GREATER_THEN, value);
	}

	public Predicate lessThen(String operand, Object value) {
		return of(operand, Operator.LESS_THEN, value);
	}

	public Predicate greaterOrEqualsThen(String operand, Object value) {
		return of(operand, Operator.GREATER_OR_EQUALS_THEN, value);
	}

	public Predicate lessOrEqualsThen(String operand, Object value) {
		return of(operand, Operator.LESS_OR_EQUALS_WHEN, value);
	}

	public Predicate like(String operand, Object value) {
		return of(operand, Operator.LIKE, value);
	}

	public Predicate in(String operand, Object... value) {
		return of(operand, Operator.IN, Sets.newHashSet(value));
	}

	public Predicate isMember(String operand, Object value) {
		return of(operand, Operator.MEMBER, value);
	}

	private Predicate of(String operand, Operator operator, Object value) {
		return new Predicate(operand, operator,
				value.getClass().getSimpleName().replaceAll("[^\\p{Alpha}]", "") + index.getAndIncrement(), value);
	}

	@Getter
	@AllArgsConstructor
	public static class OrderBy {
		private String path;
		private Order order;

		@Override
		public String toString() {
			return path + DELIMITER + order.getName();
		}
	}

	@Getter
	public enum Order {
		ASC("asc"), DESC("desc");

		private String name;

		Order(String name) {
			this.name = name;
		}
	}

	@Getter
	public enum LogicOperator {
		OR("or"), AND("and");

		private String op;

		LogicOperator(String op) {
			this.op = op;
		}
	}

	public static class Predicate {
		private String operand;
		private Operator operator;
		private String parameter;
		private Object parameterValue;
		private Predicate next;
		private LogicOperator logicOperator;

		private Predicate(String operand, Operator operator, String parameter, Object parameterValue) {
			this.operand = operand;
			this.operator = operator;
			this.parameter = parameter;
			this.parameterValue = parameterValue;
		}

		protected void compound(Predicate next, LogicOperator logicOperator) {
			this.next = next;
			this.logicOperator = logicOperator;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			if (operator == Operator.MEMBER) {
				builder.append(PARAMETER_PREFIX).append(parameter).append(DELIMITER).append(Operator.MEMBER)
						.append(DELIMITER).append(operand).append(DELIMITER);
			} else {
				builder.append(operand).append(DELIMITER).append(operator.op).append(DELIMITER).append(PARAMETER_PREFIX)
						.append(parameter).append(DELIMITER);
			}
			if (next != null) {
				builder.append(logicOperator.op).append(DELIMITER).append(next.toString());
			}
			return builder.toString();
		}
	}

	private enum Operator {
		//@formatter:off
		EQUAL("="),
		NOT_EQUAL("<>"),
		GREATER_THEN(">"),
		LESS_THEN("<"),
		GREATER_OR_EQUALS_THEN(">="),
		LESS_OR_EQUALS_WHEN("<="),
		LIKE("like"),
		IN("in"),
		MEMBER("member");
		//@formatter:on
		private String op;

		Operator(String op) {
			this.op = op;
		}
	}

}
