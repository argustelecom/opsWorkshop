package ru.argustelecom.box.env.type.model.predicate;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;

class ComparisonPredicate implements TypePropertyPredicate {

	@AllArgsConstructor
	enum Operator {
		EQUAL("="), GREATER(">"), GREATER_OR_EQUAL(">="), LESS("<"), LESS_OR_EQUAL("<=");

		@Getter
		String value;
	}

	private Operator operator;
	private String leftOperand;
	private String rightOperand;

	ComparisonPredicate(Operator operator, String leftOperand, String rightOperand) {
		checkRequiredArgument(operator, "operator");
		checkRequiredArgument(leftOperand, "leftOperand");
		checkRequiredArgument(rightOperand, "rightOperand");

		this.operator = operator;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	@Override
	public String render() {
		return leftOperand + " " + operator.getValue() + " " + rightOperand;
	}
}
