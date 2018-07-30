package ru.argustelecom.box.env.type.model.predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Collection;
import java.util.Iterator;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;

class InPredicate implements TypePropertyPredicate {

	private String leftOperand;
	private Collection<String> rightOperands;

	InPredicate(String leftOperand, String... rightOperands) {
		this(leftOperand, asList(rightOperands));
	}

	InPredicate(String leftOperand, Collection<String> rightOperands) {
		checkRequiredArgument(leftOperand, "leftOperand");
		checkRequiredArgument(rightOperands, "rightOperands");

		this.leftOperand = leftOperand;
		this.rightOperands = rightOperands.stream().filter(s -> !isNullOrEmpty(s)).collect(toList());

		checkArgument(!this.rightOperands.isEmpty());
	}

	@Override
	public String render() {
		StringBuilder sb = new StringBuilder();
		sb.append(leftOperand);
		sb.append(" IN (");

		Iterator<String> it = rightOperands.iterator();
		while (it.hasNext()) {
			String rightOperand = it.next();
			sb.append(rightOperand);
			if (it.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append(")");
		return sb.toString();
	}

}
