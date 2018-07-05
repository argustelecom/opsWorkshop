package ru.argustelecom.box.env.type.model.predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Collection;
import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;

class LogicalPredicate implements TypePropertyPredicate {

	@AllArgsConstructor
	enum Operator {
		AND("AND"), OR("OR");

		@Getter
		String value;
	}

	private Operator operator;
	private Collection<TypePropertyPredicate> predicates;

	LogicalPredicate(Operator operator, TypePropertyPredicate... predicates) {
		this(operator, asList(predicates));
	}

	LogicalPredicate(Operator operator, Collection<TypePropertyPredicate> predicates) {
		checkRequiredArgument(operator, "operator");
		checkRequiredArgument(predicates, "predicates");
		checkArgument(!predicates.isEmpty());

		this.operator = operator;
		this.predicates = unmodifiableCollection(predicates);
	}

	@Override
	public String render() {
		Iterator<TypePropertyPredicate> it = predicates.iterator();
		StringBuilder sb = new StringBuilder();
		sb.append("(");

		while (it.hasNext()) {
			TypePropertyPredicate predicate = it.next();
			sb.append(predicate.render());
			if (it.hasNext()) {
				sb.append(" ");
				sb.append(operator.getValue());
				sb.append(" ");
			}
		}

		sb.append(")");
		return sb.toString();
	}

}
