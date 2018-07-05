package ru.argustelecom.box.env.type.model.predicate;

import java.util.Collection;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;

public final class PredicateFactory {

	private PredicateFactory() {
	}

	public static TypePropertyPredicate equal(String leftOperand, String rightOperand) {
		return new ComparisonPredicate(ComparisonPredicate.Operator.EQUAL, leftOperand, rightOperand);
	}

	public static TypePropertyPredicate greater(String leftOperand, String rightOperand) {
		return new ComparisonPredicate(ComparisonPredicate.Operator.GREATER, leftOperand, rightOperand);
	}

	public static TypePropertyPredicate greaterOrEqual(String leftOperand, String rightOperand) {
		return new ComparisonPredicate(ComparisonPredicate.Operator.GREATER_OR_EQUAL, leftOperand, rightOperand);
	}

	public static TypePropertyPredicate less(String leftOperand, String rightOperand) {
		return new ComparisonPredicate(ComparisonPredicate.Operator.LESS, leftOperand, rightOperand);
	}

	public static TypePropertyPredicate lessOrEqual(String leftOperand, String rightOperand) {
		return new ComparisonPredicate(ComparisonPredicate.Operator.LESS_OR_EQUAL, leftOperand, rightOperand);
	}

	public static TypePropertyPredicate not(TypePropertyPredicate predicate) {
		return new NegationPredicate(predicate);
	}

	public static TypePropertyPredicate and(TypePropertyPredicate... predicates) {
		return new LogicalPredicate(LogicalPredicate.Operator.AND, predicates);
	}

	public static TypePropertyPredicate and(Collection<TypePropertyPredicate> predicates) {
		return new LogicalPredicate(LogicalPredicate.Operator.AND, predicates);
	}

	public static TypePropertyPredicate or(TypePropertyPredicate... predicates) {
		return new LogicalPredicate(LogicalPredicate.Operator.OR, predicates);
	}

	public static TypePropertyPredicate or(Collection<TypePropertyPredicate> predicates) {
		return new LogicalPredicate(LogicalPredicate.Operator.OR, predicates);
	}

	public static TypePropertyPredicate in(String leftOperand, String... rightOperands) {
		return new InPredicate(leftOperand, rightOperands);
	}

	public static TypePropertyPredicate in(String leftOperand, Collection<String> rightOperands) {
		return new InPredicate(leftOperand, rightOperands);
	}
}
